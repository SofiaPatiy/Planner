package com.gmail.sofiapatiy.ui.record

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.gmail.sofiapatiy.databinding.FragmentRecordBinding

class RecordFragment : Fragment() {

    private val viewModel: RecordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentRecordBinding.inflate(layoutInflater).apply {
        lifecycleOwner = viewLifecycleOwner
        recordViewModel = viewModel
    }.root
}