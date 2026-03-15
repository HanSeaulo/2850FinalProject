package tables

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.datetime.datetime

const val MAX_STRING_LENGTH = 255

object ReportsTable : Table("Reports") {
    val id = integer("id").autoIncrement()
    val generatedAt = datetime("generated_at")
    val reportType = varchar("report_type", MAX_STRING_LENGTH)

    override val primaryKey = PrimaryKey(id)
}

object UsersTable : Table("Users") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", MAX_STRING_LENGTH)
    val email = varchar("email", MAX_STRING_LENGTH).uniqueIndex()
    val role = varchar("role", MAX_STRING_LENGTH)
    val password = varchar("password", MAX_STRING_LENGTH)
    val createdAt = datetime("created_at")

    override val primaryKey = PrimaryKey(id)
}

object FlightsTable : Table("Flights" ) {
    val id = integer("id").autoIncrement()
    val flightNumber = varchar("flight_number", MAX_STRING_LENGTH)
    val departureAirport = varchar("departure_airport", MAX_STRING_LENGTH)
    val arrivalAirport = varchar("arrival_airport", MAX_STRING_LENGTH)
    val departureTime = datetime("departure_time")
    val arrivalTime = datetime("arrival_time")
    val price = double("price")
    val totalSeats = integer("total_seats")
    val availableSeats = integer("available_seats")
    val totalSeatsEconomy = integer("total_seats_economy")
    val availableSeatsEconomy = integer("available_seats_economy")
    val totalSeatsBusiness = integer("total_seats_business")
    val availableSeatsBusiness = integer("available_seats_business")
    val createdAt = datetime("created_at")

    override val primaryKey = PrimaryKey(id)
}

object BookingsTable : Table("Bookings") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(UsersTable.id)
    val flightId = integer("flight_id").references(FlightsTable.id)
    val status = varchar("status", MAX_STRING_LENGTH)
    val totalPrice = double("total_price")
    val createdAt = datetime("created_at")

    override val primaryKey = PrimaryKey(id)
}

object PassengersTable : Table("Passengers") {
    val id = integer("id").autoIncrement()
    val bookingId = integer("booking_id").references(BookingsTable.id)
    val firstName = varchar("first_name", MAX_STRING_LENGTH)
    val lastName = varchar("last_name", MAX_STRING_LENGTH)
    val email = varchar("email", MAX_STRING_LENGTH).uniqueIndex()

    override val primaryKey = PrimaryKey(id)
}

object RequestsTable : Table("Requests") {
    val id = integer("id").autoIncrement()
    val bookingId = integer("booking_id").references(BookingsTable.id)
    val type = varchar("type", MAX_STRING_LENGTH)
    val status = varchar("status", MAX_STRING_LENGTH)
    val createdAt = datetime("created_at")
    val processedAt = datetime("processed_at")

    override val primaryKey = PrimaryKey(id)
}