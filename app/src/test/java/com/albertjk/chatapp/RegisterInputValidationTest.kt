package com.albertjk.chatapp

import com.albertjk.chatapp.fragments.RegisterFragment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RegisterInputValidationTest {

    private val r = RegisterFragment()

    @Test
    fun `invalid input if photo alpha is not zero`() {
        val (validInput, errorMessage) = r.validateInput(1f, "username", "email", "password")
        assertFalse(validInput)
        assertEquals("Please add a photo.", errorMessage)
    }

    @Test
    fun `invalid input if username is empty`() {
        val (validInput, errorMessage) = r.validateInput(0f, "", "email", "password")
        assertFalse(validInput)
        assertEquals("Please enter a valid username.", errorMessage)
    }

    @Test
    fun `invalid input if email is empty`() {
        val (validInput, errorMessage) = r.validateInput(0f, "username", "", "password")
        assertFalse(validInput)
        assertEquals("Please enter a valid email.", errorMessage)
    }

    @Test
    fun `invalid input if password is empty`() {
        val (validInput, errorMessage) = r.validateInput(0f, "username", "email", "")
        assertFalse(validInput)
        assertEquals("Please enter a valid password.", errorMessage)
    }

    @Test
    fun `invalid password if it is shorter than 8 characters`() {
        val (validInput, errorMessage) = r.validateInput(0f, "username", "email", "pw")
        assertFalse(validInput)
        assertEquals("The password doesn't meet the requirements.", errorMessage)
    }

    @Test
    fun `invalid password if it does not contain a lowercase character`() {
        val (validInput, errorMessage) = r.validateInput(0f, "username", "email", "ATESTPASSWORD1")
        assertFalse(validInput)
        assertEquals("The password doesn't meet the requirements.", errorMessage)
    }

    @Test
    fun `invalid password if it does not contain an uppercase character`() {
        val (validInput, errorMessage) = r.validateInput(0f, "username", "email", "atestpassword1")
        assertFalse(validInput)
        assertEquals("The password doesn't meet the requirements.", errorMessage)
    }

    @Test
    fun `invalid password if it does not contain a digit`() {
        val (validInput, errorMessage) = r.validateInput(0f, "username", "email", "ATestPassword")
        assertFalse(validInput)
        assertEquals("The password doesn't meet the requirements.", errorMessage)
    }

    @Test
    fun `all input is valid`() {
        val (validInput, errorMessage) = r.validateInput(0f, "username", "email", "aTestPassword1")
        assertTrue(validInput)
        assertEquals("", errorMessage)
    }
}