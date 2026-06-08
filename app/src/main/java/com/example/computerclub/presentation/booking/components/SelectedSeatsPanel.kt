package com.example.computerclub.presentation.booking.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.example.computerclub.domain.model.Seat
import com.example.computerclub.presentation.booking.model.ClubSeatLayout
import com.example.computerclub.presentation.common.formatPrice
import com.example.computerclub.ui.theme.HomeCardBg
import com.example.computerclub.ui.theme.HomeMuted
import com.example.computerclub.ui.theme.HomeRed

@Composable
internal fun SelectedSeatsPanel(
    seats: List<Seat>,
    layouts: List<ClubSeatLayout>
) {
    val totalPerHour = seats.sumOf { it.pricePerHour }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = HomeCardBg),
        border = BorderStroke(1.dp, HomeRed)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Выбрано мест: ${seats.size}",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black
            )

            Text(
                text = seats.joinToString(", ") { seat ->
                    layouts.firstOrNull { it.id == seat.name }?.label ?: seat.name
                },
                color = HomeMuted,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${totalPerHour.formatPrice()} ₽/ч",
                color = HomeRed,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black
            )

            Text(
                text = "Нажмите на выбранное место еще раз, чтобы убрать его",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
