package com.gmail.sofiapatiy.ui.home.taskdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmail.sofiapatiy.AppConstants.Companion.DB_URL
import com.gmail.sofiapatiy.AppConstants.Companion.TASKS_NODE
import com.gmail.sofiapatiy.data.convertors.toPlannerTaskFirebase
import com.gmail.sofiapatiy.data.model.firebase.Urgency
import com.gmail.sofiapatiy.data.model.ui.PlannerTaskInfo
import com.gmail.sofiapatiy.data.model.ui.TaskUiState
import com.gmail.sofiapatiy.data.network.OperationStatus
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class TaskDetailsViewModel : ViewModel() {
    private val _task = MutableStateFlow<PlannerTaskInfo?>(null)

    private val _taskUiState = MutableStateFlow<TaskUiState>(TaskUiState.View)
    val taskUiState = _taskUiState.asStateFlow()

    val isTaskDetailsEnabled = taskUiState.map {
        it is TaskUiState.Edit
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

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
    }.combine(_task) { taskInfo, originalTaskInfo ->
        taskInfo.copy(
            firebaseKey = originalTaskInfo?.firebaseKey
        )
    }

    fun setUrgency(u: Urgency) {
        _urgency.update { u }
    }

    fun setTaskUiState(state: TaskUiState) {
        _taskUiState.update { state }
    }

    fun setTask(task: PlannerTaskInfo) {
        _task.update { task }
    }

    fun submitTask() {
        viewModelScope.launch(Dispatchers.IO) {
            plannerTaskInfo.firstOrNull()?.let { task ->
                val firebaseDatabase = FirebaseDatabase.getInstance(DB_URL)
                val databaseReference = firebaseDatabase.reference

                task.firebaseKey?.let { key ->
                    databaseReference.child(TASKS_NODE).child(key)
                        .setValue(task.toPlannerTaskFirebase())
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

    init {
        // set initial values from original firebase-received task, once it updated
        _task.filterNotNull().onEach { originalTaskInfo ->
            taskName.update { originalTaskInfo.name }
            taskDescription.update { originalTaskInfo.note }
            completionDateTime.update { originalTaskInfo.timeOfCompletion }
            deadlineDateTime.update { originalTaskInfo.timeOfDeadline }
            reminderDateTime.update { originalTaskInfo.reminder }
            isTaskRegular.update { originalTaskInfo.isRegular }
            _urgency.update { originalTaskInfo.urgency }
        }.launchIn(viewModelScope)
    }
}