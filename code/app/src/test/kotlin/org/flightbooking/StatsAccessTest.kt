package org.flightbooking

import access.StatsAccess
import database.DBFactory
import kotlin.test.*

class StatsAccessTest {

    @BeforeTest
    fun setup() {
        // Connect to the database, no teardown needed since we aren't writing data!
        DBFactory.init()
    }

    @Test
    fun `test TodaysBookings executes safely and returns a valid integer`() {
        val statsAccess = StatsAccess()
        val count = statsAccess.TodaysBookings()
        assertTrue(count >= 0, "Today's bookings count should be 0 or greater")
    }

    @Test
    fun `test ActiveFlights executes safely and returns a valid integer`() {
        val statsAccess = StatsAccess()
        val count = statsAccess.ActiveFlights()
        assertTrue(count >= 0, "Active flights count should be 0 or greater")
    }

    @Test
    fun `test RegisteredUsers executes safely and returns a valid integer`() {
        val statsAccess = StatsAccess()
        val count = statsAccess.RegisteredUsers()
        assertTrue(count >= 0, "Registered users count should be 0 or greater")
    }

    @Test
    fun `test TotalFlights executes safely and returns a valid integer`() {
        val statsAccess = StatsAccess()
        val count = statsAccess.TotalFlights()
        assertTrue(count >= 0, "Total flights count should be 0 or greater")
    }

    @Test
    fun `test OpenRequests executes safely and returns a valid integer`() {
        val statsAccess = StatsAccess()
        val count = statsAccess.OpenRequests()
        assertTrue(count >= 0, "Open requests count should be 0 or greater")
    }

    @Test
    fun `test RecentActivity returns a list with a maximum of 10 items`() {
        val statsAccess = StatsAccess()
        val activityList = statsAccess.RecentActivity()
        
        assertNotNull(activityList, "Recent activity should return a list (even if empty), not null")
        assertTrue(activityList.size <= 10, "Recent activity should never exceed 10 items")
    }
}