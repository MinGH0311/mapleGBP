package mapleGBP.dao.repository

import mapleGBP.model.Guild
import mapleGBP.model.World
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface GuildRepository: JpaRepository<Guild, Int> {

    fun findByGuildNameAndWorld(name: String, world: World): Optional<Guild>
}