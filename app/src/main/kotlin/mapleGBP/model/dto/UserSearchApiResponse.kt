package mapleGBP.model.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class UserSearchApiResponse (
    @JsonProperty("character_image")
    val characterImage: String?,

    @JsonProperty("nickname")
    val nickname: String?,

    @JsonProperty("classes")
    val classes: String?,

    @JsonProperty("level")
    val level: Int?,

    @JsonProperty("popularity")
    val popularity: Int?,

    @JsonProperty("guild_name")
    val guildName: String?,

    @JsonProperty("world")
    val world: String?,

    @JsonProperty("rank_info")
    val rankInfo: Rank?,

    @JsonProperty("dojang_info")
    val doJangInfo: Dojang?,

    @JsonProperty("dojang_history")
    val doJangInfoHistory: DojangHistory?,

    @JsonProperty("seed_info")
    val seedInfo: Seed?,

    @JsonProperty("union_info")
    val unionInfo: Union?
) {
    data class Rank(
        @JsonProperty("total_rank")
        val totalRank: Int,

        @JsonProperty("world_rank")
        val worldRank: Int,

        @JsonProperty("total_classes_rank")
        val totalClassesRank: Int,

        @JsonProperty("world_classes_rank")
        val worldClassesRank: Int
    )

    data class Dojang(
        @JsonProperty("stage")
        val stage: Int,

        @JsonProperty("period")
        val period: Int,

        @JsonProperty("tried_level")
        val triedLevel: Int,

        @JsonProperty("tried_classes")
        val triedClasses: String,

        @JsonProperty("total_rank")
        val totalRank: Int,

        @JsonProperty("world_rank")
        val worldRank: Int,

        @JsonProperty("timestamp")
        val timestamp: String
    )

    data class DojangHistory(
        @JsonProperty("history")
        val history: List<Dojang>
    )

    data class Seed(
        @JsonProperty("stage")
        val stage: Int,

        @JsonProperty("period")
        val period: Int,

        @JsonProperty("tried_level")
        val triedLevel: Int,

        @JsonProperty("tried_classes")
        val triedClasses: String,

        @JsonProperty("total_rank")
        val totalRank: Int,

        @JsonProperty("world_rank")
        val worldRank: Int,

        @JsonProperty("timestamp")
        val timestamp: String
    )

    data class Union(
        @JsonProperty("grade")
        val grade: String,

        @JsonProperty("level")
        val level: Int,

        @JsonProperty("power")
        val power: Int,

        @JsonProperty("total_rank")
        val totalRank: Int,

        @JsonProperty("world_rank")
        val worldRank: Int,

        @JsonProperty("timestamp")
        val timestamp: String
    )
}