package com.gmail.sofiapatiy.ui.adapters

import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import androidx.recyclerview.widget.RecyclerView
import com.gmail.sofiapatiy.R
import com.gmail.sofiapatiy.data.model.firebase.Urgency
import com.gmail.sofiapatiy.data.model.ui.PlannerTaskInfo
import com.gmail.sofiapatiy.data.model.ui.TaskUiState
import com.gmail.sofiapatiy.data.model.ui.getDailyUrgency
import com.gmail.sofiapatiy.ktx.setViewAndChildrenEnabled
import com.gmail.sofiapatiy.ktx.toFormattedMonth
import com.gmail.sofiapatiy.ktx.toUIDateFormattedString
import com.gmail.sofiapatiy.ktx.toUITimeFormattedString
import com.gmail.sofiapatiy.ui.calendar.CalendarTasksListAdapter
import com.gmail.sofiapatiy.ui.home.TasksListAdapter
import com.gmail.sofiapatiy.ui.view.DateTimeSelector
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButtonToggleGroup
import com.kizitonwose.calendar.core.CalendarDay
import java.time.LocalDate
import java.time.LocalDateTime

// -------

@BindingMethods(
    BindingMethod(
        type = DateTimeSelector::class,
        attribute = "onDateSelect",
        method = "setOnDateSelectListener"
    ),
    BindingMethod(
        type = DateTimeSelector::class,
        attribute = "onTimeSelect",
        method = "setOnTimeSelectListener"
    )
)
class DateTimeSelectorBinding

// -------

@BindingMethods(
    BindingMethod(
        type = MaterialToolbar::class,
        attribute = "onNavigationClick",
        method = "setNavigationOnClickListener"
    )
)
class ToolBarBinding

// -------

@BindingMethods(
    BindingMethod(
        type = MaterialToolbar::class,
        attribute = "onMenuItemClick",
        method = "setOnMenuItemClickListener"
    )
)
class MenuBindingAdapter

// -------

@BindingAdapter("visibleGone")
fun bindVisibleGone(view: View, isVisible: Boolean?) =
    view.apply {
        visibility = when (isVisible) {
            true -> View.VISIBLE
            else -> View.GONE
        }
    }

@BindingAdapter("enableDisable")
fun bindEnableDisable(view: View, isEnabled: Boolean?) =
    view.apply {
        setViewAndChildrenEnabled(isEnabled == true)
    }

@BindingAdapter("selectedFormattedDate")
fun bindSelectedFormattedDate(view: DateTimeSelector, dateTime: LocalDateTime?) =
    view.apply {
        dateTime ?: return@apply
        setDateButtonText(dateTime.toUIDateFormattedString())
    }

@BindingAdapter("selectedFormattedTime")
fun bindSelectedFormattedTime(view: DateTimeSelector, dateTime: LocalDateTime?) =
    view.apply {
        dateTime ?: return@apply
        setTimeButtonText(dateTime.toUITimeFormattedString())
    }

@BindingAdapter("monthStartDate")
fun bindMonthStartDate(view: MaterialToolbar, startDate: LocalDate?) =
    view.apply {
        startDate ?: return@apply
        title = "${resources.getString(R.string.calendar)} - ${startDate.toFormattedMonth()}"
    }

@BindingAdapter("isNewTaskOkToSubmit")
fun bindIsNewTaskOkToSubmit(view: MaterialToolbar, isTaskOkToSubmit: Boolean?) =
    view.apply {
        val submitItem = menu.findItem(R.id.submitTask)
        submitItem.isEnabled = (isTaskOkToSubmit == true)
    }

@BindingAdapter("adjustDate")
fun bindAdjustCalendarDate(
    view: MaterialToolbar,
    selectedDate: LocalDate?
) =
    view.apply {
        selectedDate ?: return@apply
        val adjustItem = menu.findItem(R.id.adjustDate)
        adjustItem.isEnabled = (selectedDate != LocalDate.now())
    }

