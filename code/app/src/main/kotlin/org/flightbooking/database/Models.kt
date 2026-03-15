package models 

import kotlinx.datetime.LocalDateTime


data class Reports(
    val id: Int = 0, 
    val generatedAt: LocalDateTime, 
    val reportType: String
)

data class Users(
    val id: Int = 0,
    val name: String,
    val email: String,
    val role: String, 
    val password: String,
    val createdAt: LocalDateTime
)

data class Flights(
    val id: Int = 0,
    val flightNumber: String,
    val departureAirport: String, 
    val arrivalAirport: String,
    val departureTime: LocalDateTime,
    val arrivalTime: LocalDateTime,
    val price: Double, 
    val totalSeats: Int, 
    val availableSeats: Int,
    val totalSeatsEconomy: Int,
    val availableSeatsEconomy: Int,
    val totalSeatsBusiness: Int,
    val availableSeatsBusiness: Int,
    val createdAt: LocalDateTime
)

data class Bookings(
    val id: Int = 0,
    val userId: Int,
    val flightId: Int,
    val status: String,
    val totalPrice: Double, 
    val createdAt: LocalDateTime
)

data class Passengers(
    val id: Int = 0,
    val bookingId: Int, 
    val firstName: String,
    val lastName: String, 
    val email: String
)

data class Requests(
    val id: Int = 0,
    val bookingId: Int,
    val type: String,
    val status: String,
    val createdAt: LocalDateTime,
    val processedAt: LocalDateTime
)