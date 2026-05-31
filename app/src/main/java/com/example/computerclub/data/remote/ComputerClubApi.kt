package com.example.computerclub.data.remote

import com.example.computerclub.data.model.AuthResponseDto
import com.example.computerclub.data.model.BookingDto
import com.example.computerclub.data.model.ComputerClubDto
import com.example.computerclub.data.model.CreateBookingRequestDto
import com.example.computerclub.data.model.LoginRequestDto
import com.example.computerclub.data.model.MapObjectDto
import com.example.computerclub.data.model.RefreshTokenRequestDto
import com.example.computerclub.data.model.RegisterRequestDto
import com.example.computerclub.data.model.SeatDto
import com.example.computerclub.data.model.SeatTypeDto
import com.example.computerclub.data.model.UpdateSeatNameRequestDto
import com.example.computerclub.data.model.UpdateSeatStatusRequestDto
import com.example.computerclub.data.model.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ComputerClubApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<AuthResponseDto>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequestDto): Response<AuthResponseDto>

    @POST("auth/refresh")
    suspend fun refresh(@Body request: RefreshTokenRequestDto): Response<AuthResponseDto>

    @GET("profile")
    suspend fun profile(@Header("Authorization") token: String): Response<UserDto>

    @GET("clubs")
    suspend fun clubs(@Header("Authorization") token: String): Response<List<ComputerClubDto>>

    @GET("seats")
    suspend fun seats(
        @Header("Authorization") token: String,
        @Query("clubId") clubId: Int,
        @Query("typeId") typeId: Int? = null
    ): Response<List<SeatDto>>

    @GET("seat-types")
    suspend fun seatTypes(@Header("Authorization") token: String): Response<List<SeatTypeDto>>

    @GET("clubs/{clubId}/map")
    suspend fun clubMap(
        @Header("Authorization") token: String,
        @Path("clubId") clubId: Int
    ): Response<List<MapObjectDto>>

    @POST("bookings")
    suspend fun createBooking(
        @Header("Authorization") token: String,
        @Body request: CreateBookingRequestDto
    ): Response<BookingDto>

    @GET("bookings/my")
    suspend fun myBookings(@Header("Authorization") token: String): Response<List<BookingDto>>

    @DELETE("bookings/{id}")
    suspend fun cancelBooking(
        @Header("Authorization") token: String,
        @Path("id") bookingId: Int
    ): Response<Unit>

    @GET("admin/bookings")
    suspend fun getAllBookings(@Header("Authorization") token: String): Response<List<BookingDto>>

    @DELETE("admin/bookings/{id}")
    suspend fun adminCancelBooking(
        @Header("Authorization") token: String,
        @Path("id") bookingId: Int
    ): Response<Unit>

    @DELETE("admin/seats/{id}")
    suspend fun deactivateSeat(
        @Header("Authorization") token: String,
        @Path("id") seatId: Int
    ): Response<Unit>

    @PATCH("admin/seats/{id}/name")
    suspend fun updateSeatName(
        @Header("Authorization") token: String,
        @Path("id") seatId: Int,
        @Body request: UpdateSeatNameRequestDto
    ): Response<SeatDto>

    @PATCH("admin/seats/{id}/status")
    suspend fun updateSeatStatus(
        @Header("Authorization") token: String,
        @Path("id") seatId: Int,
        @Body request: UpdateSeatStatusRequestDto
    ): Response<SeatDto>
}
