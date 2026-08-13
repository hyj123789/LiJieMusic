package com.example.login.ui.fragment

import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import com.example.base.BaseComposeFragment
import com.example.login.ui.viewmodel.LoginViewModel
import com.example.login.ui.screen.ScanScreen
import com.example.therouter.RoutePath
import com.therouter.TheRouter

class ScanFragment : BaseComposeFragment() {
    private val viewModel: LoginViewModel by viewModels()

    @Composable
    override fun Content() {
        ScanScreen(
            viewModel = viewModel,
            onLoginSuccess = {
                TheRouter.build(RoutePath.MAIN_ACTIVITY).navigation()
                activity?.finish()
            }
        )
    }
}
