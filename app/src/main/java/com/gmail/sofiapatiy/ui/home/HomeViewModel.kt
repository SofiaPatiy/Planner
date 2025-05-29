package com.gmail.sofiapatiy.ui.home

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmail.sofiapatiy.AppConstants.Companion.DB_URL
import com.gmail.sofiapatiy.AppConstants.Companion.TASKS_NODE
import com.gmail.sofiapatiy.data.convertors.toPlannerTaskModel
import com.gmail.sofiapatiy.data.model.firebase.PlannerTaskFirebase
import com.gmail.sofiapatiy.repository.PlannerRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class HomeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @Named("Coroutine.Context.IO") coroutineContext: CoroutineContext,
    private val repository: PlannerRepository
) : ViewModel() {

    private val _userId = savedStateHandle.get<String>("userId")
    val userId = _userId

    private val firebaseDatabase = FirebaseDatabase.getInstance(DB_URL)
    private val databaseReference = firebaseDatabase.getReference(TASKS_NODE)

    val tasks = repository.getAllTasks()
        .map {list->
            list.sortedBy {
                it.urgency.sortPriority
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val firebaseEventListener = object : ValueEventListener {

        // direct parse -Map of tasks- from Firebase
        private val taskTypeIndicator =
            object : GenericTypeIndicator<Map<String, PlannerTaskFirebase>>() {}

        override fun onDataChange(snapshot: DataSnapshot) {
            runCatching {
                val firebaseTasks = snapshot.getValue(taskTypeIndicator)
                val databaseReadyTasks = firebaseTasks?.entries
                    ?.filter { plannerTaskFirebase ->
                        plannerTaskFirebase.value.userId == _userId
                    }?.map { plannerTaskFirebase ->
                        plannerTaskFirebase.value.toPlannerTaskModel(plannerTaskFirebase.key)
                    }

                viewModelScope.launch(coroutineContext) {
                    when (databaseReadyTasks) {
                        null -> repository.deleteUserTasks()
                        else -> repository.refreshUserTasks(databaseReadyTasks)
                    }
                }
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