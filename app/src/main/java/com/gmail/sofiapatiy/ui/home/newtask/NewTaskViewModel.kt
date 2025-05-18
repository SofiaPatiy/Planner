package com.gmail.sofiapatiy.ui.home.newtask

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmail.sofiapatiy.data.model.firebase.Urgency
import com.gmail.sofiapatiy.data.model.ui.PlannerTaskInfo
import com.gmail.sofiapatiy.data.network.OperationStatus
import com.gmail.sofiapatiy.repository.PlannerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class NewTaskViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @Named("Coroutine.Context.IO") private val coroutineContext: CoroutineContext,
    private val repository: PlannerRepository
) : ViewModel() {
    private val _userId = savedStateHandle.get<String>("userId")

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
            databaseTaskId = 0L,
            userId = _userId ?: "",
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
        viewModelScope.launch(coroutineContext) {
            runCatching {
                plannerTaskInfo.firstOrNull()?.let { newTask ->
                    repository.submitNewTask(
                        task = newTask,
                        onSuccessListener = {
                            _operationStatus.update { OperationStatus.Success }
                        },
                        onFailureListener = { e ->
                            _operationStatus.update { OperationStatus.Failure(e) }
                        }

                    )
                }
            }.onFailure {
                Log.e("submit_new_task", "$it")
            }
        }
    }
}