package com.example.computerclub.presentation.app

import android.content.Context
import com.example.computerclub.data.local.TokenStorage
import com.example.computerclub.data.remote.NetworkModule
import com.example.computerclub.data.repository.ComputerClubRepositoryImpl
import com.example.computerclub.domain.repository.ComputerClubRepository
import com.example.computerclub.domain.usecase.AdminCancelBookingUseCase
import com.example.computerclub.domain.usecase.CancelBookingUseCase
import com.example.computerclub.domain.usecase.CreateBookingUseCase
import com.example.computerclub.domain.usecase.DeactivateSeatUseCase
import com.example.computerclub.domain.usecase.GetAllBookingsUseCase
import com.example.computerclub.domain.usecase.LoadAllSeatsUseCase
import com.example.computerclub.domain.usecase.LoadClubMapUseCase
import com.example.computerclub.domain.usecase.LoadClubsUseCase
import com.example.computerclub.domain.usecase.LoadMyBookingsUseCase
import com.example.computerclub.domain.usecase.LoadSeatTypesUseCase
import com.example.computerclub.domain.usecase.LoadSeatsUseCase
import com.example.computerclub.domain.usecase.LoginUseCase
import com.example.computerclub.domain.usecase.LogoutUseCase
import com.example.computerclub.domain.usecase.RegisterUseCase
import com.example.computerclub.domain.usecase.RestoreSessionUseCase
import com.example.computerclub.domain.usecase.UpdateSeatNameUseCase
import com.example.computerclub.domain.usecase.UpdateSeatStatusUseCase

class AppContainer(context: Context) {
    private val repository: ComputerClubRepository = ComputerClubRepositoryImpl(
        api = NetworkModule.createApi(),
        tokenStorage = TokenStorage(context.applicationContext)
    )

    val loginUseCase = LoginUseCase(repository)
    val registerUseCase = RegisterUseCase(repository)
    val logoutUseCase = LogoutUseCase(repository)
    val restoreSessionUseCase = RestoreSessionUseCase(repository)
    val loadClubsUseCase = LoadClubsUseCase(repository)
    val loadSeatTypesUseCase = LoadSeatTypesUseCase(repository)
    val loadSeatsUseCase = LoadSeatsUseCase(repository)
    val loadAllSeatsUseCase = LoadAllSeatsUseCase(repository)
    val loadClubMapUseCase = LoadClubMapUseCase(repository)
    val createBookingUseCase = CreateBookingUseCase(repository)
    val loadMyBookingsUseCase = LoadMyBookingsUseCase(repository)
    val cancelBookingUseCase = CancelBookingUseCase(repository)
    val getAllBookingsUseCase = GetAllBookingsUseCase(repository)
    val adminCancelBookingUseCase = AdminCancelBookingUseCase(repository)
    val deactivateSeatUseCase = DeactivateSeatUseCase(repository)
    val updateSeatNameUseCase = UpdateSeatNameUseCase(repository)
    val updateSeatStatusUseCase = UpdateSeatStatusUseCase(repository)
}
