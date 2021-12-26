package mapleGBP.model

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(
    uniqueConstraints = [UniqueConstraint(name = "pid_uid_uni", columnNames = ["pid", "uid"])],
    indexes = [Index(name = "party_idx", columnList = "pid", unique = false), Index(name = "user_idx", columnList = "uid", unique = false)]
)
class PartyMember(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    val pmid: Int = 0,

    @Column
    val pid: Int = 0,

    @Column
    val uid: Int = 0,

    @Column
    var createdAt: LocalDateTime? = null,

    @Column
    var updatedAt: LocalDateTime? = null
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PartyMember

        if (pid != other.pid) return false
        if (uid != other.uid) return false

        return true
    }

    override fun hashCode(): Int {
        var result = pid
        result = 31 * result + uid
        return result
    }

    override fun toString(): String {
        return "PartyMember(pmid=$pmid, pid=$pid, uid=$uid, createdAt=$createdAt, updatedAt=$updatedAt)"
    }
}