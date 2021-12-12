package mapleGBP.model

import mapleGBP.model.converter.WorldConverter
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(uniqueConstraints =
    arrayOf(UniqueConstraint(name = "guild_name_in_world", columnNames = arrayOf("guildName", "world")))
)
class Guild(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    val gid: Int = 0,

    @OneToMany(mappedBy = "guild", fetch = FetchType.EAGER)
    val users: List<User> = listOf(),

    @Column
    val guildName: String = "",

    @Convert(converter = WorldConverter::class)
    val world: World = World.NONE,

    @Column
    val createdAt: LocalDateTime = LocalDateTime.MIN,

    @Column
    val updatedAt: LocalDateTime = LocalDateTime.MIN
) {
    override fun toString(): String {
        return "Guild(guildName='$guildName', world=$world, createdAt=$createdAt, updatedAt=$updatedAt)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Guild

        if (guildName != other.guildName) return false
        if (world != other.world) return false
        if (createdAt != other.createdAt) return false
        if (updatedAt != other.updatedAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = guildName.hashCode()
        result = 31 * result + world.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + updatedAt.hashCode()
        return result
    }
}