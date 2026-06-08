package com.example.computerclub.presentation.home.utils

import com.example.computerclub.presentation.home.ComputerClubUiState

internal fun ComputerClubUiState.withBookingPeriod(
    dateOffset: Int,
    startMinuteOfDay: Int,
    durationHours: Int
): ComputerClubUiState {
    val (startTime, endTime) = BookingTimeUtils.calculateBookingPeriod(
        dateOffset = dateOffset,
        startMinuteOfDay = startMinuteOfDay,
        durationHours = durationHours
    )

    return copy(
        startTime = startTime,
        endTime = endTime,
        selectedDurationHours = durationHours
    )
}

internal fun ComputerClubUiState.bookedSeatIdsForSelectedPeriod(): Set<Int> =
    bookings
        .filter { it.status != "cancelled" && BookingTimeUtils.overlaps(startTime, endTime, it) }
        .map { it.seatId }
        .toSet()
