package com.afoxplus.emergency.presentation.contacts

import com.afoxplus.emergency.domain.model.Contact
import com.afoxplus.emergency.domain.model.EmergencyContactType

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Verifies the Primary/Backup role assignment and duplicate-prevention rules that
 * [EmergencyContactRepositoryImpl] relies on (order-derived roles), using the in-memory
 * [FakeEmergencyContactRepository] test double since the real implementation requires an
 * Android [android.content.Context].
 */
class EmergencyContactRepositoryTest {

    private val carmen = Contact("1", "Carmen Rodríguez", "+34 612 345 678")
    private val david = Contact("2", "David Alonso", "+34 688 991 223")
    private val sofia = Contact("3", "Sofía García", "+34 677 888 999")

    @Test
    fun firstAddedContactIsPrimary() {
        val repository = FakeEmergencyContactRepository()

        repository.addEmergencyContact(carmen)

        assertEquals(EmergencyContactType.PRIMARY, repository.getEmergencyContacts().single().type)
    }

    @Test
    fun subsequentContactsAreBackup() {
        val repository = FakeEmergencyContactRepository()

        repository.addEmergencyContact(carmen)
        repository.addEmergencyContact(david)
        repository.addEmergencyContact(sofia)

        val contacts = repository.getEmergencyContacts()
        assertEquals(EmergencyContactType.PRIMARY, contacts[0].type)
        assertEquals(EmergencyContactType.BACKUP, contacts[1].type)
        assertEquals(EmergencyContactType.BACKUP, contacts[2].type)
    }

    @Test
    fun addingDuplicateContactIsRejected() {
        val repository = FakeEmergencyContactRepository()

        assertTrue(repository.addEmergencyContact(carmen))
        assertFalse(repository.addEmergencyContact(carmen))
        assertEquals(1, repository.getEmergencyContacts().size)
    }

    @Test
    fun removingPrimaryPromotesNextContactToPrimary() {
        val repository = FakeEmergencyContactRepository()
        repository.addEmergencyContact(carmen)
        repository.addEmergencyContact(david)
        repository.addEmergencyContact(sofia)

        repository.removeEmergencyContact(carmen.id)

        val contacts = repository.getEmergencyContacts()
        assertEquals(david.id, contacts[0].contactId)
        assertEquals(EmergencyContactType.PRIMARY, contacts[0].type)
        assertEquals(sofia.id, contacts[1].contactId)
        assertEquals(EmergencyContactType.BACKUP, contacts[1].type)
    }

    @Test
    fun isEmergencyContactReflectsCurrentState() {
        val repository = FakeEmergencyContactRepository()
        repository.addEmergencyContact(carmen)

        assertTrue(repository.isEmergencyContact(carmen.id))

        repository.removeEmergencyContact(carmen.id)

        assertFalse(repository.isEmergencyContact(carmen.id))
    }
}
