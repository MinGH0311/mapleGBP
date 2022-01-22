package mapleGBP.model.dto

import com.fasterxml.jackson.annotation.JsonProperty
import mapleGBP.model.World

data class GuildSearchApiResponse(
    @JsonProperty("name")
    val name: String,

    @JsonProperty("world")
    val world: World,

    @JsonProperty("member")
    val member: List<GuildMember>
    ) {
    data class GuildMember(
        @JsonProperty("character_image")
        val characterImage: String,

        @JsonProperty("nickname")
        val nickname: String,

        @JsonProperty("classes")
        val classes: String,

        @JsonProperty("level")
        val level: Int
    )

    fun toGuildInfo(): GuildInfo {
        return GuildInfo(
            name = name,
            world = world,
            members = member.map {
                    member: GuildMember -> GuildInfo.GuildMember(
                        nickname = member.nickname,
                        image = member.characterImage,
                        `class` = member.classes,
                        level = member.level
                    )
            }
        )
    }
}
