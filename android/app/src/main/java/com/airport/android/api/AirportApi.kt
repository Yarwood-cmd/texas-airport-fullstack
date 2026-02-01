package com.airport.android.api

import com.airport.android.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API interface for Texas Airport Reservation System
 */
interface AirportApi {

    // Authentication
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<User>

    // Flights
    @GET("api/flights")
    suspend fun getAllFlights(): Response<List<Flight>>

    @GET("api/flights/available")
    suspend fun getAvailableFlights(): Response<List<Flight>>

    @GET("api/flights/{id}")
    suspend fun getFlightById(@Path("id") id: Long): Response<Flight>

    @GET("api/flights/search/destination/{destination}")
    suspend fun searchByDestination(@Path("destination") destination: String): Response<List<Flight>>

    // Bookings
    @GET("api/bookings")
    suspend fun getMyBookings(): Response<List<Booking>>

    @POST("api/bookings")
    suspend fun createBooking(@Body request: BookingRequest): Response<Booking>

    @DELETE("api/bookings/{id}")
    suspend fun cancelBooking(@Path("id") id: Long): Response<Booking>
}
