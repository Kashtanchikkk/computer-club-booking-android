package com.example.computerclub.presentation.booking.model

import androidx.compose.ui.graphics.Color
import com.example.computerclub.domain.model.SeatLayout

data class ClubSeatLayout(
    val id: String,
    val label: String,
    val room: String,
    val x: Int,
    val y: Int,
    val width: Int = 50,
    val height: Int = 42,
    val color: Color,
    val displayText: String = id
)

internal fun SeatLayout.toUiModel(): ClubSeatLayout = ClubSeatLayout(
    id = id,
    label = label,
    room = room,
    x = x,
    y = y,
    width = width,
    height = height,
    color = color.toComposeColor(),
    displayText = displayText
)

private fun String.toComposeColor(): Color =
    runCatching { Color(android.graphics.Color.parseColor(this)) }
        .getOrDefault(Color(0xFFFFF1B8))
