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

    val testUser: User = User(1, testGuild, "https://google.com", "test1", 100, "히어로", 30, 200, World.SCANIA)

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
    fun getUserInfo() {
        guildRepository.save(testGuild)
        userRepository.save(testUser)

        val userInfo: UserInfo = userService.getUserInfo(testUser.nickname)

        checkNull(userInfo)
        assertEquals(userInfo.nickname, "test1")
        assertEquals(userInfo.`class`, "히어로")
        assertTrue(userInfo.level > 0)
    }

    @Test
    fun getUserInfo_notExistUser() {
        assertThrows(EntityNotFoundException::class.java) { userService.getUserInfo(testUser.nickname) }
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

        val savedUserInfo: UserInfo = userService.saveOrUpdateUserInfo(testUser.toUserInfo())

        checkNull(savedUserInfo)
        assertEquals(savedUserInfo, testUser.toUserInfo())
    }

    @Test
    fun saveOrUpdateUserInfo_updateUser() {
        guildRepository.save(testGuild)
        userRepository.save(testUser)

        val updateTestUser: User = User(
            guild = testUser.guild,
            image = testUser.image,
            nickname = testUser.nickname,
            union = testUser.union + 500,
            `class` = testUser.`class`,
            mureong = testUser.mureong + 3,
            level = testUser.level + 30,
            world = testUser.world,
            extras = testUser.extras,
            createdAt = testUser.createdAt,
            updatedAt = testUser.updatedAt
        )
        val updatedUserInfo: UserInfo = userService.saveOrUpdateUserInfo(updateTestUser.toUserInfo())

        checkNull(updatedUserInfo)
        assertEquals(updatedUserInfo, updateTestUser.toUserInfo())

        val userInfo: UserInfo = userService.getUserInfo(updateTestUser.nickname)

        checkNull(userInfo)
        assertEquals(userInfo.union - testUser.union, 500)
        assertEquals(userInfo.mureong - testUser.mureong, 3)
        assertEquals(userInfo.level - testUser.level, 30)
    }

    @Test
    fun saveOrUpdateUserInfo_invalidGuild() {
        val savedUserInfo: UserInfo = userService.saveOrUpdateUserInfo(testUser.toUserInfo())

        checkNull(savedUserInfo)
        assertEquals(savedUserInfo.guild, "")
    }

    @Test
    fun deleteUserInfo() {
        guildRepository.save(testGuild)

        userService.saveOrUpdateUserInfo(testUser.toUserInfo())
        assertDoesNotThrow({ userService.getUserInfo(testUser.nickname) })

        userService.deleteUserInfo(testUser.nickname)
        assertThrows(EntityNotFoundException::class.java) { userService.getUserInfo(testUser.nickname) }
    }

    @Test
    fun deleteUserInfo_notExistUser() {
        assertThrows(EntityNotFoundException::class.java) { userService.deleteUserInfo(testUser.nickname) }
    }

    private fun checkNull(userInfo: UserInfo) {
        assertNotNull(userInfo.nickname)
        assertNotNull(userInfo.image)
        assertNotNull(userInfo.level)
        assertNotNull(userInfo.`class`)
        assertNotNull(userInfo.world)
    }
}