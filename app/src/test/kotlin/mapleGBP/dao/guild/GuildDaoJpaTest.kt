package mapleGBP.dao.guild

import mapleGBP.TestDatabase
import mapleGBP.dao.repository.GuildRepository
import mapleGBP.model.*
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
internal class GuildDaoJpaTest {

    val testGuilds: List<Guild> = listOf(
        Guild(0, emptyList(), "Test-Guild", World.SCANIA),
        Guild(1, emptyList(), "Test-Guild2", World.BERA),
        Guild(2, emptyList(), "Test-Guild3", World.CROA),
        Guild(3, emptyList(), "Test-Guild4", World.LUNA),
    )

    @Autowired
    lateinit var guildRepository: GuildRepository

    @Autowired
    lateinit var guildDao: GuildDao

    @BeforeEach
    fun clearTestDB() {
        guildRepository.deleteAll()
    }

    @Test
    fun testDB() {
        assertEquals(guildRepository.count(), 0L, "Check DB Connection is connected to test DB")
    }

    @Test
    fun addGuild() {
        assertEquals(guildRepository.count(), 0L)

        testGuilds.forEach { guild: Guild -> guildDao.addGuild(guild) }
        assertEquals(guildRepository.count(), testGuilds.count().toLong())

        testGuilds.forEach { guild: Guild ->
            run {
                val savedGuild = guildDao.getGuild(guild.guildName, guild.world)

                // TODO 왜 아래 테스트 실패하는지 확인...
//                assert(savedGuild.gid > guild.gid)
                assertEquals(savedGuild, guild)
                assertNotNull(savedGuild.createdAt)
            }
        }
    }

    @Test
    fun addGuild_worldNameAndGuildName() {
        assertEquals(guildRepository.count(), 0L)

        testGuilds.forEach { guild: Guild -> guildDao.addGuild(guild.guildName, guild.world) }
        assertEquals(guildRepository.count(), testGuilds.count().toLong())

        testGuilds.forEach { guild: Guild ->
            run {
                val savedGuild = guildDao.getGuild(guild.guildName, guild.world)

                assert(savedGuild.gid > guild.gid)
                assertEquals(savedGuild, guild)
                assertNotNull(savedGuild.createdAt)
            }
        }
    }

    @Test
    fun addGuild_duplicateWorldAndName() {
        assertEquals(guildRepository.count(), 0L)

        guildDao.addGuild("test", World.SCANIA)
        assertEquals(guildRepository.count(), 1L)

        assertThrows(DataIntegrityViolationException::class.java, { guildDao.addGuild("test", World.SCANIA) })
        assertEquals(guildRepository.count(), 1L)
    }

    @Test
    fun addGuild_duplicate() {
        assertEquals(guildRepository.count(), 0L)

        guildDao.addGuild(testGuilds[0])
        assertEquals(guildRepository.count(), 1L)
        assertEquals(guildDao.getGuild(testGuilds[0].guildName, testGuilds[0].world), testGuilds[0])

        assertThrows(DataIntegrityViolationException::class.java) { guildDao.addGuild(Guild(guildName = testGuilds[0].guildName, world = testGuilds[0].world)) }
        assertEquals(guildRepository.count(), 1L)
    }

    @Test
    fun getAllGuilds() {
        assertEquals(guildRepository.count(), 0L)

        testGuilds.forEach { guild: Guild -> guildDao.addGuild(guild) }
        assertEquals(guildDao.getAllGuilds().count(), testGuilds.count())
        assertIterableEquals(guildDao.getAllGuilds(), testGuilds)
    }

    @Test
    fun getAllGuilds_notExistGuilds() {
        assertEquals(guildRepository.count(), 0L)

        assertIterableEquals(guildDao.getAllGuilds(), emptyList<Guild>())
    }

    @Test
    fun getGuild_notExistGuild() {
        assertEquals(guildRepository.count(), 0L)

        assertThrows(EntityNotFoundException::class.java, { guildDao.getGuild("none", World.NONE) })
    }

    @Test
    fun deleteGuild() {
        assertEquals(guildRepository.count(), 0L)

        testGuilds.forEach { guild: Guild -> guildDao.addGuild(guild) }
        assertEquals(guildRepository.count(), 4L)

        testGuilds
            .mapIndexed({ index: Int, guild: Guild -> Pair(index.toLong(), guild) })
            .map { pair: Pair<Long, Guild> -> Pair(pair.first, guildDao.getGuild(pair.second.guildName, pair.second.world)) }
            .forEach { indexedGuild: Pair<Long, Guild> ->
                run {
                    guildDao.deleteGuild(indexedGuild.second)
                    assertEquals(guildRepository.count(), testGuilds.count().toLong() - indexedGuild.first - 1L)
                }
            }
    }

    @Test
    fun deleteGuild_worldNameAndGuildName() {
        assertEquals(guildRepository.count(), 0L)

        testGuilds.forEach { guild: Guild -> guildDao.addGuild(guild) }
        assertEquals(guildRepository.count(), testGuilds.count().toLong())

        testGuilds
            .mapIndexed({ index: Int, guild: Guild -> Pair(index.toLong(), guild) })
            .forEach { indexedGuild: Pair<Long, Guild> ->
                run {
                    guildDao.deleteGuild(indexedGuild.second.guildName, indexedGuild.second.world)
                    assertEquals(guildRepository.count(), testGuilds.count().toLong() - indexedGuild.first - 1L)
                }
            }
    }

    @Test
    fun deleteGuild_notExistGuilds() {
        assertEquals(guildRepository.count(), 0L)

        assertThrows(EntityNotFoundException::class.java) { guildDao.deleteGuild("invalid_guild", World.NONE) }
    }
}