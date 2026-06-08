package com.example.computerclub.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.computerclub.domain.model.Booking
import com.example.computerclub.presentation.common.formatPrice
import com.example.computerclub.presentation.home.canCancelByRule
import com.example.computerclub.presentation.home.displaySeatName
import com.example.computerclub.presentation.home.utils.BookingTimeUtils
import com.example.computerclub.ui.theme.HomeCardBg
import com.example.computerclub.ui.theme.HomeMuted
import com.example.computerclub.ui.theme.HomeRed

@Composable
internal fun BookingDetailsDialog(
    booking: Booking,
    onDismiss: () -> Unit,
    onCancelBooking: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = HomeCardBg,
        title = {
            Text(booking.seatName.displaySeatName(), color = Color.White, fontWeight = FontWeight.Black)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                BookingPreviewInfo(booking)
                Text("${booking.totalPrice.formatPrice()} ₽", color = HomeRed, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                if (booking.canCancelByRule()) {
                    Text("Эту бронь можно отменить.", color = HomeMuted, fontWeight = FontWeight.Bold)
                } else {
                    Text("Отмена уже недоступна: до старта осталось меньше 4 часов.", color = HomeMuted, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onCancelBooking,
                enabled = booking.canCancelByRule()
            ) {
                Text("Отменить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Закрыть", color = HomeRed)
            }
        }
    )
}

@Composable
internal fun BookingPreviewInfo(booking: Booking) {
    val start = BookingTimeUtils.parseDateTime(booking.startTime)
    val end = BookingTimeUtils.parseDateTime(booking.endTime)
    if (start == null || end == null) {
        Text(booking.startTime, color = HomeRed, fontWeight = FontWeight.Bold)
        return
    }

    val sameDay = start.toLocalDate() == end.toLocalDate()

    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(
            "Начало: ${BookingTimeUtils.formatBookingPreviewStart(start, end)}",
            color = HomeRed,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Конец: ${BookingTimeUtils.formatBookingPreviewEnd(start, end)}",
            color = HomeRed,
            fontWeight = FontWeight.Bold
        )
        if (sameDay) {
            Text("Дата: ${BookingTimeUtils.formatShortDate(start)}", color = HomeMuted, fontWeight = FontWeight.Bold)
        }
        Text("Длительность: ${BookingTimeUtils.formatDuration(start, end)}", color = HomeMuted, fontWeight = FontWeight.Bold)
    }
}
