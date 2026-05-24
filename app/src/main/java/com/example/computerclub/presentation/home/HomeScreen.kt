package com.example.computerclub.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.computerclub.domain.model.Seat
import com.example.computerclub.presentation.booking.ClubMap
import com.example.computerclub.presentation.booking.DateAndTimeStep
import com.example.computerclub.presentation.booking.availableStartSlots
import com.example.computerclub.presentation.booking.bookingDisplayDate
import com.example.computerclub.presentation.booking.coerceStartMinute
import com.example.computerclub.presentation.booking.parseStartMinute
import com.example.computerclub.presentation.booking.uiMinAvailableMinute
import com.example.computerclub.presentation.navigation.HomeBottomBar
import com.example.computerclub.presentation.profile.BookingCard
import com.example.computerclub.presentation.profile.ProfileCard
import com.example.computerclub.presentation.home.utils.bookedSeatIdsForSelectedPeriod
import com.example.computerclub.ui.theme.HomeBg
import com.example.computerclub.ui.theme.HomeFieldBg
import com.example.computerclub.ui.theme.HomeRed

@Composable
fun HomeScreen(
    state: ComputerClubUiState,
    onLogout: () -> Unit,
    onTabSelected: (HomeTab) -> Unit,
    onRefreshSeats: () -> Unit,
    onRefreshBookings: () -> Unit,
    onClubSelected: (Int) -> Unit,
    onSeatTypeSelected: (Int) -> Unit,
    onPeriodSelected: (Int, Int, Int) -> Unit,
    onSeatsSelected: (List<Seat>) -> Unit,
    onCancelBooking: (Int) -> Unit
) {
    var bookingStep by remember { mutableIntStateOf(1) }
    var selectedDateOffset by remember { mutableIntStateOf(0) }
    var selectedStartMinute by remember { mutableIntStateOf(coerceStartMinute(0, parseStartMinute(state.startTime))) }
    var showBookingRules by remember { mutableStateOf(false) }
    fun requestBookingStart() {
        state.seatTypes.firstOrNull()?.let { type ->
            onSeatTypeSelected(type.id)
        }
        showBookingRules = true
    }
    fun acceptBookingRules() {
        bookingStep = 1
        showBookingRules = false
        onTabSelected(HomeTab.Seats)
    }
    val bookedSeatIds = state.bookedSeatIdsForSelectedPeriod()

    Scaffold(
        containerColor = HomeBg,
        bottomBar = {
            HomeBottomBar(
                selectedTab = state.selectedTab,
                onTabSelected = onTabSelected,
                onBookingSelected = ::requestBookingStart
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(HomeBg)
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 18.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (state.selectedTab) {
                HomeTab.Main -> mainTab(
                    state = state,
                    bookedSeatIds = bookedSeatIds,
                    onBookingStartRequested = ::requestBookingStart,
                    onTabSelected = onTabSelected,
                    onCancelBooking = onCancelBooking
                )
                HomeTab.Seats -> seatsTab(
                    state = state,
                    bookingStep = bookingStep,
                    selectedDateOffset = selectedDateOffset,
                    selectedStartMinute = selectedStartMinute,
                    bookedSeatIds = bookedSeatIds,
                    onBack = {
                        if (bookingStep == 2) bookingStep = 1 else onTabSelected(HomeTab.Main)
                    },
                    onDateChanged = { offset, minute ->
                        selectedDateOffset = offset
                        selectedStartMinute = minute
                    },
                    onStartChanged = { selectedStartMinute = it },
                    onPeriodSelected = onPeriodSelected,
                    onNextStep = { bookingStep = 2 },
                    onSeatsSelected = onSeatsSelected
                )
                HomeTab.Bookings -> bookingsTab(state, onTabSelected, onRefreshBookings, onCancelBooking)
                HomeTab.Profile -> profileTab(state, onTabSelected, onLogout)
            }
        }
    }

    if (showBookingRules) {
        BookingRulesDialog(
            onDismiss = { showBookingRules = false },
            onAccept = ::acceptBookingRules
        )
    }
}

@Composable
private fun BookingRulesDialog(onDismiss: () -> Unit, onAccept: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = HomeFieldBg,
        title = {
            Text("Правила бронирования", color = Color.White, fontWeight = FontWeight.Black)
        },
        text = {
            androidx.compose.foundation.layout.Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("• Бронь можно отменить только за 4 часа до старта.", color = Color.White)
                Text("• Опоздание больше чем на 15 минут может привести к отмене брони.", color = Color.White)
                Text("• Одновременно можно выбрать несколько свободных мест.", color = Color.White)
                Text("• После подтверждения выбранные места становятся занятыми на выбранное время.", color = Color.White)
            }
        },
        confirmButton = {
            Button(onClick = onAccept) { Text("Принимаю") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена", color = HomeRed) }
        }
    )
}

