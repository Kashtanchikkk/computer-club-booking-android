package com.example.computerclub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.computerclub.presentation.app.AppContainer
import com.example.computerclub.presentation.auth.AuthViewModel
import com.example.computerclub.presentation.home.HomeViewModel
import com.example.computerclub.presentation.navigation.ComputerClubApp
import com.example.computerclub.ui.theme.ComputerClubAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appContainer = AppContainer(applicationContext)
        val authViewModelFactory = AuthViewModel.Factory(
            loginUseCase = appContainer.loginUseCase,
            registerUseCase = appContainer.registerUseCase,
            logoutUseCase = appContainer.logoutUseCase,
            restoreSessionUseCase = appContainer.restoreSessionUseCase
        )
        val homeViewModelFactory = HomeViewModel.Factory(
            loadClubsUseCase = appContainer.loadClubsUseCase,
            loadSeatTypesUseCase = appContainer.loadSeatTypesUseCase,
            loadSeatsUseCase = appContainer.loadSeatsUseCase,
            loadAllSeatsUseCase = appContainer.loadAllSeatsUseCase,
            loadSeatLayoutsUseCase = appContainer.loadSeatLayoutsUseCase,
            createBookingUseCase = appContainer.createBookingUseCase,
            loadMyBookingsUseCase = appContainer.loadMyBookingsUseCase,
            cancelBookingUseCase = appContainer.cancelBookingUseCase
        )

        enableEdgeToEdge()
        setContent {
            ComputerClubAppTheme {
                ComputerClubApp(
                    authViewModelFactory = authViewModelFactory,
                    homeViewModelFactory = homeViewModelFactory
                )
            }
        }
    }
}
