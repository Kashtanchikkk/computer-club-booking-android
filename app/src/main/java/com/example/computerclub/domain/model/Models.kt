package com.example.computerclub.domain.model

data class User(
    val id: Int,
    val email: String,
    val name: String,
    val role: String
)

data class Seat(
    val id: Int,
    val clubId: Int,
    val typeId: Int,
    val name: String,
    val type: String,
    val pricePerHour: Double,
    val processor: String,
    val gpu: String,
    val ram: String,
    val monitor: String,
    val isActive: Boolean = true
)

data class SeatType(
    val id: Int,
    val name: String,
    val pricePerHour: Double,
    val processor: String,
    val gpu: String,
    val ram: String,
    val monitor: String
)

data class SeatLayout(
    val id: String,
    val seatId: Int? = null,
    val label: String,
    val room: String,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    val color: String,
    val displayText: String
)

enum class MapObjectType {
    ROOM,
    WALL,
    SEAT
}

data class MapObject(
    val id: Long,
    val clubId: Long? = null,
    val type: MapObjectType,
    val title: String?,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    val seatId: Long? = null
)

data class ComputerClubBranch(
    val id: Int,
    val name: String,
    val address: String,
    val rating: Double
)

data class Booking(
    val id: Int,
    val seatId: Int,
    val seatName: String,
    val startTime: String,
    val endTime: String,
    val status: String,
    val totalPrice: Double
)
