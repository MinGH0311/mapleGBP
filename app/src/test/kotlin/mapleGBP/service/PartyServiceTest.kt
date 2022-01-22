package mapleGBP.service

import mapleGBP.TestConfiguration
import mapleGBP.dao.repository.PartyMemberRepository
import mapleGBP.dao.repository.PartyRepository
import mapleGBP.dao.repository.UserRepository
import mapleGBP.model.PartyMember
import mapleGBP.model.User
import mapleGBP.model.World
import mapleGBP.model.dto.PartyInfo
import mapleGBP.model.dto.UserInfo
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
@ContextConfiguration(classes = [TestConfiguration::class])
internal class PartyServiceTest {
    val testUsers: List<User> = listOf(
        User(1, null, "https://google.com", "test1", 100, "히어로", 30, 200, World.SCANIA),
        User(2, null, "https://google.com", "test2", 1000, "히어로", 30, 200, World.SCANIA),
        User(3, null, "https://google.com", "test3", 1500, "히어로", 30, 200, World.REBOOT2),
        User(4, null, "https://google.com", "test4", 3000, "히어로", 30, 200, World.AURORA),
    )

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var partyRepository: PartyRepository

    @Autowired
    lateinit var partyMemberRepository: PartyMemberRepository

    @Autowired
    lateinit var partyService: PartyService