@BindingAdapter(
    value = ["allCalendarTasks", "calendarDay", "selectedCalendarDate"],
    requireAll = true
)
fun bindSelectedCalendarDate(
    view: TextView,
    allTasks: List<PlannerTaskInfo>?,
    calendarDay: CalendarDay?,
    selectedDate: LocalDate?
) =
    view.apply {
        allTasks ?: return@apply
        calendarDay ?: return@apply
        selectedDate ?: return@apply

        when (val calendarDate = calendarDay.date) {
            selectedDate -> {
                val dailyUrgency =
                    allTasks
                        .filter { it.timeOfDeadline.toLocalDate() == calendarDate }
                        .getDailyUrgency()

                background = when (selectedDate) {
                    LocalDate.now() -> ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.circle_border_current,
                        context.theme
                    )?.apply {
                        val defaultTintList = compoundDrawableTintList
                        when (dailyUrgency) {
                            null -> setTintList(defaultTintList)
                            else -> setTintList(ColorStateList.valueOf(dailyUrgency.colorId))
                        }
                        alpha = 128
                    }

                    else -> ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.circle_border_default,
                        context.theme
                    )?.apply {
                        val defaultTintList = compoundDrawableTintList
                        when (dailyUrgency) {
                            null -> setTintList(defaultTintList)
                            else -> setTintList(ColorStateList.valueOf(dailyUrgency.colorId))
                        }
                        alpha = 128
                    }
                }
            }

            else -> {
                val dailyUrgency =
                    allTasks
                        .filter { it.timeOfDeadline.toLocalDate() == calendarDate }
                        .getDailyUrgency()

                background = when (dailyUrgency) {
                    null -> null
                    else -> ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.round_background,
                        context.theme
                    )?.apply {
                        setTintList(ColorStateList.valueOf(dailyUrgency.colorId))
                        alpha = 128
                    }
                }
            }
        }
    }

@BindingAdapter(value = ["taskDetailsMenuState", "isTaskOkToSubmit"], requireAll = true)
fun bindTaskDetailsMenuState(
    view: MaterialToolbar,
    state: TaskUiState?,
    isTaskOkToSubmit: Boolean?
) =
    view.apply {
        val editItem = menu.findItem(R.id.editTask)
        val deleteItem = menu.findItem(R.id.deleteTask)
        val submitItem = menu.findItem(R.id.submitTask)

        when (state) {
            is TaskUiState.View -> {
                editItem.isEnabled = true
                deleteItem.isEnabled = false
                submitItem.isEnabled = false
            }

            is TaskUiState.Edit -> {
                editItem.isEnabled = false
                deleteItem.isEnabled = true
                submitItem.isEnabled = (isTaskOkToSubmit == true)
            }

            else -> Unit
        }
    }

@BindingAdapter("firebaseTasks")
fun bindFirebaseTasks(view: RecyclerView, tasks: List<PlannerTaskInfo>?) =
    view.apply {
        (adapter as? TasksListAdapter)?.submitList(tasks)
    }

@BindingAdapter("firebaseCalendarTasks")
fun bindFirebaseCalendarTasks(view: RecyclerView, tasks: List<PlannerTaskInfo>?) =
    view.apply {
        (adapter as? CalendarTasksListAdapter)?.submitList(tasks)
    }

@BindingAdapter(value = ["initialUrgency", "isEditEnabled"], requireAll = false)
fun bindInitialUrgency(
    view: MaterialButtonToggleGroup,
    urgency: Urgency?,
    isEditEnabled: Boolean?
) =
    view.apply {
        if (isEditEnabled == null || isEditEnabled == true) {
            when (urgency) {
                is Urgency.Low -> {
                    if (R.id.urgencyLow !in checkedButtonIds) {
                        check(R.id.urgencyLow)
                    }
                }

                is Urgency.Medium -> {
                    if (R.id.urgencyMedium !in checkedButtonIds) {
                        check(R.id.urgencyMedium)
                    }
                }

                is Urgency.High -> {
                    if (R.id.urgencyHigh !in checkedButtonIds) {
                        check(R.id.urgencyHigh)
                    }
                }

                else -> Unit
            }
        }
    }

@BindingAdapter("urgencyIndicator")
fun bindUrgencyIndicator(view: ImageView, urgency: Urgency?) =
    view.apply {
        urgency ?: return@apply
        DrawableCompat.setTint(this.drawable, urgency.colorId)
    }
