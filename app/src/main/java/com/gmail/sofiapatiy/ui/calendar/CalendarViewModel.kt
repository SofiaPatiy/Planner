package com.gmail.sofiapatiy.ui.calendar

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmail.sofiapatiy.AppConstants.Companion.DB_URL
import com.gmail.sofiapatiy.AppConstants.Companion.TASKS_NODE
import com.gmail.sofiapatiy.data.convertors.toPlannerTaskInfo
import com.gmail.sofiapatiy.data.model.firebase.PlannerTaskFirebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate

class CalendarViewModel : ViewModel() {

    private val _monthStartDate = MutableStateFlow<LocalDate>(LocalDate.now().withDayOfMonth(1))
    val monthStartDate = _monthStartDate.asStateFlow()

    private val _selectedDate = MutableStateFlow<LocalDate>(LocalDate.now())
    val selectedDate = _selectedDate.asStateFlow()

    private val firebaseDatabase = FirebaseDatabase.getInstance(DB_URL)
    private val databaseReference = firebaseDatabase.getReference(TASKS_NODE)

    private val _tasks = MutableStateFlow<Map<String, PlannerTaskFirebase>?>(null)
    val allTasks = _tasks.map { firebaseTasksByKey ->
        firebaseTasksByKey?.entries?.map { plannerTaskFirebase ->
            plannerTaskFirebase.value.toPlannerTaskInfo(plannerTaskFirebase.key)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val dailyTasks = combine(
        allTasks,
        _selectedDate
    ) { tasks, date ->
        tasks?.filter { plannerTaskInfo ->
            plannerTaskInfo.timeOfDeadline.toLocalDate() == date
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val isDailyTasksEmpty = dailyTasks.map { list ->
        delay(100) // avoid UI blinks with -empty state-
        list?.isEmpty()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    private val firebaseEventListener = object : ValueEventListener {

        // direct parse -Map of tasks- from Firebase
        private val genericTypeIndicator =
            object : GenericTypeIndicator<Map<String, PlannerTaskFirebase>>() {}

        override fun onDataChange(snapshot: DataSnapshot) {
            runCatching {
                val firebaseTasks = snapshot.getValue(genericTypeIndicator)
                _tasks.update { firebaseTasks }
            }.onFailure {
                Log.e("firebase_data_parser", "$it")
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d("firebase_data_cancel", "$error")
        }

    }

    fun setMonthStartDate(startDate: LocalDate) {
        _monthStartDate.update { startDate }
    }

    fun setCalendarSelectedDate(startDate: LocalDate) {
        _selectedDate.update { startDate }
    }

    override fun onCleared() {
        databaseReference.removeEventListener(firebaseEventListener)
        super.onCleared()
    }

    init {
        databaseReference.addValueEventListener(firebaseEventListener)
    }
}