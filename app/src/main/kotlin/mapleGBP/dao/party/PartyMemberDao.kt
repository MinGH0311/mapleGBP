package mapleGBP.dao.party

import mapleGBP.model.Party
import mapleGBP.model.PartyMember
import mapleGBP.model.User

interface PartyMemberDao {
    fun addPartyMember(party: Party, user: User): PartyMember

    fun addPartyMembers(party: Party, users: List<User>): List<PartyMember>

    fun getPartyMembersWithUser(user: User): List<PartyMember>

    fun getPartyMembersWithParty(party: Party): List<PartyMember>

    fun deletePartyMember(party: Party, user: User): PartyMember

    fun deletePartyMembersByParty(party: Party): List<PartyMember>

    fun deletePartyMemberByUser(user: User): List<PartyMember>
}