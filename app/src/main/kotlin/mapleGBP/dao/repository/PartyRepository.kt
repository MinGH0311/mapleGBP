package mapleGBP.dao.repository

import mapleGBP.model.Party
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PartyRepository: JpaRepository<Party, Int> {
    fun findByPartyName(name: String): Optional<Party>
}