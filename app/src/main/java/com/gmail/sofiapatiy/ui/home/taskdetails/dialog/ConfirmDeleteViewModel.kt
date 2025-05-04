package com.gmail.sofiapatiy.ui.home.taskdetails.dialog

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmail.sofiapatiy.AppConstants.Companion.DB_URL
import com.gmail.sofiapatiy.AppConstants.Companion.TASKS_NODE
import com.gmail.sofiapatiy.data.model.ui.PlannerTaskInfo
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ConfirmDeleteViewModel : ViewModel() {
    private val _task = MutableStateFlow<PlannerTaskInfo?>(null)

    fun setTask(task: PlannerTaskInfo) {
        _task.update { task }
    }

    fun deleteTask() {
        viewModelScope.launch(Dispatchers.IO) {
            _task.firstOrNull()?.let { plannerTaskInfo ->
                val firebaseDatabase = FirebaseDatabase.getInstance(DB_URL)
                val databaseReference = firebaseDatabase.reference

                plannerTaskInfo.firebaseKey?.let {
                    databaseReference.child(TASKS_NODE).child(it).removeValue()
                        .addOnSuccessListener {
                        }
                        .addOnFailureListener { e ->
                            Log.d("firebase_data_cancel", "$e")
                        }
                }
            }
        }
    }
}