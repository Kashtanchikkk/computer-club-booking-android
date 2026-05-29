package com.example.computerclub.presentation.booking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.example.computerclub.domain.model.Seat
import com.example.computerclub.presentation.booking.components.ClubMapCanvas
import com.example.computerclub.presentation.booking.components.MapLegend
import com.example.computerclub.presentation.booking.components.SelectedSeatsPanel
import com.example.computerclub.presentation.booking.model.ClubSeatLayout
import com.example.computerclub.presentation.home.EmptyCard
import com.example.computerclub.presentation.home.PrimaryButton

@Composable
internal fun ClubMap(
    seats: List<Seat>,
    layouts: List<ClubSeatLayout>,
    isLayoutsLoading: Boolean,
    layoutsError: String?,
    bookedSeatIds: Set<Int>,
    onSeatsSelected: (List<Seat>) -> Unit
) {
    var selectedSeats by remember { mutableStateOf<List<Seat>>(emptyList()) }

    LaunchedEffect(bookedSeatIds, seats) {
        selectedSeats = selectedSeats.filter { seat ->
            seat.id !in bookedSeatIds && seats.any { it.id == seat.id }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        when {
            isLayoutsLoading -> EmptyCard("Загрузка схемы клуба...")
            layoutsError != null -> EmptyCard(layoutsError)
            else -> ClubMapCanvas(
                seats = seats,
                bookedSeatIds = bookedSeatIds,
                selectedSeats = selectedSeats,
                layouts = layouts,
                onSeatClick = { clickedSeat ->
                    selectedSeats =
                        if (selectedSeats.any { it.id == clickedSeat.id }) {
                            selectedSeats.filterNot { it.id == clickedSeat.id }
                        } else {
                            selectedSeats + clickedSeat
                        }
                }
            )
        }

        MapLegend()

        if (layoutsError == null && !isLayoutsLoading && selectedSeats.isNotEmpty()) {
            SelectedSeatsPanel(selectedSeats, layouts)
            PrimaryButton("Подтверждение") {
                onSeatsSelected(selectedSeats)
            }
        } else if (layoutsError == null && !isLayoutsLoading) {
            EmptyCard("Выберите одно или несколько мест на схеме")
        }
    }
}
