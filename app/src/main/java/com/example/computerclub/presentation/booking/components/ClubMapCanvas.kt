package com.example.computerclub.presentation.booking.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.computerclub.domain.model.Seat
import com.example.computerclub.presentation.booking.model.ClubSeatLayout
import com.example.computerclub.ui.theme.HomeStroke

@Composable
internal fun ClubMapCanvas(
    seats: List<Seat>,
    bookedSeatIds: Set<Int>,
    selectedSeats: List<Seat>,
    layouts: List<ClubSeatLayout>,
    onSeatClick: (Seat) -> Unit
) {
    var scale by remember { mutableStateOf(0.45f) }
    var mapOffset by remember { mutableStateOf(Offset.Zero) }
    val seatsById = seats.associateBy { it.id }
    val seatsByName = seats.associateBy { it.name }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(620.dp)
            .background(Color(0xFF0D1118), RoundedCornerShape(24.dp))
            .border(1.dp, HomeStroke, RoundedCornerShape(24.dp))
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.25f, 4f)
                    mapOffset += pan
                }
            },
        contentAlignment = Alignment.TopStart
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = mapOffset.x
                    translationY = mapOffset.y
                }
                .requiredWidth(1380.dp)
                .height(860.dp)
        ) {
            MapRoom("-> ВХОД", 8, 8, 220, 220)
            MapRoom("СТОЙКА\nАДМИНИСТРАТОРА", 228, 8, 210, 150)
            MapRoom("DUO 1", 438, 8, 170, 150)
            MapRoom("DUO 2", 608, 8, 165, 150)
            MapRoom("TRIO", 773, 8, 265, 150)
            MapRoom("SQUAD", 1038, 8, 105, 220)
            MapRoom("BOOTCAMP 1", 1143, 8, 165, 320)
            MapRoom("SQUAD", 122, 238, 110, 222)
            MapRoom("PS5 ROOM 1", 232, 238, 165, 210)
            MapRoom("STANDARD 1", 397, 218, 350, 165)
            MapWall(440, 328, 280)
            MapRoom("STANDARD 2", 397, 388, 350, 170)
            MapWall(440, 498, 280)
            MapRoom("BOOTCAMP 2", 887, 268, 130, 330)
            MapRoom("VIP ROOM", 1017, 335, 115, 190)
            MapRoom("PS5 ROOM 2", 1132, 335, 176, 190)
            MapRoom("WC", 1017, 525, 291, 82)
            MapRoom("DUO 3", 8, 525, 165, 120)
            MapRoom("DUO 4", 173, 525, 165, 120)
            MapRoom("STANDARD 3", 887, 625, 260, 270)
            MapRoom("TRIO", 1147, 625, 220, 155)

            layouts.forEach { layout ->
                val seat = layout.seatId?.let(seatsById::get) ?: seatsByName[layout.id]
                MapSeatButton(
                    layout = layout,
                    seat = seat,
                    busy = seat?.id in bookedSeatIds,
                    selected = selectedSeats.any { it.id == seat?.id },
                    onSeatClick = onSeatClick
                )
            }
        }
    }
}
