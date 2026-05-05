package access

import models.Flights
import models.Users
import tables.FlightsTable
import tables.UsersTable
import tables.BookingsTable
import tables.RequestsTable
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.core.*
import kotlinx.datetime.*
import org.jetbrains.exposed.v1.core.ResultRow
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.LocalDateTime

class StatsAccess {

    //pass negative numbers into the function to query from however many days ago, e.g TodaysBookings(-1) will return from yesterdays date
    @OptIn(ExperimentalTime::class)
    fun TodaysBookings(offset: Int = 0): Int = transaction {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val date = LocalDate(now.year, now.month, now.day).plus(offset, DateTimeUnit.DAY)
        val startOfDay = LocalDateTime(date.year, date.month, date.day, 0, 0, 0)
        val endOfDay = LocalDateTime(date.year, date.month, date.day, 23, 59, 59)

        BookingsTable.selectAll().where {
            (BookingsTable.createdAt greaterEq startOfDay) and
            (BookingsTable.createdAt less endOfDay)
        }.count().toInt()
    }

    @OptIn(ExperimentalTime::class)
    fun ActiveFlights(): Int = transaction {
        val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        FlightsTable.selectAll().where {
            (FlightsTable.departureTime less currentTime) and
            (FlightsTable.arrivalTime greater currentTime)
        }.count().toInt()
    }

    fun RegisteredUsers(): Int = transaction {
        UsersTable.selectAll().count().toInt()
    }

    fun TotalFlights(): Int = transaction {
        FlightsTable.selectAll().count().toInt()
    }

    fun OpenRequests(): Int = transaction {
        RequestsTable.selectAll().where {
            RequestsTable.status neq "approved"
        }.count().toInt()
    }

    fun RecentActivity(): List<Map<String, String>> = transaction {
        val bookings = BookingsTable.selectAll().map {
            mapOf(
                "time" to it[BookingsTable.createdAt].toString(),
                "action" to "Booking created",
                "details" to "Booking #${it[BookingsTable.id]} • flight ${it[BookingsTable.flightId]}",
                "status" to it[BookingsTable.status]
            )
        }

        val users = UsersTable.selectAll().map {
            mapOf(
                "time" to it[UsersTable.createdAt].toString(),
                "action" to "User registered",
                "details" to it[UsersTable.email],
                "status" to "OK"
            )
        }

        val requests = RequestsTable.selectAll().map {
            mapOf(
                "time" to it[RequestsTable.createdAt].toString(),
                "action" to "Request submitted",
                "details" to "${it[RequestsTable.type]} for booking #${it[RequestsTable.bookingId]}",
                "status" to it[RequestsTable.status]
            )
        }

        (bookings + users + requests)
            .sortedByDescending{it["time"]}
            .take(10)
    }
}