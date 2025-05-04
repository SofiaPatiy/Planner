package com.gmail.sofiapatiy.ui.home.taskdetails.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gmail.sofiapatiy.R

class ConfirmDeleteDialogFragment : DialogFragment() {

    private val args by lazy {
        ConfirmDeleteDialogFragmentArgs.fromBundle(arguments ?: Bundle())
    }

    private val viewModel: ConfirmDeleteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        args.plannerTaskInfo?.let {
            viewModel.setTask(it)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireActivity())
            .setTitle(resources.getString(R.string.warning))
            .setMessage(resources.getString(R.string.are_you_sure))
            .setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
                // delete, and return to graph start destination
                viewModel.deleteTask().also {
                    findNavController().navigate(R.id.mobile_navigation)
                }
            }
            .setNegativeButton(resources.getString(R.string.cancel_button)) { _, _ ->

            }
            .create()
    }
}