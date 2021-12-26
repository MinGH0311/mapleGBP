package mapleGBP.dao.user

import mapleGBP.dao.repository.UserRepository
import mapleGBP.model.Guild
import mapleGBP.model.User
import org.springframework.jdbc.BadSqlGrammarException
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import javax.persistence.EntityNotFoundException
import javax.persistence.PersistenceException

@Component
class UserDaoJpa(var userRepository: UserRepository): UserDao {

    override fun addUser(user: User): User {
        user.createdAt = LocalDateTime.now()
        return userRepository.save(user)
    }

    override fun getUser(uid: Int): User {
        return userRepository.findById(uid).orElseThrow({ EntityNotFoundException("User not found with uid=${uid}") })
    }

    override fun getUser(name: String): User {
        return userRepository.findByNickname(name).orElseThrow({ EntityNotFoundException("User not found with nickname=${name}") })
    }

    override fun getAllUsers(): List<User> {
        return userRepository.findAll()
    }

    override fun getUsersWithGuild(guild: Guild): List<User> {
        return userRepository.findAllByGuild(guild)
    }

    override fun updateUser(user: User): User {
        if (userRepository.existsById(user.uid)) {
            user.updatedAt = LocalDateTime.now()
            return userRepository.save(user)
        } else {
            throw EntityNotFoundException("No exist user")
        }
    }

    override fun deleteUser(uid: Int): User {
        val user: User = userRepository.findById(uid).orElseThrow({ EntityNotFoundException("User not found with uid=${uid}") })
        userRepository.delete(user)

        if (userRepository.existsById(uid)) {
            throw PersistenceException("Failed to delete user with uid=${uid}")
        } else {
            return user
        }
    }
}