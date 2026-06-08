package com.example.computerclub.presentation.home.utils

import com.example.computerclub.domain.model.Booking
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

internal object BookingTimeUtils {
    private val zoneOffset = ZoneOffset.ofHours(3)
    private val displayDateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM HH:mm")
    private val shortDateFormatter = DateTimeFormatter.ofPattern("dd.MM")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val ruFullDateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("ru"))
    private const val minutesInDay = 24 * 60
    private const val slotStepMinutes = 30
    private const val openingHour = 9
    private const val maxDurationHours = 24
    private const val cancelLimitHours = 4L

    fun defaultStartTime(): String {
        val now = now()
        val minute = minAvailableMinute(dateOffset = 0, respectOpeningHour = true)

        return LocalDateTime.of(
            now.toLocalDate(),
            LocalTime.of(minute / 60, minute % 60)
        ).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    fun defaultEndTime(): String {
        val now = now()
        val minute = minAvailableMinute(dateOffset = 0, respectOpeningHour = true)

        return LocalDateTime.of(
            now.toLocalDate(),
            LocalTime.of(minute / 60, minute % 60)
        )
            .plusHours(2)
            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    fun calculateBookingPeriod(
        dateOffset: Int,
        startMinuteOfDay: Int,
        durationHours: Int
    ): Pair<String, String> {
        val safeMinute = startMinuteOfDay.coerceAtLeast(
            minAvailableMinute(dateOffset = dateOffset, respectOpeningHour = true)
        )
        val startDate = today().plusDays(dateOffset.toLong())
        val start = LocalDateTime.of(
            startDate,
            LocalTime.of(safeMinute / 60, safeMinute % 60)
        )
        val end = start.plusHours(durationHours.toLong())

        return start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) to
            end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    fun availableStartSlots(dateOffset: Int): List<Int> =
        generateSequence(0) { it + slotStepMinutes }
            .takeWhile { it <= 23 * 60 + 30 }
            .filter { it >= uiMinAvailableMinute(dateOffset) }
            .toList()

    fun uiMinAvailableMinute(dateOffset: Int): Int =
        minAvailableMinute(dateOffset = dateOffset, respectOpeningHour = false)

    fun coerceStartMinute(dateOffset: Int, minute: Int): Int =
        minute.coerceAtLeast(uiMinAvailableMinute(dateOffset))

    fun parseStartMinute(start: String): Int =
        parseDateTime(start)
            ?.let { it.hour * 60 + it.minute }
            ?: uiMinAvailableMinute(0)

    fun formatMinuteOfDay(minuteOfDay: Int): String =
        "%02d:%02d".format(minuteOfDay / 60, minuteOfDay % 60)

    fun formatDisplayDateTime(value: String): String =
        parseDateTime(value)?.format(displayDateTimeFormatter) ?: value

    fun formatRuDate(date: LocalDate): String =
        date.format(ruFullDateFormatter)

    fun monthTitle(month: YearMonth): String =
        month.month
            .getDisplayName(TextStyle.FULL_STANDALONE, Locale.forLanguageTag("ru"))
            .replaceFirstChar { it.titlecase() } + " ${month.year}"

    fun durationSlots(): List<Int> =
        (1..maxDurationHours).toList()

    fun formatBookingPreviewStart(start: LocalDateTime, end: LocalDateTime): String =
        if (start.toLocalDate() == end.toLocalDate()) start.format(timeFormatter) else start.format(displayDateTimeFormatter)

    fun formatBookingPreviewEnd(start: LocalDateTime, end: LocalDateTime): String =
        if (start.toLocalDate() == end.toLocalDate()) end.format(timeFormatter) else end.format(displayDateTimeFormatter)

    fun formatShortDate(value: LocalDateTime): String =
        value.format(shortDateFormatter)

    fun formatDuration(start: LocalDateTime, end: LocalDateTime): String {
        val duration = Duration.between(start, end)
        val hours = duration.toHours()
        val minutes = duration.toMinutes() % 60

        return buildString {
            if (hours > 0) append("$hours ${hourWord(hours.toInt())}")
            if (minutes > 0) {
                if (isNotEmpty()) append(" ")
                append("$minutes мин")
            }
            if (isEmpty()) append("меньше минуты")
        }
    }

    fun parseDateTime(value: String): LocalDateTime? =
        runCatching { LocalDateTime.parse(value) }.getOrNull()

    fun today(): LocalDate =
        now().toLocalDate()

    fun nowDateTime(): LocalDateTime =
        now().toLocalDateTime()

    fun isActiveBooking(booking: Booking): Boolean {
        if (booking.status == "cancelled") return false
        val end = parseDateTime(booking.endTime) ?: return false
        return end.isAfter(nowDateTime())
    }

    fun canCancelByRule(booking: Booking): Boolean {
        if (!isActiveBooking(booking)) return false
        val start = parseDateTime(booking.startTime) ?: return false
        return start.minusHours(cancelLimitHours).isAfter(nowDateTime())
    }

    fun overlaps(startTime: String, endTime: String, booking: Booking): Boolean {
        val selectedStart = parseDateTime(startTime) ?: return false
        val selectedEnd = parseDateTime(endTime) ?: return false
        val bookingStart = parseDateTime(booking.startTime) ?: return false
        val bookingEnd = parseDateTime(booking.endTime) ?: return false

        return bookingStart < selectedEnd && bookingEnd > selectedStart
    }

    fun hourWord(value: Int): String = when {
        value % 10 == 1 && value % 100 != 11 -> "час"
        value % 10 in 2..4 && value % 100 !in 12..14 -> "часа"
        else -> "часов"
    }

    private fun minAvailableMinute(dateOffset: Int, respectOpeningHour: Boolean): Int {
        val now = now()
        val minute = if (dateOffset == 0) {
            if (now.minute == 0) now.hour * 60 else (now.hour + 1) * 60
        } else {
            0
        }

        return if (respectOpeningHour && dateOffset == 0) {
            minute.coerceAtLeast(openingHour * 60)
        } else {
            minute
        }
    }

    private fun now(): ZonedDateTime =
        ZonedDateTime.now(zoneOffset)
}
