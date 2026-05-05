package tables

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.datetime
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

const val MAX_STRING_LENGTH = 255

object ReportsTable : Table("Reports") {
    val id = integer("id").autoIncrement()
    val generatedAt = datetime("generated_at")
    val reportType = varchar("report_type", MAX_STRING_LENGTH)

    override val primaryKey = PrimaryKey(id)
}
object SeatsTable : Table("Seats") {
    val id = integer("id").autoIncrement()
    val flightId = integer("flight_id").references(FlightsTable.id)
    val seatNumber = varchar("seat_number", 10)
    val isBooked = bool("is_booked").default(false)

    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex(flightId, seatNumber)
    }
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

object FlightsTable : Table("Flights") {
    val id = integer("id").autoIncrement()
    val flightNumber = varchar("flight_number", MAX_STRING_LENGTH)
    val departureAirport = varchar("departure_airport", MAX_STRING_LENGTH)
    val arrivalAirport = varchar("arrival_airport", MAX_STRING_LENGTH)
    val departureTime = datetime("departure_time")
    val arrivalTime = datetime("arrival_time")
    val price = decimal("price", 10, 2)
    val totalSeats = integer("total_seats")
    val availableSeats = integer("available_seats")
    val createdAt = datetime("created_at")

    override val primaryKey = PrimaryKey(id)
}

object BookingsTable : IntIdTable("Bookings") {
    val userId = integer("user_id").references(UsersTable.id)
    val flightId = integer("flight_id").references(FlightsTable.id)
    val status = varchar("status", MAX_STRING_LENGTH)
    val totalPrice = decimal("total_price", 10, 2)
    val createdAt = datetime("created_at")
}

object PassengersTable : Table("Passengers") {
    val id = integer("id").autoIncrement()
    val bookingId = integer("booking_id").references(BookingsTable.id)
    val firstName = varchar("first_name", MAX_STRING_LENGTH)
    val lastName = varchar("last_name", MAX_STRING_LENGTH)
    val email = varchar("email", MAX_STRING_LENGTH)

    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex(bookingId, email)
    }
}

object RequestsTable : Table("Requests") {
    val id = integer("id").autoIncrement()
    val bookingId = integer("booking_id").references(BookingsTable.id)
    val type = varchar("type", MAX_STRING_LENGTH)
    val status = varchar("status", MAX_STRING_LENGTH)
    val createdAt = datetime("created_at")
    val processedAt = datetime("processed_at").nullable()

    override val primaryKey = PrimaryKey(id)
}