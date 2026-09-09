package com.afoxplus.emergency.presentation.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.afoxplus.emergency.presentation.navigation.AppNavigation
import com.afoxplus.emergency.presentation.ui.theme.AppemergencyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleAlertLaunchIntent(intent)
        setContent {
            AppemergencyTheme {
                AppNavigation(backStack = viewModel.backStack)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleAlertLaunchIntent(intent)
    }

    private fun handleAlertLaunchIntent(intent: Intent?) {
        if (intent?.getBooleanExtra(EXTRA_SHOW_ALERT_SUCCESS, false) != true) return

        viewModel.onAlertLaunchIntent(
            latitude = null,
            longitude = null,
            historyEntryId = intent.getStringExtra(EXTRA_ALERT_HISTORY_ID)
        )
        intent.removeExtra(EXTRA_SHOW_ALERT_SUCCESS)
        intent.removeExtra(EXTRA_ALERT_HISTORY_ID)
    }

    companion object {
        const val EXTRA_SHOW_ALERT_SUCCESS = "extra_show_alert_success"
        const val EXTRA_ALERT_HISTORY_ID = "extra_alert_history_id"
    }
}