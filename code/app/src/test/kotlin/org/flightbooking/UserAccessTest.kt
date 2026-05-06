package org.flightbooking

import access.UserAccess
import database.DBFactory
import kotlin.test.*
import java.sql.DriverManager

class UserAccessTest {

    private val testEmail1 = "test_hanan@leeds.ac.uk"
    private val testEmail2 = "test_laman@leeds.ac.uk"

    @BeforeTest
    fun setup() {
        // init db connection
        DBFactory.init()
        teardown()
    }

    @AfterTest
    fun teardown() {
        // clean up test users via raw SQL
        // added the edge case emails to the list to keep the db clean
        val conn = DriverManager.getConnection(dbUrl())
        val stmt = conn.createStatement()
        stmt.execute("DELETE FROM Users WHERE email IN ('$testEmail1', '$testEmail2', 'nopass@leeds.ac.uk', 'not-an-email')")
        stmt.close()
        conn.close()
    }

    @Test
    fun `test createUser rejects invalid emails and blank passwords`() {
        val userAccess = UserAccess()
        
        // checking if a blank password gets blocked
        val blankPassResult = userAccess.createUser("No Pass", "nopass@leeds.ac.uk", "   ", "user")
        assertFalse(blankPassResult, "Should reject user creation with a blank password")

        // checking if bad email formats are caught
        val badEmailResult = userAccess.createUser("Bad Email", "not-an-email", "validpass123", "user")
        assertFalse(badEmailResult, "Should reject user creation with an improperly formatted email")
    }

    @Test
    fun `test createUser inserts user and checkEmail prevents duplicates`() {
        val userAccess = UserAccess()
        
        // test initial registration
        val result = userAccess.createUser("Test Hanan", testEmail1, "securepass123", "user")
        assertTrue(result, "First user creation should succeed")

        // verify duplicate email check
        val duplicateResult = userAccess.createUser("Hanan Clone", testEmail1, "differentpassword", "user")
        assertFalse(duplicateResult, "Should return false when trying to register an existing email")
    }

    @Test
    fun `test checkLogin verifies correct credentials and rejects invalid ones`() {
        val userAccess = UserAccess()
        userAccess.createUser("Test Laman", testEmail2, "mypassword", "admin")

        // test valid auth
        assertTrue(userAccess.checkLogin(testEmail2, "mypassword"), "Should return true for correct credentials")
        
        // test invalid password
        assertFalse(userAccess.checkLogin(testEmail2, "wrongpassword"), "Should reject wrong password")
        
        // test missing user
        assertFalse(userAccess.checkLogin("nobody@leeds.ac.uk", "mypassword"), "Should reject non-existent user")
    }
}