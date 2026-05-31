package com.example.computerclub.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.computerclub.presentation.admin.AdminViewModel
import com.example.computerclub.presentation.auth.AuthScreen
import com.example.computerclub.presentation.auth.AuthViewModel
import com.example.computerclub.presentation.booking.BookingScreen
import com.example.computerclub.presentation.home.HomeScreen
import com.example.computerclub.presentation.home.HomeViewModel
import kotlinx.coroutines.delay

@Composable
fun ComputerClubApp(
    authViewModelFactory: AuthViewModel.Factory,
    homeViewModelFactory: HomeViewModel.Factory,
    adminViewModelFactory: AdminViewModel.Factory
) {
    val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)
    val homeViewModel: HomeViewModel = viewModel(factory = homeViewModelFactory)
    val adminViewModel: AdminViewModel = viewModel(factory = adminViewModelFactory)
    val authState by authViewModel.state.collectAsState()
    val state by homeViewModel.state.collectAsState()
    val adminState by adminViewModel.state.collectAsState()
    var showSplash by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(900)
        showSplash = false
    }

    LaunchedEffect(authState.user?.id) {
        homeViewModel.onUserChanged(authState.user)
        adminViewModel.onUserChanged(authState.user)
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF090A0F)) {
        when {
            showSplash || (authState.isLoading && authState.user == null) -> SplashScreen()
            authState.user == null -> AuthScreen(
                state = authState,
                onEmailChange = authViewModel::updateEmail,
                onPasswordChange = authViewModel::updatePassword,
                onNameChange = authViewModel::updateName,
                onSubmit = authViewModel::submitAuth,
                onSwitchMode = authViewModel::switchAuthMode
            )
            state.selectedSeats.isNotEmpty() -> BookingScreen(
                state = state,
                seats = state.selectedSeats,
                onBack = homeViewModel::clearSelectedSeats,
                onPeriodSelected = homeViewModel::selectBookingPeriod,
                onCreateBooking = homeViewModel::createBooking
            )
            else -> HomeScreen(
                state = state,
                onLogout = authViewModel::logout,
                onTabSelected = homeViewModel::selectTab,
                onRefreshMapData = homeViewModel::refreshMapData,
                onRefreshBookings = homeViewModel::loadBookings,
                onClubSelected = homeViewModel::selectClub,
                onSeatTypeSelected = homeViewModel::selectSeatType,
                onPeriodSelected = homeViewModel::selectBookingPeriod,
                onSeatsSelected = homeViewModel::selectSeats,
                onCancelBooking = homeViewModel::cancelBooking,
                adminState = adminState,
                onAdminSectionSelected = adminViewModel::selectSection,
                onRefreshAdminBookings = adminViewModel::loadBookings,
                onRefreshAdminSeats = adminViewModel::loadSeats,
                onAdminCancelBooking = adminViewModel::cancelBooking,
                onUpdateSeatName = { seat, name ->
                    adminViewModel.updateSeatName(seat.id, name) { updatedSeat ->
                        homeViewModel.applyAdminSeatName(updatedSeat)
                    }
                },
                onDeactivateSeat = { seat ->
                    adminViewModel.deactivateSeat(seat.id) { updatedSeat ->
                        homeViewModel.applyAdminSeatAvailability(updatedSeat, isActive = false)
                    }
                },
                onRestoreSeat = { seat ->
                    adminViewModel.restoreSeat(seat.id) { updatedSeat ->
                        homeViewModel.applyAdminSeatAvailability(updatedSeat, isActive = true)
                    }
                }
            )
        }
    }
}

@Composable
private fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.radialGradient(listOf(Color(0x55FF3347), Color(0xFF090A0F)), radius = 850f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(Color(0xFFFF3347), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("", color = Color.White, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black)
            }
            Text("Computer Club", color = Color.White, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black)
            Text("Бронирование игровых мест", color = Color(0xFF8F8F96), style = MaterialTheme.typography.titleMedium)
        }
    }
}
