package mapleGBP.dao.repository

import mapleGBP.model.Guild
import mapleGBP.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository: JpaRepository<User, Int> {

    fun findByNickname(name: String): Optional<User>
    fun findAllByGuild(guild: Guild): List<User>
}