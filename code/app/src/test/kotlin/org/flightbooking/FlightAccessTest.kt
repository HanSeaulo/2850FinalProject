package org.flightbooking

import access.FlightAccess
import database.DBFactory
import kotlin.test.*

class FlightAccessTest {

    @BeforeTest
    fun setup() {
        // Connect to the database
        DBFactory.init()
    }

    @Test
    fun `test getAll returns a list of flights`() {
        val flightAccess = FlightAccess()
        val flights = flightAccess.getAll()
        
        // Ensures the function successfully connects and pulls a list without crashing
        assertNotNull(flights, "getAll should return a list, not null")
    }

    @Test
    fun `test getFlightPrice handles non-existent flights gracefully`() {
        val flightAccess = FlightAccess()
        
        // Testing an edge case: requesting a flight ID that definitely doesn't exist
        val invalidPrice = flightAccess.getFlightPrice(-999)
        assertNull(invalidPrice, "Should return null for a flight ID that doesn't exist")
    }

    @Test
    fun `test searchFlights with invalid route returns empty list`() {
        val flightAccess = FlightAccess()
        
        // Testing how the system reacts to finding no matches (empty inputs/results)
        val results = flightAccess.searchFlights("FakeAirport1", "FakeAirport2")
        assertTrue(results.isEmpty(), "Should return an empty list for routes that don't exist")
    }
}