package mapleGBP.model

import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Party(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    val pid: Int = 0,

    @Column
    val partyName: String = "",

    @Column
    val createdAt: LocalDateTime = LocalDateTime.MIN,

    @Column
    val updatedAt: LocalDateTime = LocalDateTime.MIN
) {
    override fun toString(): String {
        return "Party(pid=$pid, partyName='$partyName', createdAt=$createdAt, updatedAt=$updatedAt)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Party

        if (pid != other.pid) return false
        if (partyName != other.partyName) return false
        if (createdAt != other.createdAt) return false
        if (updatedAt != other.updatedAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = pid
        result = 31 * result + partyName.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + updatedAt.hashCode()
        return result
    }


}