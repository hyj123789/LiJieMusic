package com.example.login.ui.fragment

import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.base.BaseComposeFragment
import com.example.login.ui.viewmodel.LoginViewModel
import com.example.login.R
import com.example.login.ui.screen.LoginScreen
import com.example.therouter.RoutePath
import com.therouter.TheRouter

class LoginFragment : BaseComposeFragment() {
    private val viewModel: LoginViewModel by viewModels()

    @Composable
    override fun Content() {
        LoginScreen(
            viewModel = viewModel,
            onNavigateToMail = {
                findNavController().navigate(R.id.action_loginFragment_to_mailFragment)
            },
            onNavigateToScan = {
                viewModel.loginByScanInPhone()
                findNavController().navigate(R.id.action_loginFragment_to_scanFragment)
            },
            onGuestLogin = { viewModel.loginByGuest() },
            onLoginSuccess = {
                TheRouter.build(RoutePath.MAIN_ACTIVITY).navigation()
                activity?.finish()
            }
        )
    }
}
