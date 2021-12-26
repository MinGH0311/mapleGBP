package mapleGBP.model

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(uniqueConstraints = [UniqueConstraint(name = "party_name", columnNames = ["partyName"])],
        indexes = [Index(name = "party_name_idx", columnList = "partyName", unique = true)])
class Party(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    val pid: Int = 0,

    @Column
    val partyName: String = "",

    @Column
    var createdAt: LocalDateTime? = null,

    @Column
    var updatedAt: LocalDateTime? = null
) {
    override fun toString(): String {
        return "Party(pid=$pid, partyName='$partyName', createdAt=$createdAt, updatedAt=$updatedAt)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Party

        if (partyName != other.partyName) return false
        if (createdAt != other.createdAt) return false
        if (updatedAt != other.updatedAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = partyName.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + updatedAt.hashCode()
        return result
    }
}