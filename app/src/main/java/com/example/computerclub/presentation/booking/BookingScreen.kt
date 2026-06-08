package com.example.computerclub.presentation.booking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.computerclub.domain.model.Seat
import com.example.computerclub.presentation.common.StatusMessage
import com.example.computerclub.presentation.common.formatPrice
import com.example.computerclub.presentation.home.BackHeader
import com.example.computerclub.presentation.home.ComputerClubUiState
import com.example.computerclub.presentation.home.utils.BookingTimeUtils
import com.example.computerclub.ui.theme.HomeBg
import com.example.computerclub.ui.theme.HomeCardBg
import com.example.computerclub.ui.theme.HomeMuted
import com.example.computerclub.ui.theme.HomeRed
import com.example.computerclub.ui.theme.HomeStroke

@Composable
fun BookingScreen(
    state: ComputerClubUiState,
    seats: List<Seat>,
    onBack: () -> Unit,
    onPeriodSelected: (Int, Int, Int) -> Unit,
    onCreateBooking: () -> Unit
) {
    val totalPrice = seats.sumOf { it.pricePerHour } * state.selectedDurationHours

    Scaffold(containerColor = HomeBg) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(HomeBg)
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            BackHeader("Подтверждение", null, onBack)
            StepIndicator()

            Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = HomeCardBg), border = BorderStroke(1.dp, HomeStroke)) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    ConfirmRow("Места", seats.joinToString(", ") { it.code() })
                    ConfirmRow("Дата", state.startTime.displayDate().take(5))
                    ConfirmRow("Начало", state.startTime.displayDate().takeLast(5))
                    ConfirmRow("Конец", state.endTime.displayDate().takeLast(5))
                    ConfirmRow("Длительность", "${state.selectedDurationHours} часа")
                    ConfirmRow("Мест", seats.size.toString())
                    ConfirmRow("Тариф", "${seats.sumOf { it.pricePerHour }.formatPrice()} ₽/ч")
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Итого", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                Text("${totalPrice.formatPrice()} ₽", color = HomeRed, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
            }

            Button(onClick = onCreateBooking, modifier = Modifier.fillMaxWidth().height(58.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = HomeRed)) {
                Text("Забронировать", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
            }
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth().height(58.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = HomeCardBg)) {
                Text("Отмена", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
            }
            StatusMessage(state)
        }
    }
}

@Composable
private fun StepIndicator() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        StepItem("Дата и время", modifier = Modifier.weight(1f))
        StepItem("Место", modifier = Modifier.weight(1f))
        StepItem("Подтверждение", modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StepItem(text: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(modifier = Modifier.fillMaxWidth().height(5.dp).background(HomeRed, RoundedCornerShape(4.dp)))
        Text(
            text = text,
            color = HomeRed,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1
        )
    }
}

@Composable
private fun ConfirmRow(title: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title, color = HomeMuted, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(value, color = Color.White, textAlign = TextAlign.End, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
    }
}

private fun Seat.code(): String = if (displayType() == "Premium") "VIP-${name.filter { it.isDigit() }.ifBlank { id.toString() }}" else "PC-${name.filter { it.isDigit() }.ifBlank { id.toString() }}"
private fun Seat.displayType(): String = if (type.contains("VIP", true) || type.contains("Bootcamp", true)) "Premium" else "Standard"
private fun String.displayDate(): String = BookingTimeUtils.formatDisplayDateTime(this)
