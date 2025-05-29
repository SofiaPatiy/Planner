package com.gmail.sofiapatiy.ui.home.taskdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.databinding.adapters.ListenerUtil
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gmail.sofiapatiy.R
import com.gmail.sofiapatiy.data.model.firebase.Urgency
import com.gmail.sofiapatiy.data.model.ui.TaskUiState
import com.gmail.sofiapatiy.data.network.OperationStatus
import com.gmail.sofiapatiy.databinding.FragmentTaskDetailsBinding
import com.gmail.sofiapatiy.ktx.asLocalDateTime
import com.gmail.sofiapatiy.ktx.asMilliseconds
import com.gmail.sofiapatiy.ktx.showOnScreen
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

@AndroidEntryPoint
class TaskDetailsFragment : Fragment() {

    private val viewModel: TaskDetailsViewModel by hiltNavGraphViewModels<TaskDetailsViewModel>(R.id.nav_graph_task_details)
    private val viewPresenter by lazy { TaskDetailsPresenter() }

    private val urgencyListener =
        MaterialButtonToggleGroup.OnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.urgencyLow -> viewModel.setUrgency(Urgency.Low)
                    R.id.urgencyMedium -> viewModel.setUrgency(Urgency.Medium)
                    R.id.urgencyHigh -> viewModel.setUrgency(Urgency.High)
                    else -> Unit
                }
            }
        }

    private var _binding: FragmentTaskDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentTaskDetailsBinding.inflate(layoutInflater).apply {
        _binding = this
        lifecycleOwner = viewLifecycleOwner
        taskViewModel = viewModel
        presenter = viewPresenter
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            ViewCompat.setOnApplyWindowInsetsListener(actionBar) { appbar, insets ->
                val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
                if (statusBarHeight != 0)
                    appbar.updatePadding(top = statusBarHeight)
                WindowInsetsCompat.CONSUMED
            }

            viewModel.operationStatus.filterNotNull().onEach {
                when (it) {
                    is OperationStatus.Success -> viewPresenter.onNavigateBack()
                    is OperationStatus.Failure -> {
                        Toast.makeText(requireActivity(), it.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)

            // take care of listener, to avoid memory leaks
            val oldUrgencyListener = ListenerUtil.trackListener(
                urgencyGroup,
                urgencyListener,
                R.id.urgencyListener
            )
            if (oldUrgencyListener != null) {
                urgencyGroup.removeOnButtonCheckedListener(oldUrgencyListener)
            }
            urgencyGroup.addOnButtonCheckedListener(urgencyListener)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class TaskDetailsPresenter {

        fun onActionItemClicked(menuItem: MenuItem, taskId: Long): Boolean {
            when (menuItem.itemId) {
                R.id.editTask -> viewModel.setTaskUiState(TaskUiState.Edit)

                R.id.deleteTask -> onDeleteTask(taskId)

                R.id.submitTask -> viewModel.updateTask()

                else -> Unit
            }
            return true
        }

        private fun onDeleteTask(taskId: Long) {
            findNavController().navigate(
                TaskDetailsFragmentDirections.showConfirmationDeleteDialog(
                    taskId = taskId
                )
            )
        }

        fun onCompletionDateSelect() {
            MaterialDatePicker
                .Builder
                .datePicker()
                .setSelection(viewModel.completionDateTime.value.asMilliseconds())
                .build()
                .apply {
                    addOnPositiveButtonClickListener { milliseconds ->
                        viewModel.completionDateTime.update {
                            milliseconds.asLocalDateTime().withHour(it.hour).withMinute(it.minute)
                        }
                    }
                }.showOnScreen(parentFragmentManager)
        }

        fun onDeadlineDateSelect() {
            MaterialDatePicker
                .Builder
                .datePicker()
                .setSelection(viewModel.deadlineDateTime.value.asMilliseconds())
                .build()
                .apply {
                    addOnPositiveButtonClickListener { milliseconds ->
                        viewModel.deadlineDateTime.update {
                            milliseconds.asLocalDateTime().withHour(it.hour).withMinute(it.minute)
                        }
                    }
                }.showOnScreen(parentFragmentManager)
        }

        fun onReminderDateSelect() {
            MaterialDatePicker
                .Builder
                .datePicker()
                .setSelection(viewModel.reminderDateTime.value.asMilliseconds())
                .build()
                .apply {
                    addOnPositiveButtonClickListener { milliseconds ->
                        viewModel.reminderDateTime.update {
                            milliseconds.asLocalDateTime().withHour(it.hour).withMinute(it.minute)
                        }
                    }
                }.showOnScreen(parentFragmentManager)
        }

        fun onCompletionTimeSelect() {
            MaterialTimePicker
                .Builder()
                .setHour(viewModel.completionDateTime.value.hour)
                .setMinute(viewModel.completionDateTime.value.minute)
                .build()
                .apply {
                    addOnPositiveButtonClickListener {
                        viewModel.completionDateTime.update { it.withHour(hour).withMinute(minute) }
                    }
                }.showOnScreen(parentFragmentManager)
        }

        fun onDeadlineTimeSelect() {
            MaterialTimePicker
                .Builder()
                .setHour(viewModel.deadlineDateTime.value.hour)
                .setMinute(viewModel.deadlineDateTime.value.minute)
                .build()
                .apply {
                    addOnPositiveButtonClickListener {
                        viewModel.deadlineDateTime.update { it.withHour(hour).withMinute(minute) }
                    }
                }.showOnScreen(parentFragmentManager)
        }

        fun onReminderTimeSelect() {
            MaterialTimePicker
                .Builder()
                .setHour(viewModel.reminderDateTime.value.hour)
                .setMinute(viewModel.reminderDateTime.value.minute)
                .build()
                .apply {
                    addOnPositiveButtonClickListener {
                        viewModel.reminderDateTime.update { it.withHour(hour).withMinute(minute) }
                    }
                }.showOnScreen(parentFragmentManager)
        }

        fun onNavigateBack() {
            findNavController().navigateUp()
        }
    }
}