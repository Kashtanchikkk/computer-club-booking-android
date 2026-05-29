package com.example.computerclub.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.computerclub.domain.model.Booking
import com.example.computerclub.domain.model.Seat
import com.example.computerclub.domain.model.User
import com.example.computerclub.domain.usecase.AdminCancelBookingUseCase
import com.example.computerclub.domain.usecase.DeactivateSeatUseCase
import com.example.computerclub.domain.usecase.GetAllBookingsUseCase
import com.example.computerclub.domain.usecase.LoadAllSeatsUseCase
import com.example.computerclub.domain.usecase.LoadClubsUseCase
import com.example.computerclub.domain.usecase.LoadSeatLayoutsUseCase
import com.example.computerclub.domain.usecase.UpdateSeatNameUseCase
import com.example.computerclub.domain.usecase.UpdateSeatStatusUseCase
import com.example.computerclub.presentation.home.utils.BookingTimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminUiState(
    val bookings: List<Booking> = emptyList(),
    val seats: List<Seat> = emptyList(),
    val selectedSection: AdminSection = AdminSection.Bookings,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

enum class AdminSection { Bookings, Seats }

class AdminViewModel(
    private val getAllBookingsUseCase: GetAllBookingsUseCase,
    private val adminCancelBookingUseCase: AdminCancelBookingUseCase,
    private val loadClubsUseCase: LoadClubsUseCase,
    private val loadAllSeatsUseCase: LoadAllSeatsUseCase,
    private val loadSeatLayoutsUseCase: LoadSeatLayoutsUseCase,
    private val deactivateSeatUseCase: DeactivateSeatUseCase,
    private val updateSeatNameUseCase: UpdateSeatNameUseCase,
    private val updateSeatStatusUseCase: UpdateSeatStatusUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(AdminUiState())
    val state: StateFlow<AdminUiState> = _state

    private var loadedForUserId: Int? = null

    fun onUserChanged(user: User?) {
        if (user?.role != ADMIN_ROLE) {
            loadedForUserId = null
            _state.value = AdminUiState()
            return
        }
        if (loadedForUserId == user.id) return
        loadedForUserId = user.id
        refreshAll()
    }

    fun selectSection(section: AdminSection) {
        _state.update { it.copy(selectedSection = section) }
        when (section) {
            AdminSection.Bookings -> loadBookings()
            AdminSection.Seats -> loadSeats()
        }
    }

    fun refreshAll() {
        launchRequest {
            loadBookingsInternal()
            loadSeatsInternal()
        }
    }

    fun loadBookings() {
        launchRequest { loadBookingsInternal() }
    }

    fun loadSeats() {
        launchRequest { loadSeatsInternal() }
    }

    fun cancelBooking(bookingId: Int) {
        if (state.value.bookings.any { it.id == bookingId && it.status.lowercase() == "cancelled" }) {
            _state.update { it.copy(errorMessage = "Эта бронь уже отменена") }
            return
        }
        if (state.value.bookings.any { it.id == bookingId && !BookingTimeUtils.isActiveBooking(it) }) {
            _state.update { it.copy(errorMessage = "Прошедшую бронь отменить нельзя") }
            return
        }
        launchRequest {
            adminCancelBookingUseCase(bookingId)
                .onSuccess {
                    _state.update { it.copy(successMessage = "Бронь отменена") }
                    loadBookingsInternal()
                }
                .onFailure { showError(it) }
        }
    }

    fun updateSeatName(seatId: Int, name: String, onSuccess: (Seat) -> Unit = {}) {
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) {
            _state.update { it.copy(errorMessage = "Введите новое название") }
            return
        }
        if (state.value.seats.any { it.id != seatId && it.name.normalizedSeatName() == trimmedName.normalizedSeatName() }) {
            _state.update { it.copy(errorMessage = "Место с таким названием уже есть") }
            return
        }
        launchRequest {
            updateSeatNameUseCase(seatId, trimmedName)
                .onSuccess { updatedSeat ->
                    _state.update { current ->
                        current.copy(
                            seats = current.seats.replaceSeat(updatedSeat),
                            successMessage = "Название места обновлено"
                        )
                    }
                    onSuccess(updatedSeat)
                }
                .onFailure { showError(it) }
        }
    }

    fun deactivateSeat(seatId: Int, onSuccess: (Seat) -> Unit = {}) {
        val seat = state.value.seats.firstOrNull { it.id == seatId }
        launchRequest {
            deactivateSeatUseCase(seatId)
                .onSuccess {
                    seat?.let { currentSeat ->
                        val inactiveSeat = currentSeat.copy(isActive = false)
                        _state.update { current ->
                            current.copy(seats = current.seats.replaceSeat(inactiveSeat))
                        }
                        onSuccess(inactiveSeat)
                    }
                    _state.update { it.copy(successMessage = "Место деактивировано") }
                }
                .onFailure { showError(it) }
        }
    }

    fun restoreSeat(seatId: Int, onSuccess: (Seat) -> Unit = {}) {
        launchRequest {
            updateSeatStatusUseCase(seatId, true)
                .onSuccess { restoredSeat ->
                    _state.update { current ->
                        current.copy(
                            seats = current.seats.replaceSeat(restoredSeat),
                            successMessage = "Место восстановлено"
                        )
                    }
                    onSuccess(restoredSeat)
                }
                .onFailure { showError(it) }
        }
    }

    private suspend fun loadBookingsInternal() {
        getAllBookingsUseCase()
            .onSuccess { bookings -> _state.update { it.copy(bookings = bookings) } }
            .onFailure { showError(it) }
    }

    private suspend fun loadSeatsInternal() {
        loadClubsUseCase()
            .onSuccess { clubs ->
                val clubId = clubs.firstOrNull()?.id
                if (clubId == null) {
                    _state.update { it.copy(seats = emptyList()) }
                    return
                }
                loadAllSeatsUseCase(clubId)
                    .onSuccess { activeSeats ->
                        _state.update { it.copy(seats = mergeSeatsWithLayout(clubId, activeSeats)) }
                    }
                    .onFailure { showError(it) }
            }
            .onFailure { showError(it) }
    }

    private suspend fun mergeSeatsWithLayout(clubId: Int, activeSeats: List<Seat>): List<Seat> {
        val layouts = loadSeatLayoutsUseCase().getOrElse { return activeSeats }
        val activeSeatsById = activeSeats.associateBy { it.id }
        val usedSeatIds = mutableSetOf<Int>()
        val seatsFromLayout = layouts
            .mapNotNull { layout ->
                val seatId = layout.seatId ?: return@mapNotNull null
                usedSeatIds += seatId
                activeSeatsById[seatId] ?: Seat(
                    id = seatId,
                    clubId = clubId,
                    typeId = 0,
                    name = layout.displayText.ifBlank { layout.label },
                    type = inferSeatType(layout.room, layout.displayText, layout.label),
                    pricePerHour = 0.0,
                    processor = "",
                    gpu = "",
                    ram = "",
                    monitor = "",
                    isActive = false
                )
            }
            .distinctBy { it.id }

        return seatsFromLayout + activeSeats.filter { it.id !in usedSeatIds }
    }

    private fun inferSeatType(room: String, displayText: String, label: String): String {
        val source = "$room $displayText $label"
        return when {
            source.contains("VIP", ignoreCase = true) -> "VIP"
            source.contains("PS", ignoreCase = true) -> "PS5"
            source.contains("BOOTCAMP", ignoreCase = true) -> "Bootcamp"
            else -> "Standard"
        }
    }

    private fun launchRequest(block: suspend () -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            block()
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun showError(error: Throwable) {
        _state.update { it.copy(errorMessage = error.message ?: "Ошибка") }
    }

    private fun List<Seat>.replaceSeat(seat: Seat): List<Seat> =
        if (any { it.id == seat.id }) {
            map { if (it.id == seat.id) seat else it }
        } else {
            this + seat
        }

    private fun String.normalizedSeatName(): String =
        trim().lowercase()

    class Factory(
        private val getAllBookingsUseCase: GetAllBookingsUseCase,
        private val adminCancelBookingUseCase: AdminCancelBookingUseCase,
        private val loadClubsUseCase: LoadClubsUseCase,
        private val loadAllSeatsUseCase: LoadAllSeatsUseCase,
        private val loadSeatLayoutsUseCase: LoadSeatLayoutsUseCase,
        private val deactivateSeatUseCase: DeactivateSeatUseCase,
        private val updateSeatNameUseCase: UpdateSeatNameUseCase,
        private val updateSeatStatusUseCase: UpdateSeatStatusUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            AdminViewModel(
                getAllBookingsUseCase = getAllBookingsUseCase,
                adminCancelBookingUseCase = adminCancelBookingUseCase,
                loadClubsUseCase = loadClubsUseCase,
                loadAllSeatsUseCase = loadAllSeatsUseCase,
                loadSeatLayoutsUseCase = loadSeatLayoutsUseCase,
                deactivateSeatUseCase = deactivateSeatUseCase,
                updateSeatNameUseCase = updateSeatNameUseCase,
                updateSeatStatusUseCase = updateSeatStatusUseCase
            ) as T
    }

    private companion object {
        const val ADMIN_ROLE = "admin"
    }
}
