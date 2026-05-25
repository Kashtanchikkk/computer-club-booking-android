package com.example.computerclub.presentation.home

import com.example.computerclub.presentation.home.components.MainHeader
import com.example.computerclub.presentation.home.components.ZoneGrid

internal fun androidx.compose.foundation.lazy.LazyListScope.mainTab(
    state: ComputerClubUiState,
    bookedSeatIds: Set<Int>,
    onBookingStartRequested: () -> Unit,
    onTabSelected: (HomeTab) -> Unit,
    onCancelBooking: (Int) -> Unit
) {
    item {
        MainHeader(
            name = state.user?.name ?: "Игрок",
            onProfile = { onTabSelected(HomeTab.Profile) }
        )
    }
    item {
        ZoneGrid(
            state = state,
            bookedSeatIds = bookedSeatIds,
            onBookingClick = onBookingStartRequested,
            onCancelBooking = onCancelBooking
        )
    }
    item { StatusAndLoading(state) }
}