private fun androidx.compose.foundation.lazy.LazyListScope.seatsTab(
    state: ComputerClubUiState,
    bookingStep: Int,
    selectedDateOffset: Int,
    selectedStartMinute: Int,
    bookedSeatIds: Set<Int>,
    onBack: () -> Unit,
    onDateChanged: (Int, Int) -> Unit,
    onStartChanged: (Int) -> Unit,
    onPeriodSelected: (Int, Int, Int) -> Unit,
    onNextStep: () -> Unit,
    onSeatsSelected: (List<Seat>) -> Unit
) {
    item {
        BackHeader(
            title = if (bookingStep == 1) "Новое бронирование" else "Выберите место",
            subtitle = if (bookingStep == 2) "${state.startTime.bookingDisplayDate()} - ${state.endTime.bookingDisplayDate().takeLast(5)}" else null,
            onBack = onBack
        )
    }
    item { StepIndicator(bookingStep) }
    if (bookingStep == 1) {
        item {
            DateAndTimeStep(
                selectedDateOffset = selectedDateOffset,
                selectedStartMinute = selectedStartMinute,
                selectedDuration = state.selectedDurationHours,
                onDateSelected = { offset ->
                    val minute = availableStartSlots(offset).firstOrNull() ?: uiMinAvailableMinute(offset)
                    onDateChanged(offset, minute)
                    onPeriodSelected(offset, minute, state.selectedDurationHours)
                },
                onStartSelected = { minute ->
                    onStartChanged(minute)
                    onPeriodSelected(selectedDateOffset, minute, state.selectedDurationHours)
                },
                onDurationSelected = { duration ->
                    val minute = coerceStartMinute(selectedDateOffset, selectedStartMinute)
                    onDateChanged(selectedDateOffset, minute)
                    onPeriodSelected(selectedDateOffset, minute, duration)
                }
            )
        }
        item { PrimaryButton("Выбрать место", onNextStep) }
    } else {
        item {
            ClubMap(
                seats = state.allSeats,
                layouts = state.seatLayouts,
                isLayoutsLoading = state.isSeatLayoutsLoading,
                layoutsError = state.seatLayoutsError,
                bookedSeatIds = bookedSeatIds,
                onSeatsSelected = onSeatsSelected
            )
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.bookingsTab(
    state: ComputerClubUiState,
    onTabSelected: (HomeTab) -> Unit,
    onRefreshBookings: () -> Unit,
    onCancelBooking: (Int) -> Unit
) {
    val activeBookings = state.bookings.filter { it.isActiveBooking() }
    val historyBookings = state.bookings.filterNot { it.isActiveBooking() }

    item { BackHeader("Мои бронирования", null) { onTabSelected(HomeTab.Main) } }
    item { SectionHeader("Активные брони", onRefreshBookings) }
    item { StatusAndLoading(state) }
    if (activeBookings.isEmpty()) {
        item { EmptyCard("Активных броней пока нет") }
    } else {
        items(activeBookings) { booking -> BookingCard(booking, onCancelBooking, canCancel = true) }
    }

    item { SectionTitle("История бронирований") }
    if (historyBookings.isEmpty()) {
        item { EmptyCard("История пока пустая") }
    } else {
        items(historyBookings) { booking -> BookingCard(booking, onCancelBooking, canCancel = false) }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.profileTab(
    state: ComputerClubUiState,
    onTabSelected: (HomeTab) -> Unit,
    onLogout: () -> Unit
) {
    item { BackHeader("Профиль", null) { onTabSelected(HomeTab.Main) } }
    item {
        ProfileCard(
            state = state,
            onBookingsClick = { onTabSelected(HomeTab.Bookings) },
            onLogout = onLogout
        )
    }
}
