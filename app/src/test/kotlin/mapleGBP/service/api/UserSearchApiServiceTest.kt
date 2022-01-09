package mapleGBP.service.api

import mapleGBP.config.Configuration
import mapleGBP.model.dto.UserSearchApiResponse
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
@ContextConfiguration(classes = [Configuration::class])
internal class UserSearchApiServiceTest {

    @Autowired
    lateinit var userSearchApiService: UserSearchApiService

    @Test
    fun getUserInfo() {
        val userInfo: UserSearchApiResponse = userSearchApiService.getUserInfo("빨간색빨갱이")
        println(userInfo)

        assertEquals(userInfo.nickname, "빨간색빨갱이")
        assertEquals(userInfo.classes, "팔라딘")
    }

    @Test
    fun getUserInfo_includeNull() {
        val userInfo: UserSearchApiResponse = userSearchApiService.getUserInfo("팔라딘빨갱이")
        println(userInfo)

        assertEquals(userInfo.nickname, "팔라딘빨갱이")
        assertEquals(userInfo.classes, "팔라딘")
        assertNull(userInfo.seedInfo)
        assertNull(userInfo.unionInfo)
    }

    @Test
    fun getUserInfo_notExist() {
        try {
            println(userSearchApiService.getUserInfo("invalid_name"))
            fail("Runtime exception is expected, but exception didn't throw")
        } catch (e: RuntimeException) {
            assertContains(e.message ?: "", "E200")
        } catch (e: Exception) {
            fail("Unexpected exception ${e.javaClass} occurred")
        }
    }
}