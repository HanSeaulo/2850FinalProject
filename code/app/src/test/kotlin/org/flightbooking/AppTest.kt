package org.flightbooking

import kotlin.test.Test
import kotlin.test.assertTrue

class AppTest {
    
    @Test 
    fun `test dbUrl generates correct sqlite connection string`() {
        val url = dbUrl()
        
        // check that the driver prefix is right
        assertTrue(url.startsWith("jdbc:sqlite:"), "DB URL should start with jdbc:sqlite:")
        
        // make sure it actually points to our db file
        assertTrue(url.contains("Database.db"), "DB URL should contain Database.db")
    }
}