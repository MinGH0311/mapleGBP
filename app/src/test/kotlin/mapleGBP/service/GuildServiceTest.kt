package mapleGBP.service

import mapleGBP.TestConfiguration
import mapleGBP.dao.repository.GuildRepository
import mapleGBP.dao.repository.UserRepository
import mapleGBP.model.Guild
import mapleGBP.model.User
import mapleGBP.model.World
import mapleGBP.model.dto.GuildInfo
import mapleGBP.model.dto.GuildSearchApiResponse
import mapleGBP.service.api.GuildSearchApiService
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.spy
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.lang.RuntimeException
import javax.persistence.EntityNotFoundException
import kotlin.test.assertContains

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
internal class GuildServiceTest {
    val testGuild: Guild = Guild(0, emptyList(), "Test-Guild", World.SCANIA)

    val testUsers: List<User> = listOf(
        User(1, testGuild, "https://google.com", "test1", 100, "히어로", 30, 200, World.SCANIA),
        User(2, testGuild, "https://google.com", "test2", 1000, "히어로", 30, 200, World.SCANIA),
        User(3, null, "https://google.com", "test3", 1500, "히어로", 30, 200, World.REBOOT2),
        User(4, null, "https://google.com", "test4", 3000, "히어로", 30, 200, World.AURORA),
    )

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var guildRepository: GuildRepository

    @Autowired
    lateinit var guildService: GuildService

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var guildSearchApiService: GuildSearchApiService

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
    fun getGuildInfo() {
        guildRepository.save(testGuild)

        val guildInfo: GuildInfo = guildService.getGuildInfo(testGuild.guildName, testGuild.world)
        assertEquals(guildInfo, testGuild.toGuildInfo())
    }

    @Test
    fun getGuildInfo_notExistGuild() {
        assertThrows(EntityNotFoundException::class.java) { guildService.getGuildInfo(testGuild.guildName, testGuild.world) }
    }

    @Test
    fun searchGuildInfo() {
        val searchGuildInfo: GuildInfo = guildService.searchGuildInfo("출구", World.SCANIA)

        assertEquals(searchGuildInfo.name, "출구")
        assertEquals(searchGuildInfo.world, World.SCANIA)
        assertTrue(searchGuildInfo.members.isNotEmpty())
        assertContains(searchGuildInfo.members.map { guildMember -> guildMember.nickname }.joinToString(","),"빨간색빨갱이")
    }

    @Test
    fun searchGuildInfo_notExistGuild() {
        assertThrows(RuntimeException::class.java) { guildService.searchGuildInfo("출구", World.NONE) }
        assertThrows(RuntimeException::class.java) { guildService.searchGuildInfo("invalid_name", World.SCANIA) }
    }

    @Test
    fun saveGuildInfo() {
        val savedGuildInfo: GuildInfo = guildService.saveGuildInfo(testGuild.toGuildInfo())
        assertEquals(savedGuildInfo, testGuild.toGuildInfo())
    }

    @Test
    fun saveOrUpdateAllGuildMember() {
        guildService.saveGuildInfo(testGuild.toGuildInfo())

        val spyUserService: UserService = spy(userService)
        val spyGuildSearchApiService: GuildSearchApiService = spy(guildSearchApiService)
        guildService.userService = spyUserService
        guildService.guildSearchApiService = spyGuildSearchApiService

        val mockGuildSearchApiResponse: GuildSearchApiResponse = GuildSearchApiResponse(
            testGuild.guildName,
            testGuild.world,
            listOf(
                GuildSearchApiResponse.GuildMember(testUsers[0].image, testUsers[0].nickname, testUsers[0].`class`, testUsers[0].level),
                GuildSearchApiResponse.GuildMember(testUsers[1].image, testUsers[1].nickname, testUsers[1].`class`, testUsers[1].level)
            )
        )

        doReturn(mockGuildSearchApiResponse).whenever(spyGuildSearchApiService).getGuildInfo(eq(testGuild.guildName), eq(testGuild.world))
        doReturn(testUsers[0].toUserInfo()).whenever(spyUserService).searchUserInfo(eq(testUsers[0].nickname))
        doReturn(testUsers[1].toUserInfo()).whenever(spyUserService).searchUserInfo(eq(testUsers[1].nickname))

        guildService.saveOrUpdateAllGuildMember(testGuild.toGuildInfo())

        assertEquals(userRepository.count(), 2L)
        assertIterableEquals(userRepository.findAll(), testUsers.subList(0, 2))

        guildService.userService = userService
        guildService.guildSearchApiService = guildSearchApiService
    }

    @Test
    fun saveOrUpdateAllGuildMember_notExistGuild() {
        assertThrows(EntityNotFoundException::class.java) { guildService.saveOrUpdateAllGuildMember(testGuild.toGuildInfo()) }
    }

    @Test
    fun deleteGuildInfo() {
        val savedGuildInfo: GuildInfo = guildService.saveGuildInfo(testGuild.toGuildInfo())
        assertEquals(savedGuildInfo, testGuild.toGuildInfo())

        guildService.deleteGuildInfo(testGuild.guildName, testGuild.world)
        assertThrows(EntityNotFoundException::class.java) { guildService.getGuildInfo(testGuild.guildName, testGuild.world) }
    }

    @Test
    fun deleteGuildInfo_notExistGuild() {
        assertThrows(EntityNotFoundException::class.java) { guildService.deleteGuildInfo(testGuild.guildName, testGuild.world) }
    }

    @Test
    fun deleteGuildInfoWithMember() {
        guildRepository.save(testGuild)
        userRepository.saveAll(testUsers)

        val savedGuildInfo: GuildInfo = guildService.getGuildInfo(testGuild.guildName, testGuild.world)
        assertEquals(savedGuildInfo.name, testGuild.guildName)
        assertEquals(savedGuildInfo.world, testGuild.world)
        assertIterableEquals(userRepository.findAll(), testUsers)

        guildService.deleteGuildInfoWithMember(testGuild.guildName, testGuild.world)
        assertThrows(EntityNotFoundException::class.java) { guildService.getGuildInfo(testGuild.guildName, testGuild.world) }
        assertIterableEquals(userRepository.findAll(), testUsers.subList(2, testUsers.size))
    }
}