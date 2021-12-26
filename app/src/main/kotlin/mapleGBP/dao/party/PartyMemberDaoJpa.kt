package mapleGBP.dao.party

import mapleGBP.dao.repository.PartyMemberRepository
import mapleGBP.dao.repository.PartyRepository
import mapleGBP.model.Party
import mapleGBP.model.PartyMember
import mapleGBP.model.User
import org.springframework.data.domain.Example
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import javax.persistence.EntityNotFoundException
import javax.persistence.PersistenceException

@Component
class PartyMemberDaoJpa(var partyMemberRepository: PartyMemberRepository): PartyMemberDao {

    override fun addPartyMember(party: Party, user: User): PartyMember {
        val newPartyMember: PartyMember = PartyMember(pid = party.pid, uid = user.uid, createdAt = LocalDateTime.now())
        return partyMemberRepository.save(newPartyMember)
    }

    override fun addPartyMembers(party: Party, users: List<User>): List<PartyMember> {
        val newPartyMembers: List<PartyMember> = users.map { user: User -> PartyMember(pid = party.pid, uid = user.uid, createdAt = LocalDateTime.now()) }
        return partyMemberRepository.saveAll(newPartyMembers)
    }

    override fun getPartyMembersWithUser(user: User): List<PartyMember> {
        return partyMemberRepository.findAllByUid(user.uid)
    }

    override fun getPartyMembersWithParty(party: Party): List<PartyMember> {
        return partyMemberRepository.findAllByPid(party.pid)
    }

    override fun deletePartyMember(party: Party, user: User): PartyMember {
        val savedPartyMember: PartyMember = partyMemberRepository.findByPidAndUid(party.pid, user.uid).orElseThrow( { EntityNotFoundException("failed to find party_member with pid=${party.pid}, uid=${user.uid}") })
        partyMemberRepository.delete(savedPartyMember)

        if (partyMemberRepository.existsById(savedPartyMember.pmid)) {
            throw PersistenceException("Failed to delete party member with pid=${party.pid}, uid=${user.uid}")
        } else {
            return savedPartyMember
        }
    }

    override fun deletePartyMembersByParty(party: Party): List<PartyMember> {
        val savedPartyMember: List<PartyMember> = partyMemberRepository.findAllByPid(party.pid)

        partyMemberRepository.deleteAll(savedPartyMember)

        if (partyMemberRepository.count(Example.of(PartyMember(pid = party.pid))) > 0) {
            throw PersistenceException("Failed to delete party member with pid ${party.pid}")
        } else {
            return savedPartyMember
        }
    }

    override fun deletePartyMemberByUser(user: User): List<PartyMember> {
        val savedPartyMember: List<PartyMember> = partyMemberRepository.findAllByUid(user.uid)

        partyMemberRepository.deleteAll(savedPartyMember)

        if (partyMemberRepository.count(Example.of(PartyMember(uid = user.uid))) > 0) {
            throw PersistenceException("Failed to delete party member with uid ${user.uid}")
        } else {
            return savedPartyMember
        }
    }
}