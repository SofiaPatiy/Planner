package com.gmail.sofiapatiy.ui.adapters

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import androidx.recyclerview.widget.RecyclerView
import com.gmail.sofiapatiy.R
import com.gmail.sofiapatiy.data.model.firebase.Urgency
import com.gmail.sofiapatiy.data.model.ui.PlannerTaskInfo
import com.gmail.sofiapatiy.data.model.ui.TaskUiState
import com.gmail.sofiapatiy.ktx.setViewAndChildrenEnabled
import com.gmail.sofiapatiy.ktx.toUIDateFormattedString
import com.gmail.sofiapatiy.ktx.toUITimeFormattedString
import com.gmail.sofiapatiy.ui.home.TasksListAdapter
import com.gmail.sofiapatiy.ui.view.DateTimeSelector
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButtonToggleGroup
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

@BindingAdapter("isNewTaskOkToSubmit")
fun bindIsNewTaskOkToSubmit(view: MaterialToolbar, isTaskOkToSubmit: Boolean?) =
    view.apply {
        val submitItem = menu.findItem(R.id.submitTask)
        submitItem.isEnabled = (isTaskOkToSubmit == true)
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
        val color = when (urgency) {
            is Urgency.Low -> Color.GREEN
            is Urgency.Medium -> Color.YELLOW
            is Urgency.High -> Color.RED
        }
        DrawableCompat.setTint(this.drawable, color)
    }