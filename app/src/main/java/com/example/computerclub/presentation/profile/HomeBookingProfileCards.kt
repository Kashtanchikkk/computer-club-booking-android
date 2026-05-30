package com.example.computerclub.presentation.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.computerclub.domain.model.Booking
import com.example.computerclub.presentation.booking.bookingDisplayDate
import com.example.computerclub.presentation.common.formatPrice
import com.example.computerclub.presentation.home.ComputerClubUiState
import com.example.computerclub.presentation.home.displaySeatName
import com.example.computerclub.presentation.home.isActiveBooking
import com.example.computerclub.presentation.home.utils.BookingTimeUtils
import com.example.computerclub.ui.theme.HomeCardBg
import com.example.computerclub.ui.theme.HomeGreen
import com.example.computerclub.ui.theme.HomeMuted
import com.example.computerclub.ui.theme.HomeRed
import com.example.computerclub.ui.theme.HomeStroke
import java.time.Duration
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
internal fun BookingCard(booking: Booking, onCancelBooking: (Int) -> Unit, canCancel: Boolean = booking.isActiveBooking()) {
    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = HomeCardBg), border = BorderStroke(1.dp, HomeStroke)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(booking.seatName.displaySeatName(), color = Color.White, fontWeight = FontWeight.Black)
                Text(booking.status.toRuStatus(), color = HomeGreen, fontWeight = FontWeight.Bold)
            }
            Text("${booking.startTime.bookingDisplayDate()} - ${booking.endTime.bookingDisplayDate().takeLast(5)}", color = HomeMuted)
            Text("${booking.totalPrice.formatPrice()} ₽", color = HomeRed, fontWeight = FontWeight.Black)
            if (canCancel) OutlinedButton(onClick = { onCancelBooking(booking.id) }) { Text("Отменить") }
        }
    }
}

@Composable
internal fun ProfileCard(
    state: ComputerClubUiState,
    onBookingsClick: () -> Unit,
    onLogout: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        UserProfileCard(state)
        ProfileStatsCard(state)
        BookingHistoryCard(
            bookings = state.bookings.take(4),
            onBookingsClick = onBookingsClick
        )
        LogoutButton(onLogout)
    }
}

@Composable
private fun UserProfileCard(state: ComputerClubUiState) {
    Card(shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = HomeCardBg), border = BorderStroke(1.dp, HomeStroke)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(96.dp).background(HomeRed, CircleShape), contentAlignment = Alignment.Center) {
                Text(state.user?.name.profileInitials(), color = Color.White, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(state.user?.name ?: "Игрок", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                Text(state.user?.email.orEmpty(), color = HomeMuted, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                ActiveBadge()
            }
        }
    }
}

