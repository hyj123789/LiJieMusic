package com.example.login.ui.fragment

import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.base.BaseComposeFragment
import com.example.login.ui.viewmodel.LoginViewModel
import com.example.login.R
import com.example.login.ui.screen.MailScreen

class MailFragment : BaseComposeFragment() {
    private val viewModel: LoginViewModel by viewModels()

    @Composable
    override fun Content() {
        MailScreen(
            viewModel = viewModel
        )
    }
}
