package access

import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.core.*
import tables.SeatsTable

class SeatAccess {

    fun bookSeat(flightId: Int, seatNumber: String): Boolean = transaction {
        val existing = SeatsTable.selectAll().where {
            (SeatsTable.flightId eq flightId) and
            (SeatsTable.seatNumber eq seatNumber)
        }.singleOrNull()

        if (existing != null && existing[SeatsTable.isBooked]) {
            return@transaction false
        }

        if (existing == null) {
            SeatsTable.insert {
                it[SeatsTable.flightId] = flightId
                it[SeatsTable.seatNumber] = seatNumber
                it[SeatsTable.isBooked] = true
            }
        } else {
            SeatsTable.update({
                (SeatsTable.flightId eq flightId) and
                (SeatsTable.seatNumber eq seatNumber)
            }) {
                it[SeatsTable.isBooked] = true
            }
        }

        true
    }

    fun getBookedSeats(flightId: Int): List<String> = transaction {
        SeatsTable.selectAll().where {
            (SeatsTable.flightId eq flightId) and
            (SeatsTable.isBooked eq true)
        }.map {
            it[SeatsTable.seatNumber]
        }
    }
}