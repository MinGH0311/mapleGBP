package mapleGBP.model.dto

import mapleGBP.model.World

data class UserInfo(
    val nickname: String,
    val image: String,
    val guild: String,
    val union: Int,
    val `class`: String,
    val mureong: Int,
    val level: Int,
    val world: World,
    val extras: String
)
