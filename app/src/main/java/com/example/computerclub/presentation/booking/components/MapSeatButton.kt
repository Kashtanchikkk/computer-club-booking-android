package com.example.computerclub.presentation.booking.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.computerclub.domain.model.Seat
import com.example.computerclub.presentation.booking.model.ClubSeatLayout
import com.example.computerclub.presentation.home.displaySeatName

@Composable
internal fun MapSeatButton(
    layout: ClubSeatLayout,
    seat: Seat?,
    busy: Boolean,
    selected: Boolean,
    onSeatClick: (Seat) -> Unit
) {
    val unavailable = seat == null || !seat.isActive
    val enabled = !unavailable && !busy

    Box(
        modifier = Modifier
            .offset(layout.x.dp, layout.y.dp)
            .size(width = layout.width.dp, height = layout.height.dp)
            .background(
                color = when {
                    selected -> Color(0xFFFFFF66)
                    busy -> Color(0xFF4A4A4F)
                    unavailable -> Color(0xFF2E3138)
                    else -> Color(0xFFF6F0DA)
                },
                shape = RoundedCornerShape(7.dp)
            )
            .border(
                width = 1.dp,
                color = when {
                    selected -> Color(0xFFFFFF00)
                    busy -> Color(0xFF77777C)
                    unavailable -> Color(0xFF555963)
                    else -> layout.color
                },
                shape = RoundedCornerShape(7.dp)
            )
            .clickable(enabled = enabled) {
                seat?.let(onSeatClick)
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = seat?.name?.displaySeatName() ?: layout.displayText,
            color = if (unavailable || busy) Color(0xFFB5B7BF) else Color(0xFF111217),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
    }
}
