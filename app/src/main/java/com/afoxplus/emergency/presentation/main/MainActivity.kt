package com.afoxplus.emergency.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.afoxplus.emergency.navigation.AppNavigation
import com.afoxplus.emergency.ui.theme.AppemergencyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppemergencyTheme {
                AppNavigation(backStack = viewModel.backStack)
            }
        }
    }
}