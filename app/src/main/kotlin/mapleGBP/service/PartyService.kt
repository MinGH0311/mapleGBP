package mapleGBP.service

import mapleGBP.dao.party.PartyDao
import mapleGBP.dao.party.PartyMemberDao
import mapleGBP.dao.user.UserDao
import mapleGBP.model.Party
import mapleGBP.model.PartyMember
import mapleGBP.model.User
import mapleGBP.model.dto.PartyInfo
import mapleGBP.model.dto.UserInfo
import org.springframework.stereotype.Service

@Service
class PartyService(
    var partyDao: PartyDao,
    var partyMemberDao: PartyMemberDao,
    var userDao: UserDao
) {
    fun getAllPartyInfo(): List<PartyInfo> {
        return partyDao.getAllParties().map { party: Party -> getPartyInfo(party.partyName) }
    }

    fun getPartyInfo(partyName: String): PartyInfo {
        val party: Party = partyDao.getParty(partyName)
        val partyMembers: List<User> = partyMemberDao.getPartyMembersWithParty(party).map { partyMember: PartyMember ->
            userDao.getUser(partyMember.uid)
        }

        return PartyInfo(party.partyName, partyMembers.map { user -> PartyInfo.PartyMember(user.nickname, user.image, user.`class`, user.level) })
    }

    fun getPartiesInfoWithUser(userInfo: UserInfo): List<PartyInfo> {
        val user: User = userDao.getUser(userInfo.nickname)
        val parties: List<Party> = partyMemberDao.getPartyMembersWithUser(user).map { partyMember: PartyMember ->
            partyDao.getParty(partyMember.pid)
        }

        return parties.map { party: Party -> getPartyInfo(party.partyName) }
    }

    fun savePartyInfo(partyInfo: PartyInfo): PartyInfo {
        val newParty: Party = Party(partyName = partyInfo.name)
        val newPartyMembers: List<User> = partyInfo.members.map {
            partyMember: PartyInfo.PartyMember -> userDao.getUser(partyMember.nickname)
        }

        val savedNewParty: Party = partyDao.addParty(newParty)
        val savedNewPartyMembers: List<PartyMember> = partyMemberDao.addPartyMembers(savedNewParty, newPartyMembers)

        return partyInfo
    }

    fun addPartyMembers(partyName: String, newMembers: List<UserInfo>): PartyInfo {
        val party: Party = partyDao.getParty(partyName)
        val newMemberUsers: List<User> = newMembers.map { partyMember: UserInfo ->
            userDao.getUser(partyMember.nickname)
        }

        partyMemberDao.addPartyMembers(party, newMemberUsers)

        val partyMembers: List<User> = partyMemberDao.getPartyMembersWithParty(party).map { partyMember: PartyMember ->
            userDao.getUser(partyMember.uid)
        }

        return PartyInfo(party.partyName, partyMembers.map { user -> PartyInfo.PartyMember(user.nickname, user.image, user.`class`, user.level) })
    }

    fun removePartyMembers(partyName: String, targetMembers: List<UserInfo>): PartyInfo {
        val party: Party = partyDao.getParty(partyName)
        val targetPartyMembers: List<User> = targetMembers.map { partyMember: UserInfo ->
            userDao.getUser(partyMember.nickname)
        }

        targetPartyMembers.forEach { partyMember: User ->
            partyMemberDao.deletePartyMember(party, partyMember)
        }

        val partyMembers: List<User> = partyMemberDao.getPartyMembersWithParty(party).map { partyMember: PartyMember ->
            userDao.getUser(partyMember.uid)
        }

        return PartyInfo(party.partyName, partyMembers.map { user -> PartyInfo.PartyMember(user.nickname, user.image, user.`class`, user.level) })
    }

    fun deleteParty(partyName: String): PartyInfo {
        val party: Party = partyDao.getParty(partyName)
        val partyMembers: List<User> = partyMemberDao.deletePartyMembersByParty(party).map { partyMember: PartyMember ->
            userDao.getUser(partyMember.uid)
        }
        partyDao.deleteParty(party.pid)

        return PartyInfo(party.partyName, partyMembers.map { user -> PartyInfo.PartyMember(user.nickname, user.image, user.`class`, user.level) })
    }
}