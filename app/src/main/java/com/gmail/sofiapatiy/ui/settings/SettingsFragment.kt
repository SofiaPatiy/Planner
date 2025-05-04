package com.gmail.sofiapatiy.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.gmail.sofiapatiy.databinding.FragmentSettingsBinding
import com.gmail.sofiapatiy.ktx.isDarkThemeOn
import kotlinx.coroutines.flow.update

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentSettingsBinding.inflate(layoutInflater).apply {
        lifecycleOwner = viewLifecycleOwner
        settingsViewModel = viewModel

        ViewCompat.setOnApplyWindowInsetsListener(actionBar) { appbar, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            if (statusBarHeight != 0)
                appbar.updatePadding(top = statusBarHeight)
            WindowInsetsCompat.CONSUMED
        }

        viewModel.isAppUseDarkTheme.update {
            requireActivity().isDarkThemeOn()
        }
    }.root
}