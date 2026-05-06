package org.flightbooking

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AppHelperTest {

    @Test
    fun `test sqlText returns null for blank strings`() {
        // checking if blank or empty inputs correctly return null
        assertNull(sqlText(""))
        assertNull(sqlText("   "))
    }

    @Test
    fun `test sqlText returns the string if valid`() {
        assertEquals("valid_input", sqlText("valid_input"))
    }

    @Test
    fun `test esc function correctly escapes quotes to prevent SQL issues`() {
        // making sure quotes get escaped properly so sql doesn't break
        val input = """He said "hello""""
        val expected = """He said \"hello\""""
        
        assertEquals(expected, esc(input))
    }
}