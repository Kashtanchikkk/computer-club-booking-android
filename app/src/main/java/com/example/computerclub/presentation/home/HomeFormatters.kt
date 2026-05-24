package com.example.computerclub.presentation.home

import com.example.computerclub.domain.model.Booking
import com.example.computerclub.presentation.home.utils.BookingTimeUtils

internal fun String.displaySeatName(): String {
    val numericName = trim().toIntOrNull() ?: return this
    return "PC-$numericName"
}

internal fun Booking.isActiveBooking(): Boolean {
    return BookingTimeUtils.isActiveBooking(this)
}

internal fun Booking.canCancelByRule(): Boolean {
    return BookingTimeUtils.canCancelByRule(this)
}
