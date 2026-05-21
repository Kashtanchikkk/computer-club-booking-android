package com.example.computerclub.data.model

data class LoginRequestDto(
    val email: String,
    val password: String
)

data class RegisterRequestDto(
    val email: String,
    val password: String,
    val name: String
)

data class AuthResponseDto(
    val token: String?,
    val accessToken: String?,
    val refreshToken: String?,
    val user: UserDto
)

data class UserDto(
    val id: Int,
    val email: String,
    val name: String,
    val role: String
)

data class ComputerClubDto(
    val id: Int,
    val name: String,
    val address: String,
    val rating: Double
)

data class SeatDto(
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
    val isActive: Boolean
)

data class SeatTypeDto(
    val id: Int,
    val name: String,
    val pricePerHour: Double,
    val processor: String,
    val gpu: String,
    val ram: String,
    val monitor: String
)

data class SeatLayoutDto(
    val id: String,
    val label: String,
    val room: String,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    val color: String,
    val displayText: String
)

data class BookingDto(
    val id: Int,
    val userId: Int,
    val seatId: Int,
    val seatName: String,
    val startTime: String,
    val endTime: String,
    val status: String,
    val totalPrice: Double
)

data class CreateBookingRequestDto(
    val seatId: Int,
    val startTime: String,
    val endTime: String
)

data class RefreshTokenRequestDto(
    val refreshToken: String
)

data class ErrorResponseDto(
    val code: String?,
    val message: String?
)
