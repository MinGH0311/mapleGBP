package mapleGBP.model

import mapleGBP.model.converter.WorldConverter
import mapleGBP.model.dto.UserInfo
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(uniqueConstraints = arrayOf(UniqueConstraint(name = "nickname_uni", columnNames = arrayOf("nickname"))))
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    val uid: Int = 0,

    @ManyToOne
    @JoinColumn(name = "gid", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val guild: Guild? = null,

    @Column(length = 4096)
    val image: String = "",

    @Column
    val nickname: String = "",

    @Column(name = "`union`")
    val union: Int = 0,

    @Column
    val `class`: String = "",

    @Column
    val mureong: Int = 0,

    @Column
    val level: Int = 0,

    @Convert(converter = WorldConverter::class)
    val world: World = World.NONE,

    @Column
    val extras: String? = "",

    @Column
    var createdAt: LocalDateTime? = null,

    @Column
    var updatedAt: LocalDateTime? = null
) {
    fun toUserInfo(): UserInfo {
        return UserInfo(
            nickname = nickname,
            image = image,
            guild = guild ?. guildName ?: "",
            union = union,
            `class` = `class`,
            mureong = mureong,
            level = level,
            world = world,
            extras = extras ?: ""
        )
    }

    override fun toString(): String {
        return "User(uid=$uid, guild=$guild, image='$image', nickname='$nickname', union=$union, `class`='$`class`', mureong=$mureong, level=$level, world=$world, extras='$extras', createdAt=$createdAt, updatedAt=$updatedAt)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (guild != other.guild) return false
        if (image != other.image) return false
        if (nickname != other.nickname) return false
        if (union != other.union) return false
        if (`class` != other.`class`) return false
        if (mureong != other.mureong) return false
        if (level != other.level) return false
        if (world != other.world) return false
        if (extras != other.extras) return false

        return true
    }

    override fun hashCode(): Int {
        var result = guild?.hashCode() ?: 0
        result = 31 * result + image.hashCode()
        result = 31 * result + nickname.hashCode()
        result = 31 * result + union
        result = 31 * result + `class`.hashCode()
        result = 31 * result + mureong
        result = 31 * result + level
        result = 31 * result + world.hashCode()
        result = 31 * result + extras.hashCode()

        return result
    }


}