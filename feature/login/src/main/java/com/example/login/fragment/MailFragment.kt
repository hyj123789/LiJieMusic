package com.example.login.fragment

import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.base.BaseComposeFragment
import com.example.login.LoginViewModel
import com.example.login.R
import com.example.login.ui.MailScreen

class MailFragment : BaseComposeFragment() {
    private val viewModel: LoginViewModel by viewModels()

    @Composable
    override fun Content() {
        MailScreen(
            viewModel = viewModel,
            onBackToPhone = {
                findNavController().navigate(R.id.action_mailFragment_to_loginFragment)
            }
        )
    }
}
