package com.gmail.sofiapatiy.ui.home.newtask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmail.sofiapatiy.AppConstants.Companion.DB_URL
import com.gmail.sofiapatiy.AppConstants.Companion.TASKS_NODE
import com.gmail.sofiapatiy.data.convertors.toPlannerTaskFirebase
import com.gmail.sofiapatiy.data.model.firebase.Urgency
import com.gmail.sofiapatiy.data.model.ui.PlannerTaskInfo
import com.gmail.sofiapatiy.data.network.OperationStatus
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class NewTaskViewModel : ViewModel() {
    private val _operationStatus = MutableStateFlow<OperationStatus?>(null)
    val operationStatus = _operationStatus.asStateFlow()

    val taskName = MutableStateFlow<String?>(null)
    val taskDescription = MutableStateFlow<String?>(null)

    val completionDateTime = MutableStateFlow<LocalDateTime>(LocalDateTime.now())
    val deadlineDateTime = MutableStateFlow<LocalDateTime>(LocalDateTime.now())
    val reminderDateTime = MutableStateFlow<LocalDateTime>(LocalDateTime.now())

    val isTaskRegular = MutableStateFlow(false)

    private val _urgency = MutableStateFlow<Urgency>(Urgency.Low)
    val urgency = _urgency.asStateFlow()

    val isTaskOkToSubmit = combine(
        taskName,
        taskDescription
    ) { name, description ->
        return@combine when {
            name.isNullOrBlank() or name.isNullOrEmpty() -> false
            description.isNullOrBlank() or description.isNullOrEmpty() -> false
            else -> true
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private val plannerTaskInfo = combine(
        completionDateTime,
        deadlineDateTime,
        reminderDateTime,
        taskName.filterNotNull(),
        taskDescription.filterNotNull()
    ) { completion, deadline, reminder, name, description ->
        return@combine PlannerTaskInfo(
            name = name,
            note = description,
            timeOfCreation = LocalDateTime.now(),
            timeOfCompletion = completion,
            timeOfDeadline = deadline,
            reminder = reminder,
            urgency = Urgency.Low,
            isRegular = false
        )
    }.combine(isTaskRegular) { taskInfo, finalIsRegular ->
        taskInfo.copy(
            isRegular = finalIsRegular
        )
    }.combine(_urgency) { taskInfo, finalUrgency ->
        taskInfo.copy(
            urgency = finalUrgency
        )
    }

    fun setUrgency(u: Urgency) {
        _urgency.update { u }
    }

    fun submitNewTask() {
        viewModelScope.launch(Dispatchers.IO) {
            plannerTaskInfo.firstOrNull()?.let {
                val firebaseDatabase = FirebaseDatabase.getInstance(DB_URL)
                val databaseReference = firebaseDatabase.getReference(TASKS_NODE)

                databaseReference.push().setValue(it.toPlannerTaskFirebase())
                    .addOnSuccessListener {
                        _operationStatus.update { OperationStatus.Success }
                    }
                    .addOnFailureListener { e ->
                        _operationStatus.update { OperationStatus.Failure(e) }
                    }
            }
        }
    }
}