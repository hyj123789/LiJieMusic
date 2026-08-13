package com.example.profile.ui.fragment

import androidx.compose.runtime.Composable
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.base.BaseComposeFragment
import com.example.profile.ui.screen.ProfileScreen
import com.example.profile.ui.viewmodel.ProfileViewModel

class ProfileFragment : BaseComposeFragment() {
    private val viewModel: ProfileViewModel by viewModels()

    @Composable
    override fun Content() {
        ProfileScreen(viewModel){id->
            findNavController().navigate(("musicapp://playlist/$id").toUri())
        }
    }
}