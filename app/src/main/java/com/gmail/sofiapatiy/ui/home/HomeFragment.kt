package com.gmail.sofiapatiy.ui.home

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gmail.sofiapatiy.databinding.FragmentHomeBinding
import com.gmail.sofiapatiy.service.CalendarForegroundService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()
    private val viewPresenter by lazy { HomePresenter() }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                startNotificationService()
            } else {
                Toast.makeText(
                    requireActivity(),
                    "Notification permission denied. Cannot start service.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentHomeBinding.inflate(layoutInflater).apply {
        _binding = this
        lifecycleOwner = viewLifecycleOwner
        homeViewModel = viewModel
        homePresenter = viewPresenter
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
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

            // Request notification permission on Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        requireActivity(),
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    startNotificationService()
                } else {
                    requestNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            } else {
                startNotificationService() // For older Android versions, permission is granted by default
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun startNotificationService() {
        val notificationServiceIntent =
            Intent(requireContext(), CalendarForegroundService::class.java)
        try {
            ContextCompat.startForegroundService(requireContext(), notificationServiceIntent)
        } catch (e: Exception) {
            Log.e("HomeFragment", "Failed to start service: ${e.message}")
        }
    }

    inner class HomePresenter {

        fun onClickFAB() {
            findNavController().navigate(HomeFragmentDirections.showNewTask(userId = viewModel.userId))
        }

        fun onTaskDetails(taskId: Long) {
            findNavController().navigate(
                HomeFragmentDirections.showTaskDetails(taskId = taskId)
            )
        }
    }
}