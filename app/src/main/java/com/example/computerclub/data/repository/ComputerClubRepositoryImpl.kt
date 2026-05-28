package com.example.computerclub.data.repository

import com.example.computerclub.data.local.TokenStorage
import com.example.computerclub.data.model.CreateBookingRequestDto
import com.example.computerclub.data.model.LoginRequestDto
import com.example.computerclub.data.model.RefreshTokenRequestDto
import com.example.computerclub.data.model.RegisterRequestDto
import com.example.computerclub.data.model.UpdateSeatNameRequestDto
import com.example.computerclub.data.model.UpdateSeatStatusRequestDto
import com.example.computerclub.data.model.toDomain
import com.example.computerclub.data.remote.ApiErrorParser
import com.example.computerclub.data.remote.ComputerClubApi
import com.example.computerclub.domain.model.Booking
import com.example.computerclub.domain.model.ComputerClubBranch
import com.example.computerclub.domain.model.Seat
import com.example.computerclub.domain.model.SeatLayout
import com.example.computerclub.domain.model.SeatType
import com.example.computerclub.domain.model.User
import com.example.computerclub.domain.repository.ComputerClubRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import retrofit2.Response

class ComputerClubRepositoryImpl(
    private val api: ComputerClubApi,
    private val tokenStorage: TokenStorage
) : ComputerClubRepository {
    override val token: Flow<String?> = tokenStorage.token

    override suspend fun login(email: String, password: String): Result<User> = safeCall {
        val response = api.login(LoginRequestDto(email.trim(), password))
        val body = response.requireBody()
        tokenStorage.saveAuthResponse(body)
        body.user.toDomain()
    }

    override suspend fun register(email: String, password: String, name: String): Result<User> = safeCall {
        val response = api.register(RegisterRequestDto(email.trim(), password, name.trim()))
        val body = response.requireBody()
        tokenStorage.saveAuthResponse(body)
        body.user.toDomain()
    }

    override suspend fun logout() {
        tokenStorage.clearToken()
    }

    override suspend fun loadProfile(): Result<User> = safeCall {
        authorizedBody { token -> api.profile(token) }.toDomain()
    }

    override suspend fun loadClubs(): Result<List<ComputerClubBranch>> = safeCall {
        authorizedBody { token -> api.clubs(token) }.map { it.toDomain() }
    }

    override suspend fun loadSeatTypes(): Result<List<SeatType>> = safeCall {
        authorizedBody { token -> api.seatTypes(token) }.map { it.toDomain() }
    }

    override suspend fun loadSeats(clubId: Int, typeId: Int): Result<List<Seat>> = safeCall {
        authorizedBody { token -> api.seats(token, clubId, typeId) }.map { it.toDomain() }
    }

    override suspend fun loadAllSeats(clubId: Int): Result<List<Seat>> = safeCall {
        authorizedBody { token -> api.seats(token, clubId) }.map { it.toDomain() }
    }

    override suspend fun getSeatLayouts(): Result<List<SeatLayout>> = safeCall {
        api.seatLayouts().requireBody().map { it.toDomain() }
    }

    override suspend fun createBooking(
        seatId: Int,
        startTime: String,
        endTime: String
    ): Result<Booking> = safeCall {
        authorizedBody { token ->
            api.createBooking(
                token = token,
                request = CreateBookingRequestDto(seatId, startTime, endTime)
            )
        }.toDomain()
    }

    override suspend fun loadMyBookings(): Result<List<Booking>> = safeCall {
        authorizedBody { token -> api.myBookings(token) }.map { it.toDomain() }
    }

    override suspend fun cancelBooking(bookingId: Int): Result<Unit> = safeCall {
        val response = authorizedResponse { token -> api.cancelBooking(token, bookingId) }
        if (!response.isSuccessful) error(ApiErrorParser.message(response))
    }

    override suspend fun getAllBookings(): Result<List<Booking>> = safeCall {
        authorizedBody { token -> api.getAllBookings(token) }.map { it.toDomain() }
    }

    override suspend fun adminCancelBooking(bookingId: Int): Result<Unit> = safeCall {
        val response = authorizedResponse { token -> api.adminCancelBooking(token, bookingId) }
        if (!response.isSuccessful) error(ApiErrorParser.message(response))
    }

    override suspend fun deactivateSeat(seatId: Int): Result<Unit> = safeCall {
        val response = authorizedResponse { token -> api.deactivateSeat(token, seatId) }
        if (!response.isSuccessful) error(ApiErrorParser.message(response))
    }

    override suspend fun updateSeatName(seatId: Int, name: String): Result<Seat> = safeCall {
        authorizedBody { token ->
            api.updateSeatName(
                token = token,
                seatId = seatId,
                request = UpdateSeatNameRequestDto(name.trim())
            )
        }.toDomain()
    }

    override suspend fun updateSeatStatus(seatId: Int, isActive: Boolean): Result<Seat> = safeCall {
        authorizedBody { token ->
            api.updateSeatStatus(
                token = token,
                seatId = seatId,
                request = UpdateSeatStatusRequestDto(isActive)
            )
        }.toDomain()
    }

    private suspend fun authHeader(): String {
        val token = tokenStorage.token.firstOrNull() ?: refreshAccessToken()
        return "Bearer ${token ?: error("Нужно войти в аккаунт")}"
    }

    private suspend fun <T> authorizedBody(block: suspend (String) -> Response<T>): T =
        authorizedResponse(block).requireBody()

    private suspend fun <T> authorizedResponse(block: suspend (String) -> Response<T>): Response<T> {
        val response = block(authHeader())
        if (response.code() != 401) return response

        val refreshedToken = refreshAccessToken() ?: return response
        return block("Bearer $refreshedToken")
    }

    private suspend fun refreshAccessToken(): String? {
        val refreshToken = tokenStorage.refreshToken.firstOrNull() ?: return null
        val response = api.refresh(RefreshTokenRequestDto(refreshToken))
        if (!response.isSuccessful) {
            tokenStorage.clearToken()
            return null
        }

        val body = response.body() ?: run {
            tokenStorage.clearToken()
            return null
        }
        tokenStorage.saveAuthResponse(body)
        return body.accessToken ?: body.token
    }

    private suspend fun TokenStorage.saveAuthResponse(response: com.example.computerclub.data.model.AuthResponseDto) {
        val accessToken = response.accessToken ?: response.token ?: error("Сервер не вернул access token")
        val refreshToken = response.refreshToken ?: error("Сервер не вернул refresh token")
        saveTokens(accessToken = accessToken, refreshToken = refreshToken)
    }

    private suspend inline fun <T> safeCall(crossinline block: suspend () -> T): Result<T> =
        try {
            Result.success(block())
        } catch (error: Throwable) {
            Result.failure(Exception(error.message ?: "Не удалось выполнить запрос"))
        }

    private fun <T> Response<T>.requireBody(): T {
        if (!isSuccessful) error(ApiErrorParser.message(this))
        return body() ?: error("Пустой ответ сервера")
    }
}
