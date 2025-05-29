package com.gmail.sofiapatiy.ui.auth.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gmail.sofiapatiy.R
import com.gmail.sofiapatiy.data.network.OperationStatus
import com.gmail.sofiapatiy.databinding.FragmentSignupBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class SignupFragment : Fragment() {

    private val viewModel: SignupViewModel by viewModels()
    private val viewPresenter by lazy { SignupPresenter() }

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentSignupBinding.inflate(layoutInflater).apply {
        _binding = this
        lifecycleOwner = viewLifecycleOwner
        signupViewModel = viewModel
        presenter = viewPresenter
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel.operationStatus.filterNotNull().onEach {
                when (it) {
                    is OperationStatus.Success -> viewPresenter.onNavigateGraphStart()
                    is OperationStatus.Failure -> {
                        Toast.makeText(requireActivity(), it.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class SignupPresenter {

        fun onClickSignup() {
            viewModel.submitNewUser()
        }

        fun onNavigateGraphStart() {
            findNavController().navigate(R.id.mobile_navigation)
        }

        fun onNavigateBack() {
            findNavController().navigateUp()
        }
    }
}