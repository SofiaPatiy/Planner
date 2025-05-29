package com.gmail.sofiapatiy.ui.settings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gmail.sofiapatiy.R
import com.gmail.sofiapatiy.databinding.FragmentSettingsBinding
import com.gmail.sofiapatiy.ktx.isDarkThemeOn
import com.gmail.sofiapatiy.service.CalendarForegroundService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.update

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModels()
    private val viewPresenter by lazy { SettingsPresenter() }

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentSettingsBinding.inflate(layoutInflater).apply {
        _binding = this
        lifecycleOwner = viewLifecycleOwner
        settingsViewModel = viewModel
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

            viewModel.isAppUseDarkTheme.update {
                requireActivity().isDarkThemeOn()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun stopNotificationService() {
        val notificationServiceIntent =
            Intent(requireContext(), CalendarForegroundService::class.java)
        try {
            requireContext().stopService(notificationServiceIntent)
        } catch (e: Exception) {
            Log.e("SettingsFragment", "Failed to stop service: ${e.message}")
        }
    }

    inner class SettingsPresenter {

        fun onClickLogout() {
            stopNotificationService().also {
                findNavController().navigate(R.id.navigation_auth)
            }
        }
    }
}