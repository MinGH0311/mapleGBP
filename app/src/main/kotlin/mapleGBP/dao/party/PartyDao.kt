package mapleGBP.dao.party

import mapleGBP.model.Party

interface PartyDao {
    fun addParty(party: Party): Party

    fun getParty(name: String): Party

    fun getParty(id: Int): Party

    fun getAllParties(): List<Party>

    fun updateParty(party: Party): Party

    fun deleteParty(id: Int): Party
}