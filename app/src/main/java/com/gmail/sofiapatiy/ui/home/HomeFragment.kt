package com.gmail.sofiapatiy.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gmail.sofiapatiy.data.model.ui.PlannerTaskInfo
import com.gmail.sofiapatiy.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()
    private val viewPresenter by lazy { HomePresenter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentHomeBinding.inflate(layoutInflater).apply {
        lifecycleOwner = viewLifecycleOwner
        homeViewModel = viewModel
        homePresenter = viewPresenter

        // avoid top application toolbar being overlapped by system toolbar on modern OSes
        ViewCompat.setOnApplyWindowInsetsListener(actionBar) { appbar, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            if (statusBarHeight != 0)
                appbar.updatePadding(top = statusBarHeight)
            WindowInsetsCompat.CONSUMED
        }

        tasks.adapter = TasksListAdapter(
            viewLifecycleOwner,
            viewPresenter,
            viewModel
        )
    }.root

    inner class HomePresenter {

        fun onClickFAB() {
            findNavController().navigate(HomeFragmentDirections.showNewTask())
        }

        fun onTaskDetails(plannerTaskInfo: PlannerTaskInfo) {
            findNavController().navigate(
                HomeFragmentDirections.showTaskDetails(plannerTaskInfo = plannerTaskInfo)
            )
        }
    }
}