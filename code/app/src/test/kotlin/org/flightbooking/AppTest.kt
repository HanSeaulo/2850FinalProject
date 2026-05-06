package org.flightbooking

import kotlin.test.Test
import kotlin.test.assertTrue

class AppTest {
    
    @Test 
    fun `test dbUrl generates correct sqlite connection string`() {
        val url = dbUrl()
        
        // Ensures the URL starts with the correct JDBC SQLite prefix
        assertTrue(url.startsWith("jdbc:sqlite:"), "DB URL should start with jdbc:sqlite:")
        // Ensures the URL points to your actual database file
        assertTrue(url.contains("Database.db"), "DB URL should contain Database.db")
    }
}