package com.example.dynamics.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.base.BaseActivity
import com.example.dynamics.ui.adapter.DynamicsAdapter
import com.example.dynamics.ui.viewmodel.DynamicsViewModel
import com.example.dynamics.R
import com.example.dynamics.databinding.ActivityDynamicsBinding
import com.example.dynamics.ui.screen.DynamicScreen
import com.example.model.UserManager
import com.example.therouter.RoutePath
import com.example.util.ToastUtil
import com.therouter.router.Route
import kotlinx.coroutines.launch

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