package com.gmail.sofiapatiy.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmail.sofiapatiy.repository.PlannerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    repository: PlannerRepository
) : ViewModel() {

    private val _monthStartDate = MutableStateFlow<LocalDate>(LocalDate.now().withDayOfMonth(1))
    val monthStartDate = _monthStartDate.asStateFlow()

    private val _selectedDate = MutableStateFlow<LocalDate>(LocalDate.now())
    val selectedDate = _selectedDate.asStateFlow()

    val allTasks = repository.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val dailyTasks = combine(
        allTasks,
        _selectedDate
    ) { tasks, date ->
        tasks.filter { plannerTaskInfo ->
            plannerTaskInfo.timeOfDeadline.toLocalDate() == date
        }.sortedBy {
            it.urgency.sortPriority
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val isDailyTasksEmpty = dailyTasks.map { list ->
        delay(50) // avoid UI blinks with -empty state-
        list?.isEmpty()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun setMonthStartDate(startDate: LocalDate) {
        _monthStartDate.update { startDate }
    }

    fun setCalendarSelectedDate(startDate: LocalDate) {
        _selectedDate.update { startDate }
    }
}