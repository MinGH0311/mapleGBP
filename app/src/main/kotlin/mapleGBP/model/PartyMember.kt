package mapleGBP.model

import javax.persistence.*

@Entity
@Table(indexes = arrayOf(
    Index(name = "party_idx", columnList = "pid", unique = false),
    Index(name = "user_idx", columnList = "uid", unique = false)
))
class PartyMember(
    @Id
    @Column
    val pmid: Int = 0,

    @Column
    val pid: Int = 0,

    @Column
    val uid: Int = 0
) {
}