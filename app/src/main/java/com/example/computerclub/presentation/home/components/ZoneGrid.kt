package com.example.computerclub.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.computerclub.domain.model.Booking
import com.example.computerclub.presentation.home.ComputerClubUiState
import com.example.computerclub.presentation.home.isActiveBooking
import com.example.computerclub.presentation.home.utils.freeSeatsCount
import com.example.computerclub.ui.theme.HomeMuted

@Composable
internal fun ZoneGrid(
    state: ComputerClubUiState,
    bookedSeatIds: Set<Int>,
    onBookingClick: () -> Unit,
    onCancelBooking: (Int) -> Unit
) {
    var showEquipment by remember { mutableStateOf(false) }
    var selectedBooking by remember { mutableStateOf<Booking?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        BookingCta(onClick = onBookingClick)

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            InfoCard("70+", "мест", Icons.Default.Computer, Modifier.weight(1f))
            InfoCard("24/7", "Работаем\nдля тебя", Icons.Default.AccessTime, Modifier.weight(1f))
            InfoCard(
                value = "Top",
                subtitle = "Оборудование",
                icon = Icons.Default.Star,
                modifier = Modifier.weight(1f),
                onClick = { showEquipment = !showEquipment }
            )
        }

        if (showEquipment) {
            EquipmentDetailsCard(state.seatTypes)
        }

        MyBookingsPreview(
            bookings = state.bookings.filter { it.isActiveBooking() },
            onBookingClick = { selectedBooking = it }
        )

        Text(
            text = "Свободно сейчас: ${state.freeSeatsCount(bookedSeatIds)} мест",
            color = HomeMuted,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }

    selectedBooking?.let { booking ->
        BookingDetailsDialog(
            booking = booking,
            onDismiss = { selectedBooking = null },
            onCancelBooking = {
                onCancelBooking(booking.id)
                selectedBooking = null
            }
        )
    }
}