@Composable
private fun ActiveBadge() {
    Row(
        modifier = Modifier.background(Color(0x3325D875), RoundedCornerShape(18.dp)).padding(horizontal = 12.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(9.dp).background(HomeGreen, CircleShape))
        Text("Активен", color = HomeGreen, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun ProfileStatsCard(state: ComputerClubUiState) {
    val totalBookings = state.bookings.size
    val totalHours = state.bookings.sumOf { it.durationHours() }
    val completedBookings = state.bookings.count { !it.isActiveBooking() && it.status.lowercase() != "cancelled" }

    Card(shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = HomeCardBg), border = BorderStroke(1.dp, HomeStroke)) {
        Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Text("Статистика", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            Row(
                modifier = Modifier.fillMaxWidth().height(120.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem(Icons.Default.DateRange, Color(0xFFB07CFF), totalBookings.toString(), "Всего\nброней", Modifier.weight(1f))
                VerticalDivider()
                StatItem(Icons.Default.AccessTime, Color(0xFF59A9FF), "${totalHours} ч", "Всего часов\nарендовано", Modifier.weight(1f))
                VerticalDivider()
                StatItem(Icons.Default.CheckCircle, HomeGreen, completedBookings.toString(), "Завершённых\nброней", Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(modifier = Modifier.size(36.dp).background(Color(0x2215161B), CircleShape), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
        }
        Text(value, color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
        Text(label, color = HomeMuted, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun VerticalDivider() {
    Box(modifier = Modifier.width(1.dp).height(76.dp).background(HomeStroke))
}

@Composable
private fun BookingHistoryCard(
    bookings: List<Booking>,
    onBookingsClick: () -> Unit
) {
    Card(shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = HomeCardBg), border = BorderStroke(1.dp, HomeStroke)) {
        Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp, end = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "История бронирований",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f).padding(end = 12.dp)
                )
                Row(
                    modifier = Modifier.clickable(onClick = onBookingsClick),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Смотреть все", color = Color(0xFFC084FC), fontWeight = FontWeight.Black, maxLines = 1)
                    Icon(Icons.Default.ChevronRight, contentDescription = "Смотреть все", tint = Color(0xFFC084FC), modifier = Modifier.size(22.dp))
                }
            }

            if (bookings.isEmpty()) {
                Text("История пока пустая", color = HomeMuted, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 18.dp))
            } else {
                bookings.forEachIndexed { index, booking ->
                    BookingHistoryRow(booking)
                    if (index != bookings.lastIndex) {
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(HomeStroke))
                    }
                }
            }
        }
    }
}

@Composable
private fun BookingHistoryRow(booking: Booking) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(56.dp).background(Color(0xFF292B33), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.DesktopWindows, contentDescription = null, tint = booking.iconColor(), modifier = Modifier.size(30.dp))
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(booking.seatName.displaySeatName(), color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
            Text(booking.periodText(), color = HomeMuted, fontWeight = FontWeight.Bold)
            Text(booking.durationText(), color = HomeMuted, fontWeight = FontWeight.Bold)
        }
        StatusBadge(booking)
    }
}

@Composable
private fun StatusBadge(booking: Booking) {
    val active = booking.isActiveBooking()
    val cancelled = booking.status.lowercase() == "cancelled"
    val text = when {
        cancelled -> "Отменена"
        active -> "Активна"
        else -> "Завершено"
    }
    val color = when {
        cancelled -> HomeRed
        active -> Color(0xFF5AA9FF)
        else -> HomeGreen
    }
    val background = when {
        cancelled -> Color(0x33FF3347)
        active -> Color(0x335AA9FF)
        else -> Color(0x3325D875)
    }

    Text(
        text = text,
        color = color,
        fontWeight = FontWeight.Black,
        modifier = Modifier.background(background, RoundedCornerShape(9.dp)).padding(horizontal = 10.dp, vertical = 7.dp)
    )
}

@Composable
private fun LogoutButton(onLogout: () -> Unit) {
    Button(
        onClick = onLogout,
        modifier = Modifier.fillMaxWidth().height(62.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = HomeRed)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color.White)
            Text("Выйти из аккаунта", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
        }
    }
}

private fun String.toRuStatus(): String = when (lowercase()) {
    "pending" -> "ожидает"
    "confirmed" -> "подтверждена"
    "cancelled" -> "отменена"
    else -> this
}

private fun String?.profileInitials(): String {
    val name = this?.trim().orEmpty()
    if (name.isBlank()) return "ИГ"

    val parts = name.split(Regex("\\s+")).filter { it.isNotBlank() }
    return if (parts.size >= 2) {
        "${parts[0].first()}${parts[1].first()}".uppercase()
    } else {
        name.take(2).uppercase()
    }
}

private fun Booking.durationHours(): Int {
    val start = BookingTimeUtils.parseDateTime(startTime) ?: return 0
    val end = BookingTimeUtils.parseDateTime(endTime) ?: return 0
    return Duration.between(start, end).toHours().toInt().coerceAtLeast(0)
}

private fun Booking.durationText(): String {
    val start = BookingTimeUtils.parseDateTime(startTime) ?: return ""
    val end = BookingTimeUtils.parseDateTime(endTime) ?: return ""
    return BookingTimeUtils.formatDuration(start, end)
}

private fun Booking.periodText(): String {
    val start = BookingTimeUtils.parseDateTime(startTime) ?: return startTime
    val end = BookingTimeUtils.parseDateTime(endTime) ?: return endTime
    val formatter = DateTimeFormatter.ofPattern("d MMMM, HH:mm", Locale.forLanguageTag("ru"))
    val endFormatter = DateTimeFormatter.ofPattern("HH:mm")
    return "${start.format(formatter)} - ${end.format(endFormatter)}"
}

private fun Booking.iconColor(): Color {
    return when {
        seatName.contains("VIP", ignoreCase = true) -> Color(0xFF65AFFF)
        seatName.contains("PS", ignoreCase = true) -> Color(0xFFFF8A2A)
        isActiveBooking() -> Color(0xFFC084FC)
        else -> HomeRed
    }
}
