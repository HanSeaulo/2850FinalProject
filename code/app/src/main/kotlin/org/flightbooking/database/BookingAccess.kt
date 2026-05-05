package access

import models.Flights
import models.Users
import tables.FlightsTable
import tables.UsersTable
import tables.BookingsTable
import tables.PassengersTable
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.core.*
import kotlinx.datetime.*
import org.jetbrains.exposed.v1.core.ResultRow
//import org.flightbooking.UserSession

class BookingAccess { 

// fun createBooking(session: UserSession, flightId: Int, selectedSeats: List<String>, totalPrice: Double): String = transaction {
//         val userId = UsersTable
//             .select(UsersTable.id)
//             .where { UsersTable.email eq session.email }
//             .singleOrNull()?.get(UsersTable.id)
//             ?: return@transaction "USER_NOT_FOUND"

//         val availableSeats = FlightsTable
//             .select(FlightsTable.availableSeats)
//             .where { FlightsTable.id eq flightId }
//             .singleOrNull()?.get(FlightsTable.availableSeats)
//             ?: return@transaction "FLIGHT_NOT_FOUND"

//         if (availableSeats < selectedSeats.size) return@transaction "NOT_ENOUGH_SEATS"

//         val bookingId = BookingsTable.insertAndGetId {
//             it[BookingsTable.userId] = userId
//             it[BookingsTable.flightId] = flightId
//             it[BookingsTable.status] = "confirmed"
//             it[BookingsTable.totalPrice] = totalPrice.toBigDecimal()
//             it[BookingsTable.createdAt] = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
//         }.value

//         val parts = session.name.trim().split(Regex("\\s+"))
//         val firstName = parts.firstOrNull() ?: "Guest"
//         val lastName = if (parts.size > 1) parts.drop(1).joinToString(" ") else "Passenger"

//         PassengersTable.batchInsert(selectedSeats.mapIndexed { i, _ -> i }) { index ->
//             this[PassengersTable.bookingId] = bookingId
//             this[PassengersTable.firstName] = if (index == 0) firstName else "$firstName ${index + 1}"
//             this[PassengersTable.lastName] = lastName
//             this[PassengersTable.email] = if (index == 0) session.email else session.email.replace("@", "+${index + 1}@")
//         }

//         FlightsTable.update({ FlightsTable.id eq flightId }) {
//             it[FlightsTable.availableSeats] = availableSeats - selectedSeats.size
//         }

//         bookingId.toString()
//     }

}

