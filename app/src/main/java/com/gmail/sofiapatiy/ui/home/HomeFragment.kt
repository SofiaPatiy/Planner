package com.gmail.sofiapatiy.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gmail.sofiapatiy.R
import com.gmail.sofiapatiy.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    private val viewPresenter by lazy {
        HomePresenter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentHomeBinding.inflate(layoutInflater).apply {
        lifecycleOwner = viewLifecycleOwner
        homeViewModel = viewModel
        homePresenter = viewPresenter
    }.root


    inner class HomePresenter {

        fun onClickFAB() {
            findNavController().navigate(R.id.showDetails)
        }
    }
}