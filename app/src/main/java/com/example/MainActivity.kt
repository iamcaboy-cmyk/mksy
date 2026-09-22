package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.screens.admin.AdminApplicationDetailScreen
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.admin.AdminLoginScreen
import com.example.ui.screens.application.NewApplicationWizardScreen
import com.example.ui.screens.application.ReceiptScreen
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.auth.RegisterScreen
import com.example.ui.screens.dashboard.CitizenDashboardScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.status.ApplicationStatusScreen
import com.example.ui.theme.JanSahayataTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JanSahayataTheme {
                JanSahayataApp()
            }
        }
    }
}

@Composable
fun JanSahayataApp(viewModel: MainViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val language by viewModel.language.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.userMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Hardware back button navigation handling
    BackHandler(enabled = currentScreen != Screen.HOME) {
        when (currentScreen) {
            Screen.REGISTER, Screen.LOGIN, Screen.ADMIN_LOGIN -> viewModel.navigateTo(Screen.HOME)
            Screen.DASHBOARD -> viewModel.navigateTo(Screen.HOME)
            Screen.NEW_APPLICATION -> {
                if (viewModel.wizardCurrentStep.value > 1) {
                    viewModel.wizardCurrentStep.value -= 1
                } else {
                    viewModel.navigateTo(Screen.DASHBOARD)
                }
            }
            Screen.APPLICATION_STATUS -> viewModel.navigateTo(Screen.DASHBOARD)
            Screen.RECEIPT -> viewModel.navigateTo(Screen.DASHBOARD)
            Screen.ADMIN_DASHBOARD -> viewModel.navigateTo(Screen.HOME)
            Screen.ADMIN_APPLICATION_DETAIL -> viewModel.navigateTo(Screen.ADMIN_DASHBOARD)
            Screen.HOME -> { /* Exit handled by OS */ }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Modifier.padding(innerPadding) // innerPadding is passed down to screen composables

        when (currentScreen) {
            Screen.HOME -> HomeScreen(
                viewModel = viewModel,
                language = language,
                onNavigate = { screen -> viewModel.navigateTo(screen) }
            )

            Screen.REGISTER -> RegisterScreen(
                viewModel = viewModel,
                language = language,
                onNavigate = { screen -> viewModel.navigateTo(screen) }
            )

            Screen.LOGIN -> LoginScreen(
                viewModel = viewModel,
                language = language,
                onNavigate = { screen -> viewModel.navigateTo(screen) }
            )

            Screen.ADMIN_LOGIN -> AdminLoginScreen(
                viewModel = viewModel,
                language = language,
                onNavigate = { screen -> viewModel.navigateTo(screen) }
            )

            Screen.DASHBOARD -> CitizenDashboardScreen(
                viewModel = viewModel,
                language = language,
                onNavigate = { screen -> viewModel.navigateTo(screen) }
            )

            Screen.NEW_APPLICATION -> NewApplicationWizardScreen(
                viewModel = viewModel,
                language = language,
                onNavigate = { screen -> viewModel.navigateTo(screen) }
            )

            Screen.APPLICATION_STATUS -> ApplicationStatusScreen(
                viewModel = viewModel,
                language = language,
                onNavigate = { screen -> viewModel.navigateTo(screen) }
            )

            Screen.RECEIPT -> ReceiptScreen(
                viewModel = viewModel,
                language = language,
                onNavigate = { screen -> viewModel.navigateTo(screen) }
            )

            Screen.ADMIN_DASHBOARD -> AdminDashboardScreen(
                viewModel = viewModel,
                language = language,
                onNavigate = { screen -> viewModel.navigateTo(screen) }
            )

            Screen.ADMIN_APPLICATION_DETAIL -> AdminApplicationDetailScreen(
                viewModel = viewModel,
                language = language,
                onNavigate = { screen -> viewModel.navigateTo(screen) }
            )
        }
    }
}
