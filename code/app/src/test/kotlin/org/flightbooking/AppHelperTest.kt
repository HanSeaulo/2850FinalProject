package org.flightbooking

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AppHelperTest {

    @Test
    fun `test sqlText returns null for blank strings`() {
        // Testing how your program reacts to empty inputs
        assertNull(sqlText(""))
        assertNull(sqlText("   "))
    }

    @Test
    fun `test sqlText returns the string if valid`() {
        assertEquals("valid_input", sqlText("valid_input"))
    }

    @Test
    fun `test esc function correctly escapes quotes to prevent SQL issues`() {
        // Testing a potential security risk (SQL injection vectors)
        val input = """He said "hello""""
        val expected = """He said \"hello\""""
        
        assertEquals(expected, esc(input))
    }
}