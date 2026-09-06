package com.afoxplus.emergency.presentation.contacts

import org.junit.Assert.assertEquals
import org.junit.Test

class ContactsUiStateTest {

    private val elena = Contact("1", "Elena Martínez", "+34 600 111 222")
    private val javier = Contact("2", "Javier Ruiz", "+34 655 444 333")
    private val sofia = Contact("3", "Sofía García", "+34 677 888 999")
    private val carmen = Contact("4", "Carmen Rodríguez", "+34 612 345 678")

    private val state = ContactsUiState(phoneContacts = listOf(elena, javier, sofia, carmen))

    @Test
    fun matchesByFirstName() {
        assertEquals(listOf(javier), state.copy(searchQuery = "Javier").displayedContacts)
    }

    @Test
    fun matchesByLastName() {
        assertEquals(listOf(carmen), state.copy(searchQuery = "Rodríguez").displayedContacts)
    }

    @Test
    fun matchesByFullName() {
        assertEquals(listOf(sofia), state.copy(searchQuery = "Sofía García").displayedContacts)
    }

    @Test
    fun matchesByPhoneNumber() {
        assertEquals(listOf(elena), state.copy(searchQuery = "600 111").displayedContacts)
    }

    @Test
    fun blankSearchLimitsToFirstThreeContacts() {
        assertEquals(listOf(elena, javier, sofia), state.displayedContacts)
    }

    @Test
    fun totalContactsCountReflectsAllContactsNotOnlyDisplayed() {
        assertEquals(4, state.totalContactsCount)
        assertEquals(3, state.displayedContacts.size)
    }
}
