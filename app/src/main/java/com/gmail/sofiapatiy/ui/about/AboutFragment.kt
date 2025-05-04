package com.gmail.sofiapatiy.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.gmail.sofiapatiy.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {

    private val viewModel: AboutViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentAboutBinding.inflate(layoutInflater).apply {
        lifecycleOwner = viewLifecycleOwner
        model = viewModel

        ViewCompat.setOnApplyWindowInsetsListener(actionBar) { appbar, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            if (statusBarHeight != 0)
                appbar.updatePadding(top = statusBarHeight)
            WindowInsetsCompat.CONSUMED
        }
    }.root
}