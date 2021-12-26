package mapleGBP.dao.party

import mapleGBP.TestDatabase
import mapleGBP.dao.repository.PartyRepository
import mapleGBP.model.Party
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import javax.persistence.EntityNotFoundException

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestDatabase::class])
internal class PartyDaoJpaTest {

    @Autowired
    lateinit var partyRepository: PartyRepository

    @Autowired
    lateinit var partyDao: PartyDao

    val testParty: List<Party> = listOf(
        Party(1, "test-party1"),
        Party(2, "test-party2"),
        Party(3, "test-party3"),
        Party(4, "test-party4")
    )

    @BeforeEach
    fun cleanTestDB() {
        partyRepository.deleteAll()
    }

    @Test
    fun testDB() {
        assertEquals(partyRepository.count(), 0L)
    }

    @Test
    fun addParty() {
        assertEquals(partyRepository.count(), 0L)

        testParty.forEach { party: Party -> partyDao.addParty(party) }
        assertEquals(partyRepository.count(), testParty.count().toLong())

        testParty.forEach { party: Party ->
            val savedParty: Party = partyDao.getParty(party.partyName)

            assert(savedParty.pid > party.pid)
            assertEquals(savedParty, party)
            assertNotNull(savedParty.createdAt)
        }
    }

    @Test
    fun addParty_duplicatePartyName() {
        assertEquals(partyRepository.count(), 0L)

        partyDao.addParty(Party(partyName = "test"))
        assertEquals(partyRepository.count(), 1L)

        assertThrows(DataIntegrityViolationException::class.java, { partyDao.addParty(Party(partyName = "test")) })
        assertEquals(partyRepository.count(), 1L)
    }

    @Test
    fun getPartyById() {
        assertEquals(partyRepository.count(), 0L)

        testParty.forEach { party: Party -> partyRepository.save(party) }

        val savedParty: List<Party> = partyRepository.findAll()

        savedParty.mapIndexed { index: Int, party: Party -> Pair(index, party) }
            .forEach { indexedParty: Pair<Int, Party> ->
                run {
                    assertEquals(partyDao.getParty(indexedParty.second.pid), testParty[indexedParty.first])
                }
            }
    }

    @Test
    fun getPartyByName() {
        assertEquals(partyRepository.count(), 0L)

        testParty.forEach { party: Party -> partyRepository.save(party) }

        val savedParty: List<Party> = partyRepository.findAll()

        savedParty.mapIndexed { index: Int, party: Party -> Pair(index, party) }
            .forEach { indexedParty: Pair<Int, Party> ->
                run {
                    assertEquals(partyDao.getParty(indexedParty.second.partyName), testParty[indexedParty.first])
                }
            }
    }

    @Test
    fun getPartyByName_notExist() {
        assertEquals(partyRepository.count(), 0L)

        assertThrows(EntityNotFoundException::class.java, { partyDao.getParty(testParty[0].partyName) })
        assertThrows(EntityNotFoundException::class.java, { partyDao.getParty(testParty[0].pid) })
    }

    @Test
    fun getAllParties() {
        assertEquals(partyRepository.count(), 0L)

        testParty.forEach { party: Party -> partyRepository.save(party) }
        assertEquals(partyRepository.count(), testParty.count().toLong())

        assertIterableEquals(partyDao.getAllParties(), testParty)
    }

    @Test
    fun updateParty() {
        assertEquals(partyRepository.count(), 0L)

        testParty.forEach { party: Party -> partyDao.addParty(party) }

        val savedParty: List<Party> = partyRepository.findAll()

        savedParty.mapIndexed { index: Int, party: Party -> Pair(index, party) }
            .forEach { indexedParty: Pair<Int, Party> ->
                run {
                    val updateParty: Party = Party(indexedParty.second.pid, indexedParty.second.partyName + "_update")

                    partyDao.updateParty(updateParty)
                    assertEquals(partyDao.getParty(indexedParty.second.pid).partyName, testParty[indexedParty.first].partyName + "_update")
                    assert(partyDao.getParty(indexedParty.second.pid).updatedAt?.isAfter(indexedParty.second.createdAt ?: fail("created_at column is null"))?: fail("updated_at column is null"))
                }
            }
    }

    @Test
    fun updateParty_notExsit() {
        assertEquals(partyRepository.count(), 0L)

        assertThrows(EntityNotFoundException::class.java, { partyDao.updateParty(testParty[0]) })
        assertEquals(partyRepository.count(), 0L)
    }

    @Test
    fun updateParty_duplicateParty() {
        assertEquals(partyRepository.count(), 0L)

        partyDao.addParty(testParty[0])
        partyDao.addParty(testParty[1])
        assertEquals(partyRepository.count(), 2L)

        val savedParty: Party = partyDao.getParty(testParty[1].partyName)
        val updateParty: Party = Party(savedParty.pid, testParty[0].partyName)
        assertThrows(DataIntegrityViolationException::class.java, { partyDao.updateParty(updateParty) })
        assertEquals(partyDao.getParty(updateParty.pid), testParty[1])
    }

    @Test
    fun deleteParty() {
        assertEquals(partyRepository.count(), 0L)

        testParty.forEach { party: Party -> partyRepository.save(party) }

        val savedParty: List<Party> = partyRepository.findAll()

        savedParty.mapIndexed { index: Int, party: Party -> Pair(index, party) }
            .forEach { indexedParty: Pair<Int, Party> ->
                run {
                    partyDao.deleteParty(indexedParty.second.pid)
                    assertEquals(partyRepository.count(), testParty.count().toLong() - indexedParty.first.toLong() - 1)
                }
            }
    }

    @Test
    fun deleteParty_notExist() {
        assertEquals(partyRepository.count(), 0L)

        assertThrows(EntityNotFoundException::class.java, { partyDao.deleteParty(0) })
        assertEquals(partyRepository.count(), 0L)
    }
}