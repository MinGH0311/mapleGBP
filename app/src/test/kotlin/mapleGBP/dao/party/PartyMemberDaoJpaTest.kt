package mapleGBP.dao.party

import mapleGBP.TestDatabase
import mapleGBP.dao.repository.PartyMemberRepository
import mapleGBP.dao.repository.PartyRepository
import mapleGBP.dao.repository.UserRepository
import mapleGBP.model.Party
import mapleGBP.model.PartyMember
import mapleGBP.model.User
import mapleGBP.model.World
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
internal class PartyMemberDaoJpaTest {

    var testParty1: Party = Party(1, "test-party1")
    var testParty2: Party = Party(2, "test_party2")

    var testUsers: List<User> = listOf(
        User(1, null, "https://google.com", "test1", 100, "히어로", 30, 200, World.SCANIA),
        User(2, null, "https://google.com", "test2", 1000, "히어로", 30, 200, World.SCANIA),
        User(3, null, "https://google.com", "test3", 1500, "히어로", 30, 200, World.REBOOT2),
        User(4, null, "https://google.com", "test4", 3000, "히어로", 30, 200, World.AURORA),
    )

    var testPartyMembers: List<PartyMember> = emptyList()

    @Autowired
    lateinit var partyRepository: PartyRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var partyMemberRepository: PartyMemberRepository

    @Autowired
    lateinit var partyMemberDao: PartyMemberDao

    @BeforeEach
    fun clearTestDB() {
        partyMemberRepository.deleteAll()
        userRepository.deleteAll()
        partyRepository.deleteAll()

        testUsers = testUsers.map { user: User -> userRepository.save(user) }
        testParty1 = partyRepository.save(testParty1)
        testParty2 = partyRepository.save(testParty2)

        testPartyMembers = listOf(
            PartyMember(pid = testParty1.pid, uid = testUsers[0].uid),
            PartyMember(pid = testParty1.pid, uid = testUsers[1].uid),
            PartyMember(pid = testParty2.pid, uid = testUsers[2].uid),
            PartyMember(pid = testParty2.pid, uid = testUsers[3].uid),
        )
    }

    @Test
    fun testDB() {
        assertEquals(partyMemberRepository.count(), 0L)
    }

    @Test
    fun addPartyMember() {
        assertEquals(partyMemberRepository.count(), 0L)

        var savedPartyMember: PartyMember = partyMemberDao.addPartyMember(testParty1, testUsers[0])
        assertEquals(partyMemberRepository.count(), 1L)
        assert(savedPartyMember.pmid > testPartyMembers[0].pmid)
        assertEquals(partyMemberDao.getPartyMembersWithUser(testUsers[0])[0], testPartyMembers[0])
        assertNotNull(savedPartyMember.createdAt)

        savedPartyMember = partyMemberDao.addPartyMember(testParty1, testUsers[1])
        assertEquals(partyMemberRepository.count(), 2L)
        assert(savedPartyMember.pmid > testPartyMembers[1].pmid)
        assertEquals(partyMemberDao.getPartyMembersWithUser(testUsers[1])[0], testPartyMembers[1])
        assertNotNull(partyMemberDao.getPartyMembersWithUser(testUsers[1])[0].createdAt)

        savedPartyMember = partyMemberDao.addPartyMember(testParty2, testUsers[2])
        assertEquals(partyMemberRepository.count(), 3L)
        assert(savedPartyMember.pmid > testPartyMembers[2].pmid)
        assertEquals(partyMemberDao.getPartyMembersWithUser(testUsers[2])[0], testPartyMembers[2])
        assertNotNull(partyMemberDao.getPartyMembersWithUser(testUsers[2])[0].createdAt)

        savedPartyMember = partyMemberDao.addPartyMember(testParty2, testUsers[3])
        assertEquals(partyMemberRepository.count(), 4L)
        assert(savedPartyMember.pmid > testPartyMembers[3].pmid)
        assertEquals(partyMemberDao.getPartyMembersWithUser(testUsers[3])[0], testPartyMembers[3])
        assertNotNull(partyMemberDao.getPartyMembersWithUser(testUsers[3])[0].createdAt)
    }

