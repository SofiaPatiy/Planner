package com.gmail.sofiapatiy.ui.auth

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gmail.sofiapatiy.R
import com.gmail.sofiapatiy.databinding.FragmentAuthBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels()
    private val viewPresenter by lazy { AuthPresenter() }

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentAuthBinding.inflate(layoutInflater).apply {
        _binding = this
        lifecycleOwner = viewLifecycleOwner
        authViewModel = viewModel
        presenter = viewPresenter
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            val signUpText = getString(R.string.signup_new_user)
            val spannableString = SpannableString(signUpText).apply {
                setSpan(UnderlineSpan(), 0, signUpText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            signUpLink.text = spannableString
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class AuthPresenter {

        fun onClickLogin() {
            findNavController().navigate(AuthFragmentDirections.showHome(userId = viewModel.validUserId.value))
        }

        fun onClickSignup() {
            findNavController().navigate(AuthFragmentDirections.showSignup())
        }
    }
}