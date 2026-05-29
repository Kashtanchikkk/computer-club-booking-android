package com.example.computerclub.presentation.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.computerclub.domain.model.Booking
import com.example.computerclub.domain.model.Seat
import com.example.computerclub.presentation.booking.bookingDisplayDate
import com.example.computerclub.presentation.home.EmptyCard
import com.example.computerclub.presentation.home.PageHeader
import com.example.computerclub.presentation.home.SectionHeader
import com.example.computerclub.presentation.home.displaySeatName
import com.example.computerclub.ui.theme.HomeCardBg
import com.example.computerclub.ui.theme.HomeFieldBg
import com.example.computerclub.ui.theme.HomeGreen
import com.example.computerclub.ui.theme.HomeMuted
import com.example.computerclub.ui.theme.HomeRed
import com.example.computerclub.ui.theme.HomeStroke

@Composable
fun AdminScreen(
    state: AdminUiState,
    onSectionSelected: (AdminSection) -> Unit,
    onRefreshBookings: () -> Unit,
    onRefreshSeats: () -> Unit,
    onCancelBooking: (Int) -> Unit,
    onUpdateSeatName: (Seat, String) -> Unit,
    onDeactivateSeat: (Seat) -> Unit,
    onRestoreSeat: (Seat) -> Unit,
    onLogout: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        AdminTopBar(onLogout = onLogout)
        AdminSectionSwitcher(
            selectedSection = state.selectedSection,
            onSectionSelected = onSectionSelected
        )
        AdminStatus(state)

        when (state.selectedSection) {
            AdminSection.Bookings -> AdminBookingsScreen(
                state = state,
                onRefresh = onRefreshBookings,
                onCancelBooking = onCancelBooking
            )
            AdminSection.Seats -> AdminSeatsScreen(
                state = state,
                onRefresh = onRefreshSeats,
                onUpdateSeatName = onUpdateSeatName,
                onDeactivateSeat = onDeactivateSeat,
                onRestoreSeat = onRestoreSeat
            )
        }
    }
}

@Composable
fun AdminBookingsScreen(
    state: AdminUiState,
    onRefresh: () -> Unit,
    onCancelBooking: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader("Бронирования", onRefresh)
        if (state.bookings.isEmpty()) {
            EmptyCard("Бронирований пока нет")
        } else {
            state.bookings.forEach { booking ->
                AdminBookingCard(booking = booking, onCancelBooking = onCancelBooking)
            }
        }
    }
}

@Composable
fun AdminSeatsScreen(
    state: AdminUiState,
    onRefresh: () -> Unit,
    onUpdateSeatName: (Seat, String) -> Unit,
    onDeactivateSeat: (Seat) -> Unit,
    onRestoreSeat: (Seat) -> Unit
) {
    var selectedType by remember { mutableStateOf<String?>(null) }
    val categories = state.seats
        .map { it.type }
        .filter { it.isNotBlank() }
        .distinct()
    val visibleSeats = selectedType?.let { type ->
        state.seats.filter { it.type == type }
    } ?: state.seats

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader("Игровые места", onRefresh)
        SeatTypeFilter(
            categories = categories,
            selectedType = selectedType,
            onTypeSelected = { selectedType = it }
        )
        if (state.seats.isEmpty()) {
            EmptyCard("Игровых мест пока нет")
        } else if (visibleSeats.isEmpty()) {
            EmptyCard("В этой категории мест нет")
        } else {
            visibleSeats.forEach { seat ->
                AdminSeatCard(
                    seat = seat,
                    onUpdateSeatName = onUpdateSeatName,
                    onDeactivateSeat = onDeactivateSeat,
                    onRestoreSeat = onRestoreSeat
                )
            }
        }
    }
}

@Composable
private fun SeatTypeFilter(
    categories: List<String>,
    selectedType: String?,
    onTypeSelected: (String?) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            FilterButton(
                text = "Все",
                selected = selectedType == null,
                onClick = { onTypeSelected(null) }
            )
        }
        items(categories) { category ->
            FilterButton(
                text = category,
                selected = selectedType == category,
                onClick = { onTypeSelected(category) }
            )
        }
    }
}

@Composable
private fun FilterButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = if (selected) {
        ButtonDefaults.buttonColors(containerColor = HomeRed, contentColor = Color.White)
    } else {
        ButtonDefaults.buttonColors(containerColor = HomeFieldBg, contentColor = HomeMuted)
    }
    Button(onClick = onClick, shape = RoundedCornerShape(14.dp), colors = colors) {
        Text(text, fontWeight = FontWeight.Black, maxLines = 1)
    }
}

