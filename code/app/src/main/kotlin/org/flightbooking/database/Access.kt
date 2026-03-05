package access

import models.Flights
import tables.FlightsTable
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and

import kotlinx.datetime.LocalDateTime

class FlightAccess {
    
    fun getAll(): List<Flights> = transaction {
        FlightsTable.selectAll().map {
            constructFlight(it)
        }
    }

    fun searchFlights(from: String, to: String): List<Flights> = transaction {
        FlightsTable.selectAll().where { (FlightsTable.departureAirport eq from) and 
        (FlightsTable.arrivalAirport eq to) }.map {
            constructFlight(it)
        }
    }


    fun constructFlight(it: ResultRow): Flights {
        return Flights (
            id = it[FlightsTable.id],
            flightNumber = it[FlightsTable.flightNumber],
            departureAirport = it[FlightsTable.departureAirport],
            arrivalAirport = it[FlightsTable.arrivalAirport],
            departureTime = it[FlightsTable.departureTime],
            arrivalTime = it[FlightsTable.arrivalTime],
            price = it[FlightsTable.price],
            totalSeats = it[FlightsTable.totalSeats],
            availableSeats = it[FlightsTable.availableSeats], 
            createdAt = it[FlightsTable.createdAt] 
        )
    }

}
