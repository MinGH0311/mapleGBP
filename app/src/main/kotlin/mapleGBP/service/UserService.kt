package mapleGBP.service

import mapleGBP.dao.guild.GuildDao
import mapleGBP.dao.user.UserDao
import mapleGBP.model.Guild
import mapleGBP.model.User
import mapleGBP.model.dto.UserInfo
import mapleGBP.service.api.UserSearchApiService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.lang.RuntimeException
import javax.persistence.EntityNotFoundException

@Service
class UserService(
    var userDao: UserDao,
    var guildDao: GuildDao,
    var userSearchApiService: UserSearchApiService
) {
    val logger: Logger = LoggerFactory.getLogger(UserService::class.java)

    fun getUserInfo(nickname: String): UserInfo {
        return userDao.getUser(nickname).toUserInfo()
    }

    fun searchUserInfo(nickname: String): UserInfo {
        return userSearchApiService.getUserInfo(nickname).toUserInfo()
    }

    fun saveOrUpdateUserInfo(userInfo: UserInfo): UserInfo {
        var userGuild: Guild?

        try {
            userGuild = guildDao.getGuild(userInfo.guild, userInfo.world)
        } catch (e: Exception) {
            userGuild = null
        }

        try {
            val savedUser: User = userDao.getUser(userInfo.nickname)
            val updatedUser: User = User(
                uid = savedUser.uid,
                guild = userGuild,
                image = userInfo.image,
                nickname = userInfo.nickname,
                union = userInfo.union,
                `class` = userInfo.`class`,
                mureong = userInfo.mureong,
                level = userInfo.level,
                world = userInfo.world,
                extras = userInfo.extras,
                createdAt = savedUser.createdAt
            )

            return userDao.updateUser(updatedUser).toUserInfo()
        } catch (e: EntityNotFoundException) {
            val newUser: User = User(
                guild = userGuild,
                image = userInfo.image,
                nickname = userInfo.nickname,
                union = userInfo.union,
                `class` = userInfo.`class`,
                mureong = userInfo.mureong,
                level = userInfo.level,
                world = userInfo.world,
                extras = userInfo.extras
            )

            return userDao.addUser(newUser).toUserInfo()
        } catch (e: Exception) {
            logger.error("failed to save or update user ${userInfo.nickname}. ${e.message}")
            throw e
        }
    }

    fun deleteUserInfo(nickname: String): UserInfo {
        val user: User = userDao.getUser(nickname)
        val deletedUserInfo: UserInfo = userDao.deleteUser(user.uid).toUserInfo()

        if (user.toUserInfo() == deletedUserInfo) {
            return deletedUserInfo
        } else {
            throw RuntimeException("${nickname} user info is updated while deleting user")
        }
    }
}