package com.example.computerclub.presentation.booking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.computerclub.ui.theme.HomeCardBg
import com.example.computerclub.ui.theme.HomeMuted
import com.example.computerclub.ui.theme.HomeRed
import com.example.computerclub.ui.theme.HomeStroke
import com.example.computerclub.presentation.home.utils.BookingTimeUtils
import java.time.LocalDate
import java.time.YearMonth

@Composable
internal fun DateAndTimeStep(
    selectedDateOffset: Int,
    selectedStartMinute: Int,
    selectedDuration: Int,
    onDateSelected: (Int) -> Unit,
    onStartSelected: (Int) -> Unit,
    onDurationSelected: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        MonthCalendar(selectedDateOffset = selectedDateOffset, onDateSelected = onDateSelected)
        val startSlots = availableStartSlots(selectedDateOffset)
            .filter { it % 60 == 0 }
            .ifEmpty { availableStartSlots(selectedDateOffset) }
        TimePickerRow(
            title = "Время начала брони",
            items = startSlots,
            selectedItem = selectedStartMinute,
            itemText = ::formatMinuteOfDay,
            onItemSelected = onStartSelected
        )

        val endSlots = endDurationSlots(selectedStartMinute)
        TimePickerRow(
            title = "Время окончания брони",
            items = endSlots,
            selectedItem = selectedDuration,
            itemText = { duration -> formatMinuteOfDay((selectedStartMinute + duration * 60) % (24 * 60)) },
            onItemSelected = onDurationSelected
        )

        BookingPeriodSummary(
            selectedDateOffset = selectedDateOffset,
            selectedStartMinute = selectedStartMinute,
            selectedDuration = selectedDuration
        )
    }
}

@Composable
private fun TimePickerRow(
    title: String,
    items: List<Int>,
    selectedItem: Int,
    itemText: (Int) -> String,
    onItemSelected: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(title, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items) { item ->
                TimeChip(
                    text = itemText(item),
                    selected = item == selectedItem,
                    onClick = { onItemSelected(item) }
                )
            }
        }
    }
}

@Composable
private fun TimeChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(76.dp)
            .height(58.dp)
            .background(if (selected) HomeRed else HomeCardBg, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun BookingPeriodSummary(
    selectedDateOffset: Int,
    selectedStartMinute: Int,
    selectedDuration: Int
) {
    val date = minskToday().plusDays(selectedDateOffset.toLong())
    val endMinute = (selectedStartMinute + selectedDuration * 60) % (24 * 60)
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = HomeCardBg),
        border = BorderStroke(1.dp, HomeStroke)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("Дата", color = HomeMuted, fontWeight = FontWeight.Bold)
                Text(BookingTimeUtils.formatRuDate(date), color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("Время", color = HomeMuted, fontWeight = FontWeight.Bold)
                Text(
                    "${formatMinuteOfDay(selectedStartMinute)} – ${formatMinuteOfDay(endMinute)}",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text("$selectedDuration ${selectedDuration.hourWord()}", color = HomeMuted, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MonthCalendar(selectedDateOffset: Int, onDateSelected: (Int) -> Unit) {
    val today = minskToday()
    val month = YearMonth.from(today)
    val firstDay = month.atDay(1)
    val firstWeekdayOffset = firstDay.dayOfWeek.value - 1
    val cells = List(firstWeekdayOffset) { null } + (1..month.lengthOfMonth()).map { month.atDay(it) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(BookingTimeUtils.monthTitle(month), color = Color.White, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс").forEach { day ->
                Text(day, color = HomeMuted, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
            }
        }
        cells.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                week.forEach { date ->
                    CalendarDayCell(
                        date = date,
                        today = today,
                        selectedDateOffset = selectedDateOffset,
                        onDateSelected = onDateSelected,
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(7 - week.size) {
                    Spacer(modifier = Modifier.weight(1f).height(42.dp))
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    date: LocalDate?,
    today: LocalDate,
    selectedDateOffset: Int,
    onDateSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (date == null) {
        Box(modifier = modifier.height(42.dp))
        return
    }
    val offset = java.time.temporal.ChronoUnit.DAYS.between(today, date).toInt()
    val selected = offset == selectedDateOffset
    val isPast = date.isBefore(today)
    Box(
        modifier = modifier
            .height(42.dp)
            .background(
                when {
                    selected -> HomeRed
                    isPast -> Color.Transparent
                    else -> Color.Transparent
                },
                RoundedCornerShape(12.dp)
            )
            .clickable(enabled = !isPast) { onDateSelected(offset) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            date.dayOfMonth.toString(),
            color = if (isPast) Color(0xFF3F4046) else Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black
        )
    }
}

internal fun availableStartSlots(dateOffset: Int): List<Int> =
    BookingTimeUtils.availableStartSlots(dateOffset)

internal fun uiMinAvailableMinute(dateOffset: Int): Int =
    BookingTimeUtils.uiMinAvailableMinute(dateOffset)

internal fun coerceStartMinute(dateOffset: Int, minute: Int): Int =
    BookingTimeUtils.coerceStartMinute(dateOffset, minute)

internal fun parseStartMinute(start: String): Int =
    BookingTimeUtils.parseStartMinute(start)

internal fun formatMinuteOfDay(minuteOfDay: Int): String =
    BookingTimeUtils.formatMinuteOfDay(minuteOfDay)

internal fun String.bookingDisplayDate(): String =
    BookingTimeUtils.formatDisplayDateTime(this)

private fun minskToday(): LocalDate = BookingTimeUtils.today()

private fun endDurationSlots(selectedStartMinute: Int): List<Int> =
    BookingTimeUtils.durationSlots()

private fun Int.hourWord(): String = BookingTimeUtils.hourWord(this)
