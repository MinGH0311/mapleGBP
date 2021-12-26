package mapleGBP.dao.guild

import mapleGBP.model.Guild
import mapleGBP.model.World

interface GuildDao {
    fun addGuild(name: String, world: World): Guild

    fun addGuild(guild: Guild): Guild

    fun getGuild(name: String, world: World): Guild

    fun getAllGuilds(): List<Guild>

    fun deleteGuild(name: String, world: World): Guild

    fun deleteGuild(guild: Guild): Guild
}