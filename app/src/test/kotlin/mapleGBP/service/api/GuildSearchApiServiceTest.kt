package mapleGBP.service.api

import mapleGBP.TestConfiguration
import mapleGBP.config.Configuration
import mapleGBP.model.World
import mapleGBP.model.dto.GuildSearchApiResponse
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.test.assertContains

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
internal class GuildSearchApiServiceTest {

    @Autowired
    lateinit var guildSearchApiService: GuildSearchApiService

    @Test
    fun getGuildInfo() {
        val guildInfo: GuildSearchApiResponse = guildSearchApiService.getGuildInfo("출구", World.SCANIA)
        println(guildInfo)

        assertEquals(guildInfo.name, "출구")
        assertEquals(guildInfo.world, World.SCANIA)
        assertTrue(guildInfo.member.size > 0)
        assertContains(guildInfo.member.map {
                memberInfo: GuildSearchApiResponse.GuildMember -> memberInfo.nickname
        }.joinToString(), "빨간색빨갱이")
    }

    @Test
    fun getGuildInfo_notExistWorld() {
        try {
            println(guildSearchApiService.getGuildInfo("출구", World.NONE))
            fail("Runtime exception is expected, but exception didn't throw")
        } catch (e: RuntimeException) {
            assertContains(e.message ?: "", "E002")
        } catch (e: Exception) {
            fail("Unexpect exception ${e.javaClass} was thrown")
        }
    }

    @Test
    fun getGuildInfo_notExistGuild() {
        try {
            println(guildSearchApiService.getGuildInfo("invalid_guild_name", World.SCANIA))
            fail("Runtime exception is expected, but exception didn't thrown")
        } catch (e: RuntimeException) {
            assertContains(e.message ?: "", "E100")
        } catch (e: Exception) {
            fail("Unexpected exception ${e.javaClass} was thrown")
        }
    }
}