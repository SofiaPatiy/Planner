package com.gmail.sofiapatiy.ui.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gmail.sofiapatiy.R
import com.gmail.sofiapatiy.data.model.ui.PlannerTaskInfo
import com.gmail.sofiapatiy.databinding.ViewCardCalendarTaskBinding

class CalendarTasksListAdapter(
    private val viewLifecycleOwner: LifecycleOwner,
    private val viewPresenter: CalendarFragment.CalendarPresenter,
    private val viewModel: CalendarViewModel,
) : ListAdapter<PlannerTaskInfo, CalendarTasksListAdapter.CalendarTaskViewHolder>(
    object : DiffUtil.ItemCallback<PlannerTaskInfo>() {
        override fun areItemsTheSame(
            oldItem: PlannerTaskInfo,
            newItem: PlannerTaskInfo
        ) = oldItem.name == newItem.name && oldItem::class == newItem::class

        override fun areContentsTheSame(
            oldItem: PlannerTaskInfo, newItem: PlannerTaskInfo
        ) = oldItem == newItem
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CalendarTaskViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.view_card_calendar_task, parent, false
        )
    )

    override fun onBindViewHolder(holder: CalendarTaskViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class CalendarTaskViewHolder(
        private val binding: ViewCardCalendarTaskBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(plannerTaskInfo: PlannerTaskInfo) {
            binding.apply {
                item = plannerTaskInfo
                calendarViewModel = viewModel
                homePresenter = viewPresenter
                lifecycleOwner = viewLifecycleOwner
                executePendingBindings()
            }
        }
    }
}