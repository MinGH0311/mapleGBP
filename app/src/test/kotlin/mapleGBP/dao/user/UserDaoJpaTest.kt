package mapleGBP.dao.user

import mapleGBP.TestDatabase
import mapleGBP.dao.repository.GuildRepository
import mapleGBP.dao.repository.UserRepository
import mapleGBP.model.Guild
import mapleGBP.model.User
import mapleGBP.model.World
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import javax.persistence.EntityNotFoundException

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestDatabase::class])
internal class UserDaoJpaTest {
    val testGuild: Guild = Guild(0, emptyList(), "Test-Guild", World.SCANIA)

    val testUsers: List<User> = listOf(
        User(1, testGuild, "https://google.com", "test1", 100, "히어로", 30, 200, World.SCANIA),
        User(2, testGuild, "https://google.com", "test2", 1000, "히어로", 30, 200, World.SCANIA),
        User(3, null, "https://google.com", "test3", 1500, "히어로", 30, 200, World.REBOOT2),
        User(4, null, "https://google.com", "test4", 3000, "히어로", 30, 200, World.AURORA),
    )

    val duplicateNamedUsers: List<User> = listOf(
        User(1, null, "https://google.com", "test", 100, "히어로", 30, 200, World.SCANIA),
        User(2, null, "https://google.com", "test", 1000, "히어로", 30, 200, World.BERA)
    )

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var guildRepository: GuildRepository

    @Autowired
    lateinit var userDao: UserDao

    @BeforeEach
    fun clearTestDB() {
        userRepository.deleteAll()
        guildRepository.deleteAll()

        guildRepository.save(testGuild)
    }

    @Test
    fun testDB() {
        assertEquals(userRepository.count(), 0L, "Check DB Connection is connected to test DB")
    }

    @Test
    fun addUser() {
        assertEquals(userRepository.count(), 0L)

        testUsers.forEach { user: User -> userDao.addUser(user) }
        assertEquals(userRepository.count(), 4L)

        testUsers.forEach { user: User ->
            val savedUser: User = userDao.getUser(user.nickname)

            assert(savedUser.uid > user.uid)
            assertEquals(savedUser, user)
            assertNotNull(savedUser.createdAt)
        }
    }

    @Test
    fun addUser_duplicateNickname() {
        assertEquals(userRepository.count(), 0L)

        userDao.addUser(duplicateNamedUsers[0])
        assertEquals(userRepository.count(), 1L)

        assertThrows(DataIntegrityViolationException::class.java, { userDao.addUser(duplicateNamedUsers[1]) })
        assertEquals(userRepository.count(), 1L)
    }

    @Test
    fun addUser_duplicateUser() {
        assertEquals(userRepository.count(), 0L)

        userDao.addUser(testUsers[0])
        assertEquals(userRepository.count(), 1L)

        assertThrows(DataIntegrityViolationException::class.java, { userDao.addUser(testUsers[0]) })
        assertEquals(userRepository.count(), 1L)
    }

    @Test
    fun getAllUsers() {
        assertEquals(userRepository.count(), 0L)

        testUsers.forEach { user: User -> userDao.addUser(user) }
        assertEquals(userRepository.count(), 4L)

        assertIterableEquals(userDao.getAllUsers(), testUsers)
    }

    @Test
    fun getUsersWithGuild() {
        assertEquals(userRepository.count(), 0L)

        testUsers.forEach { user: User -> userDao.addUser(user) }
        assertEquals(userRepository.count(), 4L)

        val usersInGuild: List<User> = userDao.getUsersWithGuild(testGuild)
        assertEquals(usersInGuild.count(), testUsers.filter { user: User -> user.guild == testGuild }.count())
        assertEquals(usersInGuild, testUsers.filter { user: User -> user.guild == testGuild })
    }

    @Test
    fun getUser_notExist() {
        assertEquals(userRepository.count(), 0L)

        assertThrows(EntityNotFoundException::class.java, { userDao.getUser(testUsers[0].nickname) })
        assertThrows(EntityNotFoundException::class.java, { userDao.getUser(testUsers[0].uid) })
    }

    @Test
    fun updateUser() {
        val testUser = testUsers[0]
        assertEquals(userRepository.count(), 0)

        userDao.addUser(testUser)

        val savedUser: User = userDao.getUser(testUser.nickname)
        assertEquals(savedUser, testUser)
        assertEquals(userRepository.count(), 1)

        val updateUser: User = User(savedUser.uid, savedUser.guild, savedUser.image, savedUser.nickname, savedUser.union,
                                savedUser.`class`, savedUser.mureong, savedUser.level, World.RED, "", savedUser.createdAt, savedUser.updatedAt)

        val updatedUser: User = userDao.updateUser(updateUser)
        assertEquals(userDao.getUser(testUser.nickname), updateUser)
        assertEquals(userRepository.count(), 1)
        assert(updatedUser.updatedAt?.isAfter(userDao.getUser(testUser.nickname).createdAt ?: fail("created_at column is null")) ?: fail("updated_at column is null"))
    }

    @Test
    fun updateUser_notExistUser() {
        val testUser: User = testUsers[0]
        assertEquals(userRepository.count(), 0L)

        assertThrows(EntityNotFoundException::class.java, { userDao.updateUser(testUser) })
        assertEquals(userRepository.count(), 0L)
    }

    @Test
    fun updateUser_duplicateUser() {
        assertEquals(userRepository.count(), 0L)

        userDao.addUser(testUsers[0])
        userDao.addUser(testUsers[1])
        assertEquals(userRepository.count(), 2L)

        val savedUser: User = userDao.getUser(testUsers[1].nickname)
        val updateUser: User = User(savedUser.uid, savedUser.guild, savedUser.image, testUsers[0].nickname, savedUser.union,
            savedUser.`class`, savedUser.mureong, savedUser.level, World.RED, "", savedUser.createdAt, savedUser.updatedAt)

        assertThrows(Exception::class.java, { userDao.updateUser(updateUser) })
        assertEquals(userDao.getUser(testUsers[1].nickname), testUsers[1])
    }

    @Test
    fun deleteUser() {
        assertEquals(userRepository.count(), 0)

        testUsers.forEach { user: User -> userRepository.save(user) }
        assertEquals(userRepository.count(), testUsers.count().toLong())

        testUsers.mapIndexed {index: Int, user: User -> Pair(index.toLong(), user) }
            .map { indexedUser: Pair<Long, User> -> Pair(indexedUser.first, userDao.getUser(indexedUser.second.nickname)) }
            .forEach { indexedUser: Pair<Long, User> ->
            run {
                userDao.deleteUser(indexedUser.second.uid)
                assertEquals(userRepository.count(), testUsers.count().toLong() - indexedUser.first - 1L)
            }
        }

        assertEquals(userRepository.count(), 0L)
    }

    @Test
    fun deleteUser_NotExist() {
        assertEquals(userRepository.count(), 0L)

        assertThrows(EntityNotFoundException::class.java, { userDao.deleteUser(0) })
        assertEquals(userRepository.count(), 0L)
    }
}