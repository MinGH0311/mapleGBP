package mapleGBP.dao.repository

import mapleGBP.model.PartyMember
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PartyMemberRepository: JpaRepository<PartyMember, Int> {

    fun findAllByUid(uid: Int): List<PartyMember>

    fun findAllByPid(pid: Int): List<PartyMember>

    fun findByPidAndUid(pid: Int, uid: Int): Optional<PartyMember>
}