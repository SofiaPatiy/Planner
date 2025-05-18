package com.gmail.sofiapatiy.ui.home.taskdetails.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.gmail.sofiapatiy.R
import com.gmail.sofiapatiy.ui.home.taskdetails.TaskDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConfirmDeleteDialogFragment : DialogFragment() {

    private val viewModel: TaskDetailsViewModel by hiltNavGraphViewModels<TaskDetailsViewModel>(R.id.nav_graph_task_details)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireActivity())
            .setTitle(resources.getString(R.string.warning))
            .setMessage(resources.getString(R.string.are_you_sure))
            .setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
                viewModel.deleteTask()
            }
            .setNegativeButton(resources.getString(R.string.cancel_button)) { _, _ ->

            }
            .create()
    }
}