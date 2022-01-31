package mapleGBP.controller

import io.swagger.annotations.ApiOperation
import mapleGBP.model.World
import mapleGBP.model.dto.GuildInfo
import mapleGBP.service.GuildService
import org.springframework.web.bind.annotation.*
import javax.persistence.EntityNotFoundException

@RestController
class GuildController(
    var guildService: GuildService
) {

    @ApiOperation("전체 길드 조회")
    @GetMapping("/guild")
    fun getAllGuilds(): List<GuildInfo> {
        return guildService.getAllGuildInfo()
    }

    @ApiOperation("길드 조회")
    @GetMapping("/guild/{world}/{guildName}")
    fun getGuild(@PathVariable("world") world: World,
                 @PathVariable("guildName") guildName: String): GuildInfo {
        try {
            return guildService.getGuildInfo(guildName, world)
        } catch (e: EntityNotFoundException) {
            return guildService.searchGuildInfo(guildName, world)
        }
    }

    @ApiOperation("길드 등록")
    @PostMapping("/guild")
    fun createGuild(@RequestBody guildInfo: GuildInfo): Boolean {
        guildService.saveGuildInfo(guildInfo)
        return true
    }

    @ApiOperation("길드 멤버 정보 갱신")
    @PostMapping("/guild/{world}/{guildName}/sync")
    fun saveOrUpdateGuildMember(@PathVariable("world") world: World,
                                @PathVariable("guildName") guildName: String): Boolean {
        guildService.saveOrUpdateAllGuildMember(GuildInfo(guildName, world, emptyList()))
        return true
    }

    @ApiOperation("길드 삭제")
    @DeleteMapping("/guild/{world}/{guildName}")
    fun deleteGuild(@PathVariable("world") world: World,
                    @PathVariable("guildName") guildName: String): Boolean {
        guildService.deleteGuildInfo(guildName, world)
        return true
    }
}