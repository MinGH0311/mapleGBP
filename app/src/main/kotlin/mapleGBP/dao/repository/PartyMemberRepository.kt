package mapleGBP.dao.repository

import mapleGBP.model.PartyMember
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PartyMemberRepository: JpaRepository<PartyMember, Int> {
}