    @Test
    fun addPartyMember_duplicatePartyAndUser() {
        assertEquals(partyMemberRepository.count(), 0L)

        partyMemberDao.addPartyMember(testParty1, testUsers[0])
        assertEquals(partyMemberRepository.count(), 1L)

        assertThrows(DataIntegrityViolationException::class.java, { partyMemberDao.addPartyMember(testParty1, testUsers[0]) })
        assertEquals(partyMemberRepository.count(), 1L)
    }

    @Test
    fun addPartyMembers() {
        assertEquals(partyMemberRepository.count(), 0L)

        partyMemberDao.addPartyMembers(testParty1, testUsers.subList(0, 2))
        assertEquals(partyMemberRepository.count(), 2L)
        assertIterableEquals(partyMemberDao.getPartyMembersWithParty(testParty1), testPartyMembers.subList(0, 2))
        assertTrue { partyMemberDao.getPartyMembersWithParty(testParty1).all { partyMember: PartyMember -> partyMember.createdAt != null } }

        partyMemberDao.addPartyMembers(testParty2, testUsers.subList(2, 4))
        assertEquals(partyMemberRepository.count(), 4L)
        assertIterableEquals(partyMemberDao.getPartyMembersWithParty(testParty2), testPartyMembers.subList(2, 4))
        assertTrue { partyMemberDao.getPartyMembersWithParty(testParty2).all { partyMember: PartyMember -> partyMember.createdAt != null } }
    }

    @Test
    fun addPartyMembers_duplicatePartyAndUsers() {
        assertEquals(partyMemberRepository.count(), 0L)

        partyMemberDao.addPartyMembers(testParty1, testUsers.subList(0, 2))
        assertEquals(partyMemberRepository.count(), 2L)

        assertThrows(DataIntegrityViolationException::class.java, { partyMemberDao.addPartyMembers(testParty1, testUsers.subList(0, 2)) })
        assertEquals(partyMemberRepository.count(), 2L)
    }

    @Test
    fun getPartyMembersWithUser() {
        assertEquals(partyMemberRepository.count(), 0L)

        testPartyMembers.map { partyMember: PartyMember -> partyMemberRepository.save(partyMember) }
        assertEquals(partyMemberRepository.count(), testPartyMembers.size.toLong())

        assertIterableEquals(partyMemberDao.getPartyMembersWithUser(testUsers[0]), listOf(testPartyMembers[0]))
        assertIterableEquals(partyMemberDao.getPartyMembersWithUser(testUsers[1]), listOf(testPartyMembers[1]))
        assertIterableEquals(partyMemberDao.getPartyMembersWithUser(testUsers[2]), listOf(testPartyMembers[2]))
        assertIterableEquals(partyMemberDao.getPartyMembersWithUser(testUsers[3]), listOf(testPartyMembers[3]))
    }

    @Test
    fun getPartyMembersWithParty() {
        assertEquals(partyMemberRepository.count(), 0L)

        testPartyMembers.forEach { partyMember: PartyMember -> partyMemberRepository.save(partyMember) }
        assertEquals(partyMemberRepository.count(), testPartyMembers.size.toLong())

        assertIterableEquals(partyMemberDao.getPartyMembersWithParty(testParty1), testPartyMembers.subList(0, 2))
        assertIterableEquals(partyMemberDao.getPartyMembersWithParty(testParty2), testPartyMembers.subList(2, 4))
    }

    @Test
    fun deletePartyMember() {
        assertEquals(partyMemberRepository.count(), 0L)

        testPartyMembers.forEach { partyMember: PartyMember -> partyMemberRepository.save(partyMember) }
        assertEquals(partyMemberRepository.count(), testPartyMembers.size.toLong())

        assertThrows(EntityNotFoundException::class.java, { partyMemberDao.deletePartyMember(testParty2, testUsers[0]) })
        partyMemberDao.deletePartyMember(testParty1, testUsers[0])
        assertEquals(partyMemberRepository.count(), 3L)
        assertIterableEquals(partyMemberDao.getPartyMembersWithUser(testUsers[0]), emptyList<Int>())

        assertThrows(EntityNotFoundException::class.java, { partyMemberDao.deletePartyMember(testParty2, testUsers[1]) })
        partyMemberDao.deletePartyMember(testParty1, testUsers[1])
        assertEquals(partyMemberRepository.count(), 2L)
        assertIterableEquals(partyMemberDao.getPartyMembersWithUser(testUsers[1]), emptyList<Int>())

        assertThrows(EntityNotFoundException::class.java, { partyMemberDao.deletePartyMember(testParty1, testUsers[2]) })
        partyMemberDao.deletePartyMember(testParty2, testUsers[2])
        assertEquals(partyMemberRepository.count(), 1L)
        assertIterableEquals(partyMemberDao.getPartyMembersWithUser(testUsers[2]), emptyList<Int>())

        assertThrows(EntityNotFoundException::class.java, { partyMemberDao.deletePartyMember(testParty1, testUsers[3]) })
        partyMemberDao.deletePartyMember(testParty2, testUsers[3])
        assertEquals(partyMemberRepository.count(), 0L)
        assertIterableEquals(partyMemberDao.getPartyMembersWithUser(testUsers[3]), emptyList<Int>())
    }

    @Test
    fun deletePartyMember_notExist() {
        assertEquals(partyMemberRepository.count(), 0L)

        assertThrows(EntityNotFoundException::class.java, { partyMemberDao.deletePartyMember(testParty1, testUsers[0]) })
        assertEquals(partyMemberRepository.count(), 0L)
    }

    @Test
    fun deletePartyMembersByParty() {
        assertEquals(partyMemberRepository.count(), 0L)

        testPartyMembers.forEach { partyMember: PartyMember -> partyMemberRepository.save(partyMember) }
        assertEquals(partyMemberRepository.count(), testPartyMembers.size.toLong())

        partyMemberDao.deletePartyMemberByUser(testUsers[0])
        assertEquals(partyMemberRepository.count(), 3L)
        assertIterableEquals(partyMemberDao.getPartyMembersWithUser(testUsers[0]), emptyList<Int>())

        partyMemberDao.deletePartyMemberByUser(testUsers[1])
        assertEquals(partyMemberRepository.count(), 2L)
        assertIterableEquals(partyMemberDao.getPartyMembersWithUser(testUsers[1]), emptyList<Int>())

        partyMemberDao.deletePartyMemberByUser(testUsers[2])
        assertEquals(partyMemberRepository.count(), 1L)
        assertIterableEquals(partyMemberDao.getPartyMembersWithUser(testUsers[2]), emptyList<Int>())

        partyMemberDao.deletePartyMemberByUser(testUsers[3])
        assertEquals(partyMemberRepository.count(), 0L)
        assertIterableEquals(partyMemberDao.getPartyMembersWithUser(testUsers[3]), emptyList<Int>())
    }

    @Test
    fun deletePartyMemberByUser() {
        assertEquals(partyMemberRepository.count(), 0L)

        testPartyMembers.forEach { partyMember: PartyMember -> partyMemberRepository.save(partyMember) }
        assertEquals(partyMemberRepository.count(), testPartyMembers.size.toLong())

        partyMemberDao.deletePartyMembersByParty(testParty1)
        assertEquals(partyMemberRepository.count(), 2L)
        assertIterableEquals(partyMemberDao.getPartyMembersWithParty(testParty1), emptyList<Int>())

        partyMemberDao.deletePartyMembersByParty(testParty2)
        assertEquals(partyMemberRepository.count(), 0L)
        assertIterableEquals(partyMemberDao.getPartyMembersWithParty(testParty2), emptyList<Int>())
    }
}