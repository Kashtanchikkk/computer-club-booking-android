package com.example.computerclub.presentation.home.utils

import com.example.computerclub.domain.model.Booking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime

class BookingTimeUtilsTest {

    @Test
    fun `formats date and time for booking display`() {
        val formatted = BookingTimeUtils.formatDisplayDateTime("2026-06-10T14:30:00")

        assertEquals("10.06 14:30", formatted)
    }

    @Test
    fun `formats duration with hours and minutes`() {
        val start = LocalDateTime.parse("2026-06-10T12:00:00")
        val end = LocalDateTime.parse("2026-06-10T14:30:00")

        val duration = BookingTimeUtils.formatDuration(start, end)

        assertEquals("2 часа 30 мин", duration)
    }

    @Test
    fun `active booking returns true when end time is in future`() {
        val now = minskNow()
        val booking = testBooking(
            start = now.plusHours(1),
            end = now.plusHours(2)
        )

        assertTrue(BookingTimeUtils.isActiveBooking(booking))
    }

    @Test
    fun `active booking returns false for cancelled booking`() {
        val now = minskNow()
        val booking = testBooking(
            start = now.plusHours(1),
            end = now.plusHours(2),
            status = "cancelled"
        )

        assertFalse(BookingTimeUtils.isActiveBooking(booking))
    }

    @Test
    fun `canCancelByRule returns true when start is more than four hours away`() {
        val now = minskNow()
        val booking = testBooking(
            start = now.plusHours(5),
            end = now.plusHours(7)
        )

        assertTrue(BookingTimeUtils.canCancelByRule(booking))
    }

    @Test
    fun `canCancelByRule returns false when start is less than four hours away`() {
        val now = minskNow()
        val booking = testBooking(
            start = now.plusHours(3),
            end = now.plusHours(5)
        )

        assertFalse(BookingTimeUtils.canCancelByRule(booking))
    }

    @Test
    fun `overlaps returns true when new booking is inside existing booking`() {
        val booking = testBooking(
            start = LocalDateTime.parse("2026-06-10T10:00:00"),
            end = LocalDateTime.parse("2026-06-10T14:00:00")
        )

        val overlaps = BookingTimeUtils.overlaps(
            startTime = "2026-06-10T11:00:00",
            endTime = "2026-06-10T12:00:00",
            booking = booking
        )

        assertTrue(overlaps)
    }

    @Test
    fun `overlaps returns false when new booking is before existing booking`() {
        val booking = testBooking(
            start = LocalDateTime.parse("2026-06-10T10:00:00"),
            end = LocalDateTime.parse("2026-06-10T14:00:00")
        )

        val overlaps = BookingTimeUtils.overlaps(
            startTime = "2026-06-10T08:00:00",
            endTime = "2026-06-10T10:00:00",
            booking = booking
        )

        assertFalse(overlaps)
    }

    @Test
    fun `overlaps returns false when new booking is after existing booking`() {
        val booking = testBooking(
            start = LocalDateTime.parse("2026-06-10T10:00:00"),
            end = LocalDateTime.parse("2026-06-10T14:00:00")
        )

        val overlaps = BookingTimeUtils.overlaps(
            startTime = "2026-06-10T14:00:00",
            endTime = "2026-06-10T16:00:00",
            booking = booking
        )

        assertFalse(overlaps)
    }

    private fun testBooking(
        start: LocalDateTime,
        end: LocalDateTime,
        status: String = "confirmed"
    ): Booking = Booking(
        id = 1,
        seatId = 1,
        seatName = "01",
        startTime = start.toString(),
        endTime = end.toString(),
        status = status,
        totalPrice = 500.0
    )

    private fun minskNow(): LocalDateTime =
        ZonedDateTime.now(ZoneOffset.ofHours(3)).toLocalDateTime()
}
