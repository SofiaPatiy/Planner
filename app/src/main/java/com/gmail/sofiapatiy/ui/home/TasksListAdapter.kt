package com.gmail.sofiapatiy.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gmail.sofiapatiy.R
import com.gmail.sofiapatiy.data.model.ui.PlannerTaskInfo
import com.gmail.sofiapatiy.databinding.ViewCardTaskBinding
import com.gmail.sofiapatiy.ui.home.TasksListAdapter.TaskViewHolder

class TasksListAdapter(
    private val viewLifecycleOwner: LifecycleOwner,
    private val viewPresenter: HomeFragment.HomePresenter,
    private val viewModel: HomeViewModel,
) : ListAdapter<PlannerTaskInfo, TaskViewHolder>(
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TaskViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.view_card_task, parent, false
        )
    )

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class TaskViewHolder(
        private val binding: ViewCardTaskBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(plannerTaskInfo: PlannerTaskInfo) {
            binding.apply {
                item = plannerTaskInfo
                homeViewModel = viewModel
                homePresenter = viewPresenter
                lifecycleOwner = viewLifecycleOwner
                executePendingBindings()
            }
        }
    }
}