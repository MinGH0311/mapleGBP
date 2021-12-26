package mapleGBP.dao.guild

import mapleGBP.dao.repository.GuildRepository
import mapleGBP.model.Guild
import mapleGBP.model.World
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import javax.persistence.EntityNotFoundException
import javax.persistence.PersistenceException

@Component
class GuildDaoJpa(var guildRepository: GuildRepository): GuildDao {
    override fun addGuild(name: String, world: World): Guild {
        return guildRepository.save(Guild(guildName = name, world = world, createdAt = LocalDateTime.now()))
    }

    override fun addGuild(guild: Guild): Guild {
        guild.createdAt = LocalDateTime.now()
        guildRepository.save(guild)
        return guild
    }

    override fun getGuild(name: String, world: World): Guild {
        return guildRepository.findByGuildNameAndWorld(name, world).orElseThrow { throw EntityNotFoundException("Guild with name=${name} and world=${world} not found") }
    }

    override fun getAllGuilds(): List<Guild> {
        return guildRepository.findAll()
    }

    override fun deleteGuild(name: String, world: World): Guild {
        val guild: Guild = guildRepository.findByGuildNameAndWorld(name, world).orElseThrow { throw EntityNotFoundException("Guild with name=${name} and world=${world} not found") }

        guildRepository.delete(guild)

        if (guildRepository.existsById(guild.gid)) {
            throw PersistenceException("Failed to delete guild with name=${name} and world=${world}")
        } else {
            return guild
        }
    }

    override fun deleteGuild(guild: Guild): Guild {
        guildRepository.delete(guild)

        if (guildRepository.existsById(guild.gid)) {
            throw PersistenceException("Failed to delete guild with name=${guild.guildName} and world=${guild.world}")
        } else {
            return guild
        }
    }
}