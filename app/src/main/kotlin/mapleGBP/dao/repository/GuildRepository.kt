package mapleGBP.dao.repository

import mapleGBP.model.Guild
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GuildRepository: JpaRepository<Guild, Int> {
}