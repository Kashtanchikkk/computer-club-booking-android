package com.example.computerclub.presentation.home.utils

import com.example.computerclub.presentation.home.ComputerClubUiState

internal fun ComputerClubUiState.freeSeatsCount(bookedSeatIds: Set<Int>): Int {
    if (allSeats.isEmpty()) return 0
    return allSeats.count { it.id !in bookedSeatIds }
}
