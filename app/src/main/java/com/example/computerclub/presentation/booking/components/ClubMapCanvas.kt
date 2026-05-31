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
import com.example.computerclub.presentation.booking.model.MapObjectModel
import com.example.computerclub.presentation.booking.model.MapObjectTypeModel
import com.example.computerclub.presentation.booking.model.toSeatLayout
import com.example.computerclub.ui.theme.HomeStroke

@Composable
internal fun ClubMapCanvas(
    seats: List<Seat>,
    bookedSeatIds: Set<Int>,
    selectedSeats: List<Seat>,
    mapObjects: List<MapObjectModel>,
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
            mapObjects.forEach { obj ->
                when (obj.type) {
                    MapObjectTypeModel.ROOM -> MapRoom(
                        title = obj.title.orEmpty(),
                        x = obj.x,
                        y = obj.y,
                        width = obj.width,
                        height = obj.height
                    )

                    MapObjectTypeModel.WALL -> MapWall(
                        x = obj.x,
                        y = obj.y,
                        width = obj.width,
                        height = obj.height
                    )

                    MapObjectTypeModel.SEAT -> {
                        val layout = obj.toSeatLayout()
                        val seat = obj.seatId?.toInt()?.let(seatsById::get) ?: seatsByName[layout.id]
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
    }
}
