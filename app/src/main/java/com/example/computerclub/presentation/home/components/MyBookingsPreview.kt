package com.example.computerclub.presentation.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.computerclub.domain.model.Booking
import com.example.computerclub.presentation.home.displaySeatName
import com.example.computerclub.ui.theme.HomeCardBg
import com.example.computerclub.ui.theme.HomeMuted
import com.example.computerclub.ui.theme.HomeStroke

@Composable
internal fun MyBookingsPreview(bookings: List<Booking>, onBookingClick: (Booking) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = HomeCardBg.copy(alpha = 0.78f)),
        border = BorderStroke(1.dp, HomeStroke)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("ТВОИ БРОНИ", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)

            if (bookings.isEmpty()) {
                Text(
                    text = "У тебя пока нет активных броней",
                    color = HomeMuted,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    bookings.forEach { booking ->
                        BookingPreviewRow(
                            booking = booking,
                            onClick = { onBookingClick(booking) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BookingPreviewRow(booking: Booking, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            booking.seatName.displaySeatName(),
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black
        )
        BookingPreviewInfo(booking)
    }
}
