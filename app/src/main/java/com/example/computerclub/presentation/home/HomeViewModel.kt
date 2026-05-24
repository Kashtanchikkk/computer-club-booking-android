package com.example.computerclub.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.computerclub.domain.model.Booking
import com.example.computerclub.domain.model.ComputerClubBranch
import com.example.computerclub.domain.model.Seat
import com.example.computerclub.domain.model.SeatType
import com.example.computerclub.domain.model.User
import com.example.computerclub.domain.usecase.CancelBookingUseCase
import com.example.computerclub.domain.usecase.CreateBookingUseCase
import com.example.computerclub.domain.usecase.LoadAllSeatsUseCase
import com.example.computerclub.domain.usecase.LoadClubsUseCase
import com.example.computerclub.domain.usecase.LoadMyBookingsUseCase
import com.example.computerclub.domain.usecase.LoadSeatLayoutsUseCase
import com.example.computerclub.domain.usecase.LoadSeatTypesUseCase
import com.example.computerclub.domain.usecase.LoadSeatsUseCase
import com.example.computerclub.presentation.booking.model.ClubSeatLayout
import com.example.computerclub.presentation.booking.model.toUiModel
import com.example.computerclub.presentation.home.utils.BookingTimeUtils
import com.example.computerclub.presentation.home.utils.withBookingPeriod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ComputerClubUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val allSeats: List<Seat> = emptyList(),
    val seatLayouts: List<ClubSeatLayout> = emptyList(),
    val isSeatLayoutsLoading: Boolean = false,
    val seatLayoutsError: String? = null,
    val seats: List<Seat> = emptyList(),
    val bookings: List<Booking> = emptyList(),
    val clubs: List<ComputerClubBranch> = emptyList(),
    val selectedClubId: Int? = null,
    val seatTypes: List<SeatType> = emptyList(),
    val selectedSeatTypeId: Int? = null,
    val selectedTab: HomeTab = HomeTab.Main,
    val selectedSeats: List<Seat> = emptyList(),
    val startTime: String = BookingTimeUtils.defaultStartTime(),
    val endTime: String = BookingTimeUtils.defaultEndTime(),
    val selectedDurationHours: Int = 2,
    val message: String? = null
)

enum class HomeTab { Main, Seats, Bookings, Profile }

