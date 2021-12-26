package mapleGBP.dao.party

import mapleGBP.dao.repository.PartyRepository
import mapleGBP.model.Party
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import javax.persistence.EntityNotFoundException
import javax.persistence.PersistenceException

@Component
class PartyDaoJpa(var partyRepository: PartyRepository): PartyDao {

    override fun addParty(party: Party): Party {
        party.createdAt = LocalDateTime.now()
        return partyRepository.save(party)
    }

    override fun getParty(name: String): Party {
        return partyRepository.findByPartyName(name).orElseThrow { EntityNotFoundException("failed to find party with party_name=${name}") }
    }

    override fun getParty(id: Int): Party {
        return partyRepository.findById(id).orElseThrow { EntityNotFoundException("failed to find party with pid=${id}") }
    }

    override fun getAllParties(): List<Party> {
        return partyRepository.findAll()
    }

    override fun updateParty(party: Party): Party {
        party.updatedAt = LocalDateTime.now()
        val savedParty: Party = partyRepository.findById(party.pid).orElseThrow { EntityNotFoundException("failed to find party with pid=${party.pid}") }
        return partyRepository.save(party)
    }

    override fun deleteParty(id: Int): Party {
        val savedParty: Party = partyRepository.findById(id).orElseThrow { EntityNotFoundException("faild to find party with pid=${id}") }
        partyRepository.delete(savedParty)

        if (partyRepository.existsById(id)) {
            throw PersistenceException("failed to delete party with pid=${id}")
        } else {
            return savedParty
        }
    }
}