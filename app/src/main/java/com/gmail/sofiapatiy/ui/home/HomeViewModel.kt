package com.gmail.sofiapatiy.ui.home

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class HomeViewModel : ViewModel() {

    private val firebaseDatabase = FirebaseDatabase.getInstance(DB_URL)
    private val databaseReference = firebaseDatabase.getReference(TASKS_NODE)

    private val _tasks = MutableStateFlow<Map<String, PlannerTaskFirebase>?>(null)

    val tasks = _tasks.filterNotNull().map { list ->
        list.entries.map { plannerTaskFirebase ->
            plannerTaskFirebase.value.toPlannerTaskInfo(plannerTaskFirebase.key)
        }
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

    override fun onCleared() {
        databaseReference.removeEventListener(firebaseEventListener)
        super.onCleared()
    }

    init {
        databaseReference.addValueEventListener(firebaseEventListener)
    }
}