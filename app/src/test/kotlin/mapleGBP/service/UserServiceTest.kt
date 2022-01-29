package mapleGBP.service

import mapleGBP.TestConfiguration
import mapleGBP.dao.repository.GuildRepository
import mapleGBP.dao.repository.UserRepository
import mapleGBP.model.Guild
import mapleGBP.model.User
import mapleGBP.model.World
import mapleGBP.model.dto.UserInfo
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.lang.RuntimeException
import javax.persistence.EntityNotFoundException

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
internal class UserServiceTest {
    val testGuild: Guild = Guild(0, emptyList(), "Test-Guild", World.SCANIA)

    val testUser1: User = User(1, testGuild, "https://google.com", "test1", 100, "히어로", 30, 200, World.SCANIA)
    val testUser2: User = User(2, testGuild, "https://google.com", "test2", 1000, "히어로", 50, 250, World.SCANIA)

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var guildRepository: GuildRepository

    @Autowired
    lateinit var userService: UserService

    @BeforeEach
    fun clearDB() {
        userRepository.deleteAll()
        guildRepository.deleteAll()
    }

    @Test
    fun testDB() {
        assertEquals(userRepository.count(), 0L, "Check DB Connection is connected to test DB")
        assertEquals(guildRepository.count(), 0L, "Check DB Connection is connected to test DB")
    }

    @Test
    fun getAllUserInfo() {
        guildRepository.save(testGuild)
        userRepository.save(testUser1)
        userRepository.save(testUser2)

        userService.getAllUserInfo().let { userInfos: List<UserInfo> ->
            assertEquals(userInfos.count(), 2)
            assertIterableEquals(userInfos, listOf(testUser1.toUserInfo(), testUser2.toUserInfo()))
        }
    }

    @Test
    fun getUserInfo() {
        guildRepository.save(testGuild)
        userRepository.save(testUser1)

        val userInfo: UserInfo = userService.getUserInfo(testUser1.nickname)

        checkNull(userInfo)
        assertEquals(userInfo.nickname, "test1")
        assertEquals(userInfo.`class`, "히어로")
        assertTrue(userInfo.level > 0)
    }

    @Test
    fun getUserInfo_notExistUser() {
        assertThrows(EntityNotFoundException::class.java) { userService.getUserInfo(testUser1.nickname) }
    }

    @Test
    fun searchUserInfo() {
        val userInfo: UserInfo = userService.searchUserInfo("빨간색빨갱이")

        checkNull(userInfo)
        assertEquals(userInfo.nickname, "빨간색빨갱이")
        assertEquals(userInfo.`class`, "팔라딘")
        assertEquals(userInfo.world, World.SCANIA)
        assertTrue(userInfo.level > 0)
    }

    @Test
    fun searchUserInfo_notExistUser() {
        assertThrows(RuntimeException::class.java) { userService.searchUserInfo("invalid_user") }
    }

    @Test
    fun saveOrUpdateUserInfo_saveNewUser() {
        guildRepository.save(testGuild)

        val savedUserInfo: UserInfo = userService.saveOrUpdateUserInfo(testUser1.toUserInfo())

        checkNull(savedUserInfo)
        assertEquals(savedUserInfo, testUser1.toUserInfo())
    }

    @Test
    fun saveOrUpdateUserInfo_updateUser() {
        guildRepository.save(testGuild)
        userRepository.save(testUser1)

        val updateTestUser: User = User(
            guild = testUser1.guild,
            image = testUser1.image,
            nickname = testUser1.nickname,
            union = testUser1.union + 500,
            `class` = testUser1.`class`,
            mureong = testUser1.mureong + 3,
            level = testUser1.level + 30,
            world = testUser1.world,
            extras = testUser1.extras,
            createdAt = testUser1.createdAt,
            updatedAt = testUser1.updatedAt
        )
        val updatedUserInfo: UserInfo = userService.saveOrUpdateUserInfo(updateTestUser.toUserInfo())

        checkNull(updatedUserInfo)
        assertEquals(updatedUserInfo, updateTestUser.toUserInfo())

        val userInfo: UserInfo = userService.getUserInfo(updateTestUser.nickname)

        checkNull(userInfo)
        assertEquals(userInfo.union - testUser1.union, 500)
        assertEquals(userInfo.mureong - testUser1.mureong, 3)
        assertEquals(userInfo.level - testUser1.level, 30)
    }

    @Test
    fun saveOrUpdateUserInfo_invalidGuild() {
        val savedUserInfo: UserInfo = userService.saveOrUpdateUserInfo(testUser1.toUserInfo())

        checkNull(savedUserInfo)
        assertEquals(savedUserInfo.guild, "")
    }

    @Test
    fun deleteUserInfo() {
        guildRepository.save(testGuild)

        userService.saveOrUpdateUserInfo(testUser1.toUserInfo())
        assertDoesNotThrow({ userService.getUserInfo(testUser1.nickname) })

        userService.deleteUserInfo(testUser1.nickname)
        assertThrows(EntityNotFoundException::class.java) { userService.getUserInfo(testUser1.nickname) }
    }

    @Test
    fun deleteUserInfo_notExistUser() {
        assertThrows(EntityNotFoundException::class.java) { userService.deleteUserInfo(testUser1.nickname) }
    }

    private fun checkNull(userInfo: UserInfo) {
        assertNotNull(userInfo.nickname)
        assertNotNull(userInfo.image)
        assertNotNull(userInfo.level)
        assertNotNull(userInfo.`class`)
        assertNotNull(userInfo.world)
    }
}