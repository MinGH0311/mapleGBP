package mapleGBP.model.dto

data class PartyInfo(
    val name: String,
    val members: List<PartyMember>
) {
    data class PartyMember(
        val nickname: String,
        val image: String,
        val `class`: String,
        val level: Int
    )
}