class HomeViewModel(
    private val loadClubsUseCase: LoadClubsUseCase,
    private val loadSeatTypesUseCase: LoadSeatTypesUseCase,
    private val loadSeatsUseCase: LoadSeatsUseCase,
    private val loadAllSeatsUseCase: LoadAllSeatsUseCase,
    private val loadSeatLayoutsUseCase: LoadSeatLayoutsUseCase,
    private val createBookingUseCase: CreateBookingUseCase,
    private val loadMyBookingsUseCase: LoadMyBookingsUseCase,
    private val cancelBookingUseCase: CancelBookingUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(ComputerClubUiState())
    val state: StateFlow<ComputerClubUiState> = _state

    fun onUserChanged(user: User?) {
        if (user == null) {
            _state.value = ComputerClubUiState(message = "Вы вышли из аккаунта")
            return
        }
        if (state.value.user?.id == user.id) return
        _state.update { it.copy(user = user, message = "Вы вошли как ${user.name}") }
        loadClubs()
        loadSeatTypes()
        loadSeatLayouts()
        loadBookings()
    }

    fun selectClub(clubId: Int) {
        _state.update { it.copy(selectedClubId = clubId, seats = emptyList()) }
        loadSeats()
    }

    fun selectSeatType(typeId: Int) {
        _state.update { it.copy(selectedSeatTypeId = typeId, seats = emptyList()) }
        loadSeats()
    }

    fun selectBookingPeriod(dateOffset: Int, startMinuteOfDay: Int, durationHours: Int) {
        _state.update { it.withBookingPeriod(dateOffset, startMinuteOfDay, durationHours) }
    }

    fun selectTab(tab: HomeTab) {
        _state.update { it.copy(selectedTab = tab) }
        if (tab == HomeTab.Bookings) loadBookings()
    }

    fun selectSeats(seats: List<Seat>) {
        _state.update { it.copy(selectedSeats = seats, message = null) }
    }

    fun clearSelectedSeats() {
        _state.update { it.copy(selectedSeats = emptyList(), message = null) }
    }

    fun loadClubs() {
        launchRequest {
            loadClubsUseCase()
                .onSuccess { clubs ->
                    val singleClub = clubs.take(1)
                    _state.update { it.copy(clubs = singleClub, selectedClubId = singleClub.firstOrNull()?.id) }
                    loadAllSeats()
                    loadSeats()
                }
                .onFailure { showError(it) }
        }
    }

    fun loadSeatTypes() {
        launchRequest {
            loadSeatTypesUseCase()
                .onSuccess { types ->
                    _state.update {
                        val selectedTypeStillVisible = types.any { type -> type.id == it.selectedSeatTypeId }
                        it.copy(
                            seatTypes = types,
                            selectedSeatTypeId = if (selectedTypeStillVisible) it.selectedSeatTypeId else types.firstOrNull()?.id
                        )
                    }
                    loadAllSeats()
                    loadSeats()
                }
                .onFailure { showError(it) }
        }
    }

    fun loadSeats() {
        val clubId = state.value.selectedClubId ?: return
        val typeId = state.value.selectedSeatTypeId ?: return
        launchRequest {
            loadSeatsUseCase(clubId, typeId)
                .onSuccess { seats -> _state.update { it.copy(seats = seats) } }
                .onFailure { showError(it) }
        }
    }

    fun loadAllSeats() {
        val clubId = state.value.selectedClubId ?: return
        launchRequest {
            loadAllSeatsUseCase(clubId)
                .onSuccess { seats -> _state.update { it.copy(allSeats = seats) } }
                .onFailure { showError(it) }
        }
    }

    fun loadSeatLayouts() {
        viewModelScope.launch {
            _state.update { it.copy(isSeatLayoutsLoading = true, seatLayoutsError = null) }
            loadSeatLayoutsUseCase()
                .onSuccess { layouts ->
                    _state.update {
                        it.copy(
                            seatLayouts = layouts.map { layout -> layout.toUiModel() },
                            isSeatLayoutsLoading = false,
                            seatLayoutsError = null
                        )
                    }
                }
                .onFailure {
                    _state.update { current ->
                        current.copy(
                            isSeatLayoutsLoading = false,
                            seatLayoutsError = "Не удалось загрузить схему клуба"
                        )
                    }
                }
        }
    }

    fun loadBookings() {
        launchRequest {
            loadMyBookingsUseCase()
                .onSuccess { bookings -> _state.update { it.copy(bookings = bookings) } }
                .onFailure { showError(it) }
        }
    }

    fun createBooking() {
        val selectedSeats = state.value.selectedSeats
        if (selectedSeats.isEmpty()) return
        launchRequest {
            var createdCount = 0
            selectedSeats.forEach { seat ->
                createBookingUseCase(seat.id, state.value.startTime, state.value.endTime)
                    .onSuccess { createdCount++ }
                    .onFailure {
                        showError(it)
                        return@launchRequest
                    }
            }
            _state.update {
                it.copy(
                    selectedSeats = emptyList(),
                    selectedTab = HomeTab.Main,
                    message = if (createdCount == 1) "Бронь создана" else "Создано броней: $createdCount"
                )
            }
            loadAllSeats()
            loadSeats()
            loadBookings()
        }
    }

    fun cancelBooking(bookingId: Int) {
        launchRequest {
            cancelBookingUseCase(bookingId)
                .onSuccess {
                    _state.update { it.copy(message = "Бронь отменена") }
                    loadBookings()
                }
                .onFailure { showError(it) }
        }
    }

    private fun launchRequest(block: suspend () -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, message = null) }
            block()
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun showError(error: Throwable) {
        _state.update { it.copy(message = error.message ?: "Ошибка") }
    }

    class Factory(
        private val loadClubsUseCase: LoadClubsUseCase,
        private val loadSeatTypesUseCase: LoadSeatTypesUseCase,
        private val loadSeatsUseCase: LoadSeatsUseCase,
        private val loadAllSeatsUseCase: LoadAllSeatsUseCase,
        private val loadSeatLayoutsUseCase: LoadSeatLayoutsUseCase,
        private val createBookingUseCase: CreateBookingUseCase,
        private val loadMyBookingsUseCase: LoadMyBookingsUseCase,
        private val cancelBookingUseCase: CancelBookingUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            HomeViewModel(
                loadClubsUseCase,
                loadSeatTypesUseCase,
                loadSeatsUseCase,
                loadAllSeatsUseCase,
                loadSeatLayoutsUseCase,
                createBookingUseCase,
                loadMyBookingsUseCase,
                cancelBookingUseCase
            ) as T
    }
}
