package com.example.computerclub.domain.usecase

import com.example.computerclub.domain.model.MapObject
import com.example.computerclub.domain.repository.ComputerClubRepository

class LoadSeatsUseCase(private val repository: ComputerClubRepository) {
    suspend operator fun invoke(clubId: Int, typeId: Int) = repository.loadSeats(clubId, typeId)
}

class LoadAllSeatsUseCase(private val repository: ComputerClubRepository) {
    suspend operator fun invoke(clubId: Int) = repository.loadAllSeats(clubId)
}

class LoadClubMapUseCase(private val repository: ComputerClubRepository) {
    suspend operator fun invoke(clubId: Int): Result<List<MapObject>> = repository.loadClubMap(clubId)
}

class LoadSeatTypesUseCase(private val repository: ComputerClubRepository) {
    suspend operator fun invoke() = repository.loadSeatTypes()
}

class LoadClubsUseCase(private val repository: ComputerClubRepository) {
    suspend operator fun invoke() = repository.loadClubs()
}

class CreateBookingUseCase(private val repository: ComputerClubRepository) {
    suspend operator fun invoke(seatId: Int, startTime: String, endTime: String) =
        repository.createBooking(seatId, startTime, endTime)
}

class LoadMyBookingsUseCase(private val repository: ComputerClubRepository) {
    suspend operator fun invoke() = repository.loadMyBookings()
}

class CancelBookingUseCase(private val repository: ComputerClubRepository) {
    suspend operator fun invoke(bookingId: Int) = repository.cancelBooking(bookingId)
}

class GetAllBookingsUseCase(private val repository: ComputerClubRepository) {
    suspend operator fun invoke() = repository.getAllBookings()
}

class AdminCancelBookingUseCase(private val repository: ComputerClubRepository) {
    suspend operator fun invoke(bookingId: Int) = repository.adminCancelBooking(bookingId)
}

class DeactivateSeatUseCase(private val repository: ComputerClubRepository) {
    suspend operator fun invoke(seatId: Int) = repository.deactivateSeat(seatId)
}

class UpdateSeatNameUseCase(private val repository: ComputerClubRepository) {
    suspend operator fun invoke(seatId: Int, name: String) = repository.updateSeatName(seatId, name)
}

class UpdateSeatStatusUseCase(private val repository: ComputerClubRepository) {
    suspend operator fun invoke(seatId: Int, isActive: Boolean) = repository.updateSeatStatus(seatId, isActive)
}