    @BeforeEach
    fun clearDB() {
        partyMemberRepository.deleteAll()
        partyRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    fun testDB() {
        assertEquals(userRepository.count(), 0L, "Check DB Connection is connected to test DB")
        assertEquals(partyRepository.count(), 0L, "Check DB Connection is connected to test DB")
        assertEquals(partyMemberRepository.count(), 0L, "Check DB Connection is connected to test DB")
    }

    @Test
    fun getPartyInfo() {
        val testParty: PartyInfo = PartyInfo("test-party1",
            testUsers.subList(0, 2).map { user: User -> PartyInfo.PartyMember(user.nickname, user.image, user.`class`, user.level) }
        )
        userRepository.saveAll(testUsers)
        partyService.savePartyInfo(testParty)

        val partyInfo: PartyInfo = partyService.getPartyInfo(testParty.name)
        assertEquals(partyInfo, testParty)
    }

    @Test
    fun getPartyInfo_notExistParty() {
        assertThrows(EntityNotFoundException::class.java) { partyService.getPartyInfo("not-exist") }
    }

    @Test
    fun getPartiesInfoWithUser() {
        val testParties: List<PartyInfo> = listOf(
            PartyInfo("test-party1", testUsers.subList(0, 4).map { user: User -> PartyInfo.PartyMember(user.nickname, user.image, user.`class`, user.level) }),
            PartyInfo("test-party2", testUsers.subList(0, 2).map { user: User -> PartyInfo.PartyMember(user.nickname, user.image, user.`class`, user.level) }),
            PartyInfo("test-party3", testUsers.subList(1, 3).map { user: User -> PartyInfo.PartyMember(user.nickname, user.image, user.`class`, user.level) }),
            PartyInfo("test-party4", testUsers.subList(2, 4).map { user: User -> PartyInfo.PartyMember(user.nickname, user.image, user.`class`, user.level) }),
        )

        userRepository.saveAll(testUsers)
        testParties.forEach { partyInfo: PartyInfo -> partyService.savePartyInfo(partyInfo) }

        assertEquals(partyRepository.count(), 4L)
        partyService.getPartiesInfoWithUser(testUsers[0].toUserInfo()).let { partyInfos: List<PartyInfo> ->
            assertEquals(partyInfos.count(), 2)
            assertIterableEquals(partyInfos, listOf(testParties[0], testParties[1]))
        }

        partyService.getPartiesInfoWithUser(testUsers[1].toUserInfo()).let { partyInfos: List<PartyInfo> ->
            assertEquals(partyInfos.count(), 3)
            assertIterableEquals(partyInfos, listOf(testParties[0], testParties[1], testParties[2]))
        }

        partyService.getPartiesInfoWithUser(testUsers[2].toUserInfo()).let { partyInfos: List<PartyInfo> ->
            assertEquals(partyInfos.count(), 3)
            assertIterableEquals(partyInfos, listOf(testParties[0], testParties[2], testParties[3]))
        }

        partyService.getPartiesInfoWithUser(testUsers[3].toUserInfo()).let { partyInfos: List<PartyInfo> ->
            assertEquals(partyInfos.count(), 2)
            assertIterableEquals(partyInfos, listOf(testParties[0], testParties[3]))
        }
    }

    @Test
    fun savePartyInfo() {
        val testParty: PartyInfo = PartyInfo("test-party1", testUsers.subList(0, 2).map { user: User -> PartyInfo.PartyMember(user.nickname, user.image, user.`class`, user.level) })
        userRepository.saveAll(testUsers)

        partyService.savePartyInfo(testParty)
        assertEquals(partyRepository.count(), 1L)
        assertEquals(partyRepository.findAll()[0].partyName, testParty.name)

        assertEquals(partyMemberRepository.count(), 2L)
        assertIterableEquals(partyMemberRepository.findAll().map { partyMember: PartyMember? -> userRepository.findById(partyMember?.uid).get() }, testUsers.subList(0, 2))
    }

    @Test
    fun savePartyInfo_withNotSavedUser() {
        val testParty: PartyInfo = PartyInfo("test-party1", testUsers.subList(0, 2).map { user: User -> PartyInfo.PartyMember(user.nickname, user.image, user.`class`, user.level) })

        assertThrows(EntityNotFoundException::class.java) { partyService.savePartyInfo(testParty) }
        assertEquals(partyRepository.count(), 0L)
        assertEquals(userRepository.count(), 0L)
    }

    @Test
    fun addPartyMembers() {
        val testParty: PartyInfo = PartyInfo("test-party1", testUsers.subList(0, 2).map { user: User -> PartyInfo.PartyMember(user.nickname, user.image, user.`class`, user.level) })
        userRepository.saveAll(testUsers)
        partyService.savePartyInfo(testParty)

        assertEquals(partyRepository.count(), 1L)
        assertEquals(partyMemberRepository.count(), 2L)
        assertEquals(partyService.getPartyInfo("test-party1"), testParty)

        partyService.addPartyMembers("test-party1", testUsers.subList(2, 4).map { user: User -> user.toUserInfo() })

        assertEquals(partyMemberRepository.count(), 4L)
        assertIterableEquals(partyMemberRepository.findAll().map { partyMember: PartyMember? -> userRepository.findById(partyMember?.uid).get() }, testUsers)
    }

    @Test
    fun addPartyMembers_notExistParty() {
        assertEquals(partyRepository.count(), 0L)
        assertThrows(EntityNotFoundException::class.java) { partyService.addPartyMembers("not-exist", testUsers.subList(2, 4).map { user: User -> user.toUserInfo() }) }
    }

    @Test
    fun addPartyMembers_notExistUser() {
        val testParty: PartyInfo = PartyInfo("test-party1", testUsers.subList(0, 2).map { user: User -> PartyInfo.PartyMember(user.nickname, user.image, user.`class`, user.level) })
        userRepository.saveAll(testUsers)
        partyService.savePartyInfo(testParty)

        assertEquals(partyRepository.count(), 1L)
        assertEquals(partyMemberRepository.count(), 2L)
        assertEquals(partyService.getPartyInfo("test-party1"), testParty)

        assertThrows(EntityNotFoundException::class.java) {partyService.addPartyMembers("test-party1", listOf(UserInfo("not-exist", "", "", 0, "", 0, 0, World.NONE, ""))) }
    }

    @Test
    fun addPartyMembers_duplicateMembers() {
        val testParty: PartyInfo = PartyInfo("test-party1", testUsers.subList(0, 2).map { user: User -> PartyInfo.PartyMember(user.nickname, user.image, user.`class`, user.level) })
        userRepository.saveAll(testUsers)
        partyService.savePartyInfo(testParty)

        assertEquals(partyRepository.count(), 1L)
        assertEquals(partyMemberRepository.count(), 2L)
        assertEquals(partyService.getPartyInfo("test-party1"), testParty)

        assertThrows(DataIntegrityViolationException::class.java) { partyService.addPartyMembers("test-party1", testUsers.subList(0, 2).map { user: User -> user.toUserInfo() }) }
    }

    @Test
    fun removePartyMembers() {
        val testParty: PartyInfo = PartyInfo("test-party1", testUsers.map { user: User -> PartyInfo.PartyMember(user.nickname, user.image, user.`class`, user.level) })
        userRepository.saveAll(testUsers)
        partyService.savePartyInfo(testParty)

        assertEquals(partyRepository.count(), 1L)
        assertEquals(partyMemberRepository.count(), 4L)
        assertEquals(partyService.getPartyInfo("test-party1"), testParty)

        partyService.removePartyMembers("test-party1", testUsers.subList(2, 4).map { user: User -> user.toUserInfo() })

        assertEquals(partyMemberRepository.count(), 2L)
        assertIterableEquals(partyMemberRepository.findAll().map { partyMember: PartyMember? -> userRepository.findById(partyMember?.uid).get() }, testUsers.subList(0, 2))
    }

    @Test
    fun removePartyMembers_notExistParty() {
        assertEquals(partyRepository.count(), 0L)
        assertThrows(EntityNotFoundException::class.java) { partyService.removePartyMembers("test-party1", testUsers.subList(2, 4).map { user: User -> user.toUserInfo() }) }
    }

    @Test
    fun removePartyMembers_notExistUser() {
        val testParty: PartyInfo = PartyInfo("test-party1", testUsers.map { user: User -> PartyInfo.PartyMember(user.nickname, user.image, user.`class`, user.level) })
        userRepository.saveAll(testUsers)
        partyService.savePartyInfo(testParty)

        assertEquals(partyRepository.count(), 1L)
        assertEquals(partyMemberRepository.count(), 4L)
        assertEquals(partyService.getPartyInfo("test-party1"), testParty)

        assertThrows(EntityNotFoundException::class.java) {partyService.removePartyMembers("test-party1", listOf(UserInfo("not-exist", "", "", 0, "", 0, 0, World.NONE, ""))) }
    }

    @Test
    fun removePartyMembers_notMemberUser() {
        val testParty: PartyInfo = PartyInfo("test-party1", testUsers.subList(0, 2).map { user: User -> PartyInfo.PartyMember(user.nickname, user.image, user.`class`, user.level) })
        userRepository.saveAll(testUsers)
        partyService.savePartyInfo(testParty)

        assertEquals(partyRepository.count(), 1L)
        assertEquals(partyMemberRepository.count(), 2L)
        assertEquals(partyService.getPartyInfo("test-party1"), testParty)

        assertThrows(EntityNotFoundException::class.java) { partyService.removePartyMembers("test-party1", testUsers.subList(2, 4).map { user: User -> user.toUserInfo() }) }
    }

    @Test
    fun deleteParty() {
        val testParty: PartyInfo = PartyInfo("test-party1", testUsers.map { user: User -> PartyInfo.PartyMember(user.nickname, user.image, user.`class`, user.level) })
        userRepository.saveAll(testUsers)
        partyService.savePartyInfo(testParty)

        assertEquals(partyRepository.count(), 1L)
        assertEquals(partyMemberRepository.count(), 4L)
        assertEquals(partyService.getPartyInfo("test-party1"), testParty)

        partyService.deleteParty(testParty.name)
        assertEquals(partyRepository.count(), 0L)
        assertEquals(partyMemberRepository.count(), 0L)
        assertEquals(userRepository.count(), 4L)
    }

    @Test
    fun deleteParty_notExistParty() {
        assertThrows(EntityNotFoundException::class.java) { partyService.deleteParty("not-exist") }
    }
}