package com.gmail.sofiapatiy.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.core.view.updatePadding
import androidx.databinding.adapters.ListenerUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gmail.sofiapatiy.R
import com.gmail.sofiapatiy.data.model.ui.PlannerTaskInfo
import com.gmail.sofiapatiy.databinding.FragmentCalendarBinding
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.MonthScrollListener
import com.kizitonwose.calendar.view.ViewContainer
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

class CalendarFragment : Fragment() {

    private val viewModel: CalendarViewModel by viewModels()
    private val viewPresenter by lazy { CalendarPresenter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentCalendarBinding.inflate(layoutInflater).apply {
        lifecycleOwner = viewLifecycleOwner
        calendarViewModel = viewModel
        calendarPresenter = viewPresenter

        // avoid top application toolbar being overlapped by system toolbar on modern OSes
        ViewCompat.setOnApplyWindowInsetsListener(actionBar) { appbar, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            if (statusBarHeight != 0)
                appbar.updatePadding(top = statusBarHeight)
            WindowInsetsCompat.CONSUMED
        }

        with(calendarView) {
            val currentMonth = YearMonth.now()
            val startCalendarMonth = currentMonth.minusYears(1)
            val endCalendarMonth = currentMonth.plusYears(1)
            val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY)

            // each calendar day cell
            dayBinder = CalendarAdapter(
                viewLifecycleOwner,
                viewModel,
                viewPresenter
            )

            // top header with weekDays
            monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)
                override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                    if (container.titlesContainer.tag == null) {
                        container.titlesContainer.tag = data.yearMonth

                        container.titlesContainer.children.map { it as TextView }
                            .forEachIndexed { index, textView ->
                                val dayOfWeek = daysOfWeek[index]
                                val title =
                                    dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                                textView.text = title
                            }
                    }
                }
            }

            // avoid memory leaks on heavy CalendarView's scrollListener
            val newListener = object : MonthScrollListener {
                override fun invoke(p1: CalendarMonth) {
                    viewModel.setMonthStartDate(p1.yearMonth.atStartOfMonth())
                }
            }
            val oldListener =
                ListenerUtil.trackListener(this, newListener, R.id.monthCalendarListener)

            if (oldListener != null) {
                monthScrollListener = null
            }
            monthScrollListener = newListener

            // run calendar and set to current date
            setup(startCalendarMonth, endCalendarMonth, daysOfWeek.first())
            scrollToDate(LocalDate.now())
        }

        dayAgendaList.adapter = CalendarTasksListAdapter(
            viewLifecycleOwner,
            viewPresenter,
            viewModel
        )

        // scroll calendar to current date, if -adjust- button was pushed
        viewModel.monthStartDate.onEach { monthStartDate ->
            when (calendarView.findFirstVisibleMonth()?.yearMonth?.atStartOfMonth()) {
                monthStartDate -> Unit // already at current month/year
                else -> calendarView.smoothScrollToDate(LocalDate.now())
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

    }.root

    inner class CalendarPresenter {

        fun onActionItemClicked(menuItem: MenuItem): Boolean {
            when (menuItem.itemId) {
                R.id.adjustDate -> {
                    viewModel.setCalendarSelectedDate(LocalDate.now())
                    viewModel.setMonthStartDate(LocalDate.now().withDayOfMonth(1))
                }

                else -> Unit
            }
            return true
        }

        fun onDateSelect(date: LocalDate) {
            viewModel.setCalendarSelectedDate(date)
        }

        fun onCalendarTaskDetails(plannerTaskInfo: PlannerTaskInfo) {
            findNavController().navigate(
                CalendarFragmentDirections.showTaskDetails(plannerTaskInfo = plannerTaskInfo)
            )
        }
    }

    private class MonthViewContainer(view: View) : ViewContainer(view) {
        val titlesContainer = view as ViewGroup
    }
}