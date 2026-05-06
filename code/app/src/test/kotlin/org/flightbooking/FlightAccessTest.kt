package org.flightbooking

import access.FlightAccess
import database.DBFactory
import kotlin.test.*

class FlightAccessTest {

    @BeforeTest
    fun setup() {
        // init db connection
        DBFactory.init()
    }

    @Test
    fun `test getAll returns a list of flights`() {
        val flightAccess = FlightAccess()
        val flights = flightAccess.getAll()
        
        // make sure we actually get a list back
        assertNotNull(flights, "getAll should return a list, not null")
    }

    @Test
    fun `test getFlightPrice handles non-existent flights gracefully`() {
        val flightAccess = FlightAccess()
        
        // try to get price for a fake flight
        val invalidPrice = flightAccess.getFlightPrice(-999)
        assertNull(invalidPrice, "Should return null for a flight ID that doesn't exist")
    }

    @Test
    fun `test searchFlights with invalid route returns empty list`() {
        val flightAccess = FlightAccess()
        
        // test searching a route that doesn't exist
        val results = flightAccess.searchFlights("FakeAirport1", "FakeAirport2")
        assertTrue(results.isEmpty(), "Should return an empty list for routes that don't exist")
    }

    @Test
    fun `test searchFlights gracefully handles empty or whitespace inputs`() {
        val flightAccess = FlightAccess()
        
        // check if blank search terms return an empty list instead of crashing
        val blankResults = flightAccess.searchFlights("", "")
        assertTrue(blankResults.isEmpty(), "Should return an empty list when given blank search terms")
        
        // check if search with just spaces is handled
        val spaceResults = flightAccess.searchFlights("   ", "   ")
        assertTrue(spaceResults.isEmpty(), "Should return an empty list when given whitespace search terms")
    }
}