@Composable
private fun AdminSectionSwitcher(
    selectedSection: AdminSection,
    onSectionSelected: (AdminSection) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        AdminSectionButton(
            text = "Бронирования",
            selected = selectedSection == AdminSection.Bookings,
            onClick = { onSectionSelected(AdminSection.Bookings) },
            modifier = Modifier.weight(1f)
        )
        AdminSectionButton(
            text = "Игровые места",
            selected = selectedSection == AdminSection.Seats,
            onClick = { onSectionSelected(AdminSection.Seats) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun AdminSectionButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = if (selected) {
        ButtonDefaults.buttonColors(containerColor = HomeRed, contentColor = Color.White)
    } else {
        ButtonDefaults.buttonColors(containerColor = HomeFieldBg, contentColor = HomeMuted)
    }
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(14.dp),
        colors = colors
    ) {
        Text(text, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun AdminStatus(state: AdminUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        state.successMessage?.let {
            Text(it, color = HomeGreen, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
        state.errorMessage?.let {
            Text(it, color = HomeRed, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
        if (state.isLoading) {
            CircularProgressIndicator(color = HomeRed)
        }
    }
}

@Composable
private fun AdminBookingCard(
    booking: Booking,
    onCancelBooking: (Int) -> Unit
) {
    val isCancelled = booking.status.lowercase() == "cancelled"
    val canCancel = !isCancelled && com.example.computerclub.presentation.home.utils.BookingTimeUtils.isActiveBooking(booking)
    val cancelButtonText = when {
        canCancel -> "Отменить бронь"
        isCancelled -> "Уже отменена"
        else -> "Бронь завершена"
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = HomeCardBg),
        border = BorderStroke(1.dp, HomeStroke)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "Бронь #${booking.id}",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black
                )
                Text(booking.status.toRuStatus(), color = booking.status.statusColor(), fontWeight = FontWeight.Bold)
            }
            AdminInfoLine("Место", booking.seatName.displaySeatName())
            AdminInfoLine("Начало", booking.startTime.bookingDisplayDate())
            AdminInfoLine("Окончание", booking.endTime.bookingDisplayDate())
            OutlinedButton(
                onClick = { if (canCancel) onCancelBooking(booking.id) },
                enabled = canCancel
            ) {
                Text(cancelButtonText)
            }
        }
    }
}

@Composable
private fun AdminTopBar(onLogout: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PageHeader("Админ панель")
        Button(
            onClick = onLogout,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = HomeRed)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color.White)
                Text("Выйти", color = Color.White, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun AdminSeatCard(
    seat: Seat,
    onUpdateSeatName: (Seat, String) -> Unit,
    onDeactivateSeat: (Seat) -> Unit,
    onRestoreSeat: (Seat) -> Unit
) {
    var showNameDialog by remember { mutableStateOf(false) }
    var showDeactivateDialog by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = HomeCardBg),
        border = BorderStroke(1.dp, HomeStroke)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "Место #${seat.id}",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black
                )
            }
            AdminInfoLine("Название", seat.name.displaySeatName())
            AdminInfoLine("Тип", seat.type)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { showNameDialog = true }, modifier = Modifier.weight(1f)) {
                    Text("Название", maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                if (seat.isActive) {
                    Button(
                        onClick = { showDeactivateDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = HomeRed),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Деактивировать", color = Color.White, fontWeight = FontWeight.Black, maxLines = 1)
                    }
                } else {
                    Button(
                        onClick = { onRestoreSeat(seat) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = HomeGreen),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Восстановить", color = Color.White, fontWeight = FontWeight.Black, maxLines = 1)
                    }
                }
            }
        }
    }

    if (showNameDialog) {
        UpdateSeatNameDialog(
            initialName = seat.name,
            onDismiss = { showNameDialog = false },
            onConfirm = { name ->
                showNameDialog = false
                onUpdateSeatName(seat, name)
            }
        )
    }

    if (showDeactivateDialog) {
        ConfirmDeactivateDialog(
            seat = seat,
            onDismiss = { showDeactivateDialog = false },
            onConfirm = {
                showDeactivateDialog = false
                onDeactivateSeat(seat)
            }
        )
    }
}

@Composable
private fun AdminInfoLine(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(label, color = HomeMuted, modifier = Modifier.weight(0.38f))
        Text(value, color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.62f))
    }
}

@Composable
private fun UpdateSeatNameDialog(
    initialName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var value by remember(initialName) { mutableStateOf(initialName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = HomeFieldBg,
        title = { Text("Изменить название", color = Color.White, fontWeight = FontWeight.Black) },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = { value = it },
                label = { Text("Новое название") },
                singleLine = true
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(value) }) { Text("Сохранить") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена", color = HomeMuted) }
        }
    )
}

@Composable
private fun ConfirmDeactivateDialog(
    seat: Seat,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = HomeFieldBg,
        title = { Text("Деактивировать место?", color = Color.White, fontWeight = FontWeight.Black) },
        text = {
            Column {
                Text("Место ${seat.name.displaySeatName()} останется на карте, но станет недоступно для выбора.", color = Color.White)
                Spacer(Modifier.height(8.dp))
                Text("Пользователи будут видеть его как недоступное.", color = HomeMuted)
            }
        },
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = HomeRed)) {
                Text("Деактивировать")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена", color = HomeMuted) }
        }
    )
}

private fun String.toRuStatus(): String = when (lowercase()) {
    "pending" -> "ожидает"
    "confirmed" -> "подтверждена"
    "cancelled" -> "отменена"
    else -> this
}

private fun String.statusColor(): Color = when (lowercase()) {
    "cancelled" -> HomeRed
    "confirmed" -> HomeGreen
    else -> HomeMuted
}
