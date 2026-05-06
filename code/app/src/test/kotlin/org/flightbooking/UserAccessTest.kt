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
        // Connects to the database exactly how your app does
        DBFactory.init()
    }

    @AfterTest
    fun teardown() {
        // This instantly deletes our test users so our real database stays perfectly clean
        // We use standard Java SQL to completely bypass the Gradle Exposed library errors.
        val conn = DriverManager.getConnection(dbUrl())
        val stmt = conn.createStatement()
        stmt.execute("DELETE FROM Users WHERE email IN ('$testEmail1', '$testEmail2')")
        stmt.close()
        conn.close()
    }

    @Test
    fun `test createUser inserts user and checkEmail prevents duplicates`() {
        val userAccess = UserAccess()
        
        // 1. Test normal creation
        val result = userAccess.createUser("Test Hanan", testEmail1, "securepass123", "user")
        assertTrue(result, "First user creation should succeed")

        // 2. Test duplicate email security check
        val duplicateResult = userAccess.createUser("Hanan Clone", testEmail1, "differentpassword", "user")
        assertFalse(duplicateResult, "Should return false when trying to register an existing email")
    }

    @Test
    fun `test checkLogin verifies correct credentials and rejects invalid ones`() {
        val userAccess = UserAccess()
        userAccess.createUser("Test Laman", testEmail2, "mypassword", "admin")

        // 1. Test valid login
        assertTrue(userAccess.checkLogin(testEmail2, "mypassword"), "Should return true for correct credentials")
        
        // 2. Test wrong password
        assertFalse(userAccess.checkLogin(testEmail2, "wrongpassword"), "Should reject wrong password")
        
        // 3. Test non-existent email
        assertFalse(userAccess.checkLogin("nobody@leeds.ac.uk", "mypassword"), "Should reject non-existent user")
    }
}