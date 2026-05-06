package access

import models.Flights
import models.Users
import tables.FlightsTable
import tables.UsersTable
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.core.*
import kotlinx.datetime.*
import org.jetbrains.exposed.v1.core.ResultRow

class UserAccess {

    fun createUser(name: String, email: String, password: String, role: String): Boolean {
        if (checkEmail(email)) return false

        if (password.isBlank() || !email.contains("@")) return false

        transaction {
            UsersTable.insert {
                it[UsersTable.name] = name
                it[UsersTable.email] = email
                it[UsersTable.password] = password
                it[UsersTable.role] = role
                it[UsersTable.createdAt] = Clock.System.now().toLocalDateTime(TimeZone.UTC)
            }
        }
        return true
    }

    fun checkEmail(email: String): Boolean = transaction {
        UsersTable.selectAll()
            .where { UsersTable.email eq email }
            .any()
    }

    fun checkLogin(email: String, password: String): Boolean = transaction {
        UsersTable.selectAll()
            .where {
                (UsersTable.email eq email) and
                (UsersTable.password eq password)
            }
            .limit(1)
            .empty()
            .not()
    }

    fun getUserByEmail(email: String): Users? = transaction {
        UsersTable.selectAll()
            .where { UsersTable.email eq email }
            .map {
                Users(
                    id = it[UsersTable.id],
                    name = it[UsersTable.name],
                    email = it[UsersTable.email],
                    role = it[UsersTable.role],
                    password = it[UsersTable.password],
                    createdAt = it[UsersTable.createdAt]
                )
            }
            .singleOrNull()
    }
}

class FlightAccess {

    fun getFlightById(id: Int): Flights? = transaction {
        FlightsTable.selectAll()
            .where { FlightsTable.id eq id }
            .map { constructFlight(it) }
            .singleOrNull()
    }

    fun getAll(): List<Flights> = transaction {
        FlightsTable.selectAll().map { constructFlight(it) }
    }

    fun getAirportCodes(): List<String> = transaction {
        val airports = mutableListOf<String>()

        FlightsTable.selectAll().forEach {
            airports.add(it[FlightsTable.departureAirport])
            airports.add(it[FlightsTable.arrivalAirport])
        }

        airports.distinct().sorted()
    }

    fun searchFlights(from: String, to: String): List<Flights> = transaction {
        FlightsTable.selectAll()
            .where {
                (FlightsTable.departureAirport eq from) and
                (FlightsTable.arrivalAirport eq to)
            }
            .map { constructFlight(it) }
    }

    fun searchFlights(
        from: String,
        to: String,
        departTime: LocalDateTime,
        passengers: Int,
        cabinClass: String
    ): List<Flights>? = transaction {
        FlightsTable.selectAll()
            .where {
                (FlightsTable.departureAirport eq from) and
                (FlightsTable.arrivalAirport eq to) and
                (FlightsTable.departureTime greaterEq departTime) and
                (FlightsTable.availableSeats greaterEq passengers)
            }
            .map { constructFlight(it) }
    }

    fun getFlightPrice(flightId: Int): Pair<Int, Double>? = transaction {
        FlightsTable.select(FlightsTable.id, FlightsTable.price)
            .where { FlightsTable.id eq flightId }
            .map { Pair(it[FlightsTable.id], it[FlightsTable.price].toDouble()) }
            .singleOrNull()
}

    private fun constructFlight(row: ResultRow): Flights {
        return Flights(
            id = row[FlightsTable.id],
            flightNumber = row[FlightsTable.flightNumber],
            departureAirport = row[FlightsTable.departureAirport],
            arrivalAirport = row[FlightsTable.arrivalAirport],
            departureTime = row[FlightsTable.departureTime],
            arrivalTime = row[FlightsTable.arrivalTime],
            price = row[FlightsTable.price].toDouble(),
            totalSeats = row[FlightsTable.totalSeats],
            availableSeats = row[FlightsTable.availableSeats],
            createdAt = row[FlightsTable.createdAt]
        )
    }
}