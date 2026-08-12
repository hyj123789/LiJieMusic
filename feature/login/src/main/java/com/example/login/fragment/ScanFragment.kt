package com.example.login.fragment

import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import com.example.base.BaseComposeFragment
import com.example.login.LoginViewModel
import com.example.login.ui.ScanScreen

class ScanFragment : BaseComposeFragment() {
    private val viewModel: LoginViewModel by viewModels()

    @Composable
    override fun Content() {
        ScanScreen(
            viewModel = viewModel,
            onLoginSuccess = {
                com.therouter.TheRouter.build(com.example.therouter.RoutePath.MAIN_ACTIVITY).navigation()
                activity?.finish()
            }
        )
    }
}
