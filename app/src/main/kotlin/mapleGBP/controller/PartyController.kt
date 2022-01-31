package mapleGBP.controller

import io.swagger.annotations.ApiOperation
import mapleGBP.model.dto.PartyInfo
import mapleGBP.model.dto.UserInfo
import mapleGBP.service.PartyService
import org.springframework.web.bind.annotation.*

@RestController
class PartyController(
    var partyService: PartyService,
) {

    @ApiOperation("전체 파티 조회")
    @GetMapping("/party")
    fun getAllParties(): List<PartyInfo> {
        return partyService.getAllPartyInfo()
    }

    @ApiOperation("파티 조회")
    @GetMapping("/party/{partyName}")
    fun getParty(@PathVariable("partyName") partyName: String): PartyInfo {
        return partyService.getPartyInfo(partyName)
    }

    @ApiOperation("파티 등록")
    @PostMapping("/party")
    fun createParty(@RequestBody partyInfo: PartyInfo): Boolean {
        partyService.savePartyInfo(partyInfo)
        return true
    }

    @ApiOperation("파티원 추가")
    @PostMapping("/party/{partyName}/members")
    fun addPartyMember(@PathVariable("partyName") partyName: String,
                       @RequestBody newUsers: List<UserInfo>): Boolean {
        partyService.addPartyMembers(partyName, newUsers)
        return true
    }

    @ApiOperation("파티원 삭제")
    @DeleteMapping("/party/{partyName}/members")
    fun removePartyMember(@PathVariable("partyName") partyName: String,
                          @RequestBody targetUsers: List<UserInfo>): Boolean {
        partyService.removePartyMembers(partyName, targetUsers)
        return true
    }

    @ApiOperation("파티 삭제")
    @DeleteMapping("/party/{partyName}")
    fun deleteParty(@PathVariable("partyName") partyName: String): Boolean {
        partyService.deleteParty(partyName)
        return true
    }
}