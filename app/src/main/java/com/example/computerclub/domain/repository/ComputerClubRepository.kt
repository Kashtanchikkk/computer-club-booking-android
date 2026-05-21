package com.example.computerclub.domain.repository

import com.example.computerclub.domain.model.Booking
import com.example.computerclub.domain.model.ComputerClubBranch
import com.example.computerclub.domain.model.Seat
import com.example.computerclub.domain.model.SeatLayout
import com.example.computerclub.domain.model.SeatType
import com.example.computerclub.domain.model.User
import kotlinx.coroutines.flow.Flow

interface ComputerClubRepository {
    val token: Flow<String?>

    suspend fun login(email: String, password: String): Result<User>

    suspend fun register(email: String, password: String, name: String): Result<User>

    suspend fun logout()

    suspend fun loadProfile(): Result<User>

    suspend fun loadClubs(): Result<List<ComputerClubBranch>>

    suspend fun loadSeatTypes(): Result<List<SeatType>>

    suspend fun loadSeats(clubId: Int, typeId: Int): Result<List<Seat>>

    suspend fun loadAllSeats(clubId: Int): Result<List<Seat>>

    suspend fun getSeatLayouts(): Result<List<SeatLayout>>

    suspend fun createBooking(seatId: Int, startTime: String, endTime: String): Result<Booking>

    suspend fun loadMyBookings(): Result<List<Booking>>

    suspend fun cancelBooking(bookingId: Int): Result<Unit>
}
