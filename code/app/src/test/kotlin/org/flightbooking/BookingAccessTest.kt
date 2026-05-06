package org.flightbooking

import access.BookingAccess
import access.UserAccess
import database.DBFactory
import kotlin.test.*
import java.sql.DriverManager

class BookingAccessTest {

    private val testEmail = "booking_tester@leeds.ac.uk"

    @BeforeTest
    fun setup() {
        DBFactory.init()
        teardown()
    }

    @AfterTest
    fun teardown() {
        // Clean up the temporary user we create for testing
        val conn = DriverManager.getConnection(dbUrl())
        val stmt = conn.createStatement()
        stmt.execute("DELETE FROM Users WHERE email = '$testEmail'")
        stmt.close()
        conn.close()
    }

    @Test
    fun `test createBooking returns FLIGHT_NOT_FOUND for invalid flight ID`() {
        // 1. Create a valid user so it passes the first security check
        UserAccess().createUser("Booking Tester", testEmail, "pass123", "user")
        val validSession = UserSession("Booking Tester", testEmail)
        
        val bookingAccess = BookingAccess()
        
        // 2. Try to book a flight ID that definitely doesn't exist (-999)
        val result = bookingAccess.createBooking(validSession, -999, listOf("1A", "1B"), 500.0)
        
        // 3. Verify it caught the bad flight without crashing
        assertEquals("FLIGHT_NOT_FOUND", result, "Should reject booking for a non-existent flight")
    }

    @Test
    fun `test createBooking returns USER_NOT_FOUND for invalid session`() {
        val bookingAccess = BookingAccess()
        val fakeSession = UserSession("Ghost", "nobody@nowhere.com")
        
        // Simulating a request from a session that isn't logged in properly
        val result = bookingAccess.createBooking(fakeSession, 1, listOf("1A"), 100.0)
        
        assertEquals("USER_NOT_FOUND", result, "Should reject booking for an invalid user session")
    }

    @Test
    fun `test MyBookings returns empty list for user with no history`() {
        val bookingAccess = BookingAccess()
        
        // Testing an empty state to ensure the inner/left joins don't break
        val results = bookingAccess.MyBookings("brandnewuser@leeds.ac.uk")
        
        assertTrue(results.isEmpty(), "A non-existent or brand new user should have 0 bookings")
    }
}