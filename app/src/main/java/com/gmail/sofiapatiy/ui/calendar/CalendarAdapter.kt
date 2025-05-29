package com.gmail.sofiapatiy.ui.calendar

import android.graphics.Typeface
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.gmail.sofiapatiy.databinding.ViewCalendarDayBinding
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.LocalDate

class CalendarAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: CalendarViewModel,
    private val viewPresenter: CalendarFragment.CalendarPresenter
) : MonthDayBinder<DayViewContainer> {

    override fun create(view: View) = DayViewContainer(
        lifecycleOwner,
        ViewCalendarDayBinding.bind(view),
        viewModel,
        viewPresenter
    )

    override fun bind(container: DayViewContainer, data: CalendarDay) {
        container.bind(data)
    }
}

class DayViewContainer(
    private val lifecycleOwner: LifecycleOwner,
    private val binding: ViewCalendarDayBinding,
    private val viewModel: CalendarViewModel,
    private val viewPresenter: CalendarFragment.CalendarPresenter
) : ViewContainer(binding.root) {

    fun bind(item: CalendarDay) {
        binding.item = item
        binding.lifecycleOwner = lifecycleOwner
        binding.model = viewModel
        binding.calendarPresenter = viewPresenter

        binding.exOneDayText.apply {
            text = item.date.dayOfMonth.toString()

            val fontStyle = when (item.date) {
                LocalDate.now() -> Typeface.BOLD
                else -> Typeface.NORMAL
            }
            setTypeface(typeface, fontStyle)

            alpha = when (item.position) {
                DayPosition.MonthDate -> 1.0f
                else -> 0.5f
            }
        }

        binding.executePendingBindings()
    }
}