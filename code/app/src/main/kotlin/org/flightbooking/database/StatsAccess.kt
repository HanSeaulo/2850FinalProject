package statsaccess

import models.Flights
import models.Users
import tables.FlightsTable
import tables.UsersTable
import tables.BookingsTable
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

class ManagementDashboard {

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
}