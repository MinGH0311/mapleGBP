package mapleGBP.controller

import mapleGBP.model.dto.PartyInfo
import mapleGBP.model.dto.UserInfo
import mapleGBP.service.PartyService
import org.springframework.web.bind.annotation.*

@RestController
class PartyController(
    var partyService: PartyService,
) {

    @GetMapping("/party")
    fun getAllParties(): List<PartyInfo> {
        return partyService.getAllPartyInfo()
    }

    @GetMapping("/party/{partyName}")
    fun getParty(@PathVariable("partyName") partyName: String): PartyInfo {
        return partyService.getPartyInfo(partyName)
    }

    @PostMapping("/party")
    fun createParty(@RequestBody partyInfo: PartyInfo): Boolean {
        partyService.savePartyInfo(partyInfo)
        return true
    }

    @PostMapping("/party/{partyName}/members")
    fun addPartyMember(@PathVariable("partyName") partyName: String,
                       @RequestBody newUsers: List<UserInfo>): Boolean {
        partyService.addPartyMembers(partyName, newUsers)
        return true
    }

    @DeleteMapping("/party/{partyName}/members")
    fun removePartyMember(@PathVariable("partyName") partyName: String,
                          @RequestBody targetUsers: List<UserInfo>): Boolean {
        partyService.removePartyMembers(partyName, targetUsers)
        return true
    }

    @DeleteMapping("/party/{partyName}")
    fun deleteParty(@PathVariable("partyName") partyName: String): Boolean {
        partyService.deleteParty(partyName)
        return true
    }
}