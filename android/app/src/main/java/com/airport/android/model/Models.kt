package com.airport.android.model

/**
 * Flight model - matches backend Flight entity
 */
data class Flight(
    val id: Long,
    val flightNumber: String,
    val origin: String,
    val destination: String,
    val departureTime: String,
    val capacity: Int,
    val availableSeats: Int,
    val basePrice: Double
) {
    fun hasAvailableSeats(): Boolean = availableSeats > 0
    fun getFormattedPrice(): String = "$${String.format("%.2f", basePrice)}"
    fun getRoute(): String = "$origin → $destination"
}

/**
 * User model
 */
data class User(
    val id: Long,
    val name: String,
    val email: String,
    val phoneNumber: String?,
    val customerType: String,
    val membershipLevel: String,
    val milesFlown: Int,
    val discountPercent: Int
) {
    fun isFrequentFlyer(): Boolean = customerType == "FREQUENT_FLYER"
    fun getDiscountText(): String = if (discountPercent > 0) "$discountPercent% off" else "No discount"
}

/**
 * Booking model
 */
data class Booking(
    val id: Long,
    val bookingReference: String,
    val flightNumber: String,
    val origin: String,
    val destination: String,
    val departureTime: String,
    val passengerName: String,
    val seatNumber: String,
    val totalPrice: Double,
    val discountAmount: Double,
    val status: String,
    val bookingDate: String
) {
    fun getFormattedPrice(): String = "$${String.format("%.2f", totalPrice)}"
    fun getRoute(): String = "$origin → $destination"
    fun isActive(): Boolean = status == "CONFIRMED"
    fun isCancelled(): Boolean = status == "CANCELLED"
}

// Request DTOs
data class LoginRequest(val email: String, val password: String)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val phoneNumber: String?
)

data class BookingRequest(
    val flightId: Long,
    val passengerFirstName: String,
    val passengerLastName: String,
    val passengerAge: Int,
    val seatPreference: String,
    val seatNumber: String
)

// Response DTOs
data class LoginResponse(val token: String, val user: User)
data class ErrorResponse(val message: String)
