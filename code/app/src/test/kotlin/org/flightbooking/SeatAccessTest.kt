package org.flightbooking

import access.SeatAccess
import database.DBFactory
import kotlin.test.*
import java.sql.DriverManager

class SeatAccessTest {

    // dummy data for testing
    private val testFlightId = -9999
    private val testSeat1 = "TEST-1A"
    private val testSeat2 = "TEST-1B"

    @BeforeTest
    fun setup() {
        DBFactory.init()
        teardown()
    }

    @AfterTest
    fun teardown() {
        // cleanup test data
        val conn = DriverManager.getConnection(dbUrl())
        val stmt = conn.createStatement()
        
        stmt.execute("DELETE FROM Seats WHERE flight_id = $testFlightId")
        
        stmt.close()
        conn.close()
    }

    @Test
    fun `test bookSeat allows booking a new seat and prevents double booking`() {
        val seatAccess = SeatAccess()

        // test initial booking
        val firstBooking = seatAccess.bookSeat(testFlightId, testSeat1)
        assertTrue(firstBooking, "Should return true when booking an empty seat")

        // test double booking prevention
        val doubleBooking = seatAccess.bookSeat(testFlightId, testSeat1)
        assertFalse(doubleBooking, "Should return false when trying to book an already booked seat")
    }

    @Test
    fun `test getBookedSeats returns the correct list of seats`() {
        val seatAccess = SeatAccess()

        // setup mock bookings
        seatAccess.bookSeat(testFlightId, testSeat1)
        seatAccess.bookSeat(testFlightId, testSeat2)

        // fetch results
        val bookedSeats = seatAccess.getBookedSeats(testFlightId)

        // verify counts and data
        assertEquals(2, bookedSeats.size, "Should return exactly 2 booked seats")
        assertTrue(bookedSeats.contains(testSeat1), "List should contain $testSeat1")
        assertTrue(bookedSeats.contains(testSeat2), "List should contain $testSeat2")
    }
}