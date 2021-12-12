package mapleGBP.dao.repository

import mapleGBP.model.Party
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PartyRepository: JpaRepository<Party, Int> {
}