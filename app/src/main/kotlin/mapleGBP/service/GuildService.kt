package mapleGBP.service

import mapleGBP.dao.guild.GuildDao
import mapleGBP.model.Guild
import mapleGBP.model.User
import mapleGBP.model.World
import mapleGBP.model.dto.GuildInfo
import mapleGBP.model.dto.UserInfo
import mapleGBP.service.api.GuildSearchApiService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class GuildService(
    var guildDao: GuildDao,
    var userService: UserService,
    var guildSearchApiService: GuildSearchApiService
) {
    @Transactional
    open fun getAllGuildInfo(): List<GuildInfo> {
        return guildDao.getAllGuilds().map { guild: Guild -> guild.toGuildInfo() }
    }

    @Transactional
    open fun getGuildInfo(guildName: String, world: World): GuildInfo {
        return guildDao.getGuild(guildName, world).toGuildInfo()
    }

    open fun searchGuildInfo(guildName: String, world: World): GuildInfo {
        return guildSearchApiService.getGuildInfo(guildName, world).toGuildInfo()
    }

    open fun saveGuildInfo(guildInfo: GuildInfo): GuildInfo {
        val newGuild: Guild = Guild(
            guildName = guildInfo.name,
            world = guildInfo.world
        )

        return guildDao.addGuild(newGuild).toGuildInfo()
    }

    @Transactional
    open fun saveOrUpdateAllGuildMember(guildInfo: GuildInfo): GuildInfo {
        getGuildInfo(guildInfo.name, guildInfo.world)
        val searchedGuildInfo: GuildInfo = searchGuildInfo(guildInfo.name, guildInfo.world)

        val savedGuildMemberInfo: List<GuildInfo.GuildMember> = searchedGuildInfo.members.map { member: GuildInfo.GuildMember ->
            userService.searchUserInfo(member.nickname)
        }.map { userInfo: UserInfo ->
            userService.saveOrUpdateUserInfo(userInfo)
        }.map { userInfo: UserInfo ->
            GuildInfo.GuildMember(
                nickname = userInfo.nickname,
                image = userInfo.image,
                `class` = userInfo.`class`,
                level = userInfo.level
            )
        }

        return GuildInfo(
            name = searchedGuildInfo.name,
            world = searchedGuildInfo.world,
            members = savedGuildMemberInfo
        )
    }

    open fun deleteGuildInfo(guildName: String, world: World): GuildInfo {
        guildDao.deleteGuild(guildName, world)
        return GuildInfo(guildName, world, emptyList())
    }

    @Transactional
    open fun deleteGuildInfoWithMember(guildName: String, world: World): GuildInfo {
        val guild: Guild = guildDao.getGuild(guildName, world)

        val deletedGuildMember: List<GuildInfo.GuildMember> = guild.users.map { user: User ->
            userService.deleteUserInfo(user.nickname)
        }.map { userInfo: UserInfo ->
            GuildInfo.GuildMember(
                nickname = userInfo.nickname,
                image = userInfo.image,
                `class` = userInfo.`class`,
                level = userInfo.level
            )
        }

        deleteGuildInfo(guildName, world)

        return GuildInfo(
            name = guild.guildName,
            world = guild.world,
            members = deletedGuildMember
        )
    }
}