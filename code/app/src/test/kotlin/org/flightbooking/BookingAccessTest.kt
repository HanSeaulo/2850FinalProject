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
        // clean up test user
        val conn = DriverManager.getConnection(dbUrl())
        val stmt = conn.createStatement()
        stmt.execute("DELETE FROM Users WHERE email = '$testEmail'")
        stmt.close()
        conn.close()
    }

    @Test
    fun `test createBooking returns FLIGHT_NOT_FOUND for invalid flight ID`() {
        // setup valid user to pass auth
        UserAccess().createUser("Booking Tester", testEmail, "pass123", "user")
        val validSession = UserSession("Booking Tester", testEmail)
        
        val bookingAccess = BookingAccess()
        
        // try to book a fake flight
        val result = bookingAccess.createBooking(validSession, -999, listOf("1A", "1B"), 500.0)
        
        // make sure it throws the right error
        assertEquals("FLIGHT_NOT_FOUND", result, "Should reject booking for a non-existent flight")
    }

    @Test
    fun `test createBooking returns USER_NOT_FOUND for invalid session`() {
        val bookingAccess = BookingAccess()
        val fakeSession = UserSession("Ghost", "nobody@nowhere.com")
        
        // test with invalid session
        val result = bookingAccess.createBooking(fakeSession, 1, listOf("1A"), 100.0)
        
        assertEquals("USER_NOT_FOUND", result, "Should reject booking for an invalid user session")
    }

    @Test
    fun `test MyBookings returns empty list for user with no history`() {
        val bookingAccess = BookingAccess()
        
        // check brand new user state
        val results = bookingAccess.MyBookings("brandnewuser@leeds.ac.uk")
        
        assertTrue(results.isEmpty(), "A non-existent or brand new user should have 0 bookings")
    }

    @Test
    fun `test createBooking handles various inputs without crashing`() {
        UserAccess().createUser("Edge Case", "edge@leeds.ac.uk", "pass123", "user")
        val session = UserSession("Edge Case", "edge@leeds.ac.uk")
        val bookingAccess = BookingAccess()

        // We just care that the app doesn't crash when given weird data
        val result = bookingAccess.createBooking(session, 1, listOf("1A"), -50.0)
        assertNotNull(result, "The app should handle the request and return a status string")
    }
}