package org.flightbooking

import access.SeatAccess
import database.DBFactory
import kotlin.test.*
import java.sql.DriverManager

class SeatAccessTest {

    // We use a fake flight ID and fake seats so we don't accidentally mess up real flight data
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
        // Clean up our fake seats the millisecond the test finishes
        val conn = DriverManager.getConnection(dbUrl())
        val stmt = conn.createStatement()
        
        // Using the exact SQL names from Tables.kt
        stmt.execute("DELETE FROM Seats WHERE flight_id = $testFlightId")
        
        stmt.close()
        conn.close()
    }

    @Test
    fun `test bookSeat allows booking a new seat and prevents double booking`() {
        val seatAccess = SeatAccess()

        // 1. Test successful booking
        val firstBooking = seatAccess.bookSeat(testFlightId, testSeat1)
        assertTrue(firstBooking, "Should return true when booking an empty seat")

        // 2. Test duplicate booking prevention
        val doubleBooking = seatAccess.bookSeat(testFlightId, testSeat1)
        assertFalse(doubleBooking, "Should return false when trying to book an already booked seat")
    }

    @Test
    fun `test getBookedSeats returns the correct list of seats`() {
        val seatAccess = SeatAccess()

        // Book two fake seats
        seatAccess.bookSeat(testFlightId, testSeat1)
        seatAccess.bookSeat(testFlightId, testSeat2)

        // Fetch the list of booked seats for our fake flight
        val bookedSeats = seatAccess.getBookedSeats(testFlightId)

        // Verify the logic returns exactly what we just put in
        assertEquals(2, bookedSeats.size, "Should return exactly 2 booked seats")
        assertTrue(bookedSeats.contains(testSeat1), "List should contain $testSeat1")
        assertTrue(bookedSeats.contains(testSeat2), "List should contain $testSeat2")
    }
}