package com.example.computerclub.presentation.booking.model

import androidx.compose.ui.graphics.Color
import com.example.computerclub.domain.model.MapObject
import com.example.computerclub.domain.model.MapObjectType

enum class MapObjectTypeModel {
    ROOM,
    WALL,
    SEAT
}

data class MapObjectModel(
    val id: Long,
    val type: MapObjectTypeModel,
    val title: String?,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    val seatId: Long? = null
)

internal fun MapObject.toUiModel(): MapObjectModel = MapObjectModel(
    id = id,
    type = MapObjectTypeModel.valueOf(type.name),
    title = title,
    x = x,
    y = y,
    width = width,
    height = height,
    seatId = seatId
)

internal fun MapObjectModel.toSeatLayout(): ClubSeatLayout = ClubSeatLayout(
    id = seatId?.toString() ?: id.toString(),
    seatId = seatId?.toInt(),
    label = title.orEmpty(),
    room = "",
    x = x,
    y = y,
    width = width,
    height = height,
    color = Color(0xFFFFF1B8),
    displayText = title ?: seatId?.toString() ?: id.toString()
)
