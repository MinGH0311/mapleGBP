package mapleGBP.model.dto

import mapleGBP.model.World

data class GuildInfo(
    val name: String,
    val world: World,
    val members: List<GuildMember>
) {
    data class GuildMember(
        val nickname: String,
        val image: String,
        val `class`: String,
        val level: Int
    )
}
