package com.example.dynamics.ui.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.dynamics.ui.viewmodel.DynamicsViewModel
import com.example.dynamics.ui.screen.DynamicScreen
import com.example.therouter.RoutePath
import com.therouter.router.Route

@Route(path = RoutePath.DYNAMICS_MAIN)
class DynamicsActivity : AppCompatActivity() {
    private val viewModel : DynamicsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       setContent {
           DynamicScreen(viewModel)
       }
    }
}