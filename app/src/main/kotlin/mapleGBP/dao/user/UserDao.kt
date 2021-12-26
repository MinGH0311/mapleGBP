package mapleGBP.dao.user

import mapleGBP.model.Guild
import mapleGBP.model.User

interface UserDao {
    fun addUser(user: User): User

    fun getUser(uid: Int): User

    fun getUser(name: String): User

    fun getAllUsers(): List<User>

    fun getUsersWithGuild(guild: Guild): List<User>

    fun updateUser(user: User): User

    fun deleteUser(uid: Int): User
}