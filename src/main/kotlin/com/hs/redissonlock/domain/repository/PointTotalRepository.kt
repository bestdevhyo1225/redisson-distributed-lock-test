package com.hs.redissonlock.domain.repository

import com.hs.redissonlock.domain.entity.PointTotal
import org.springframework.data.jpa.repository.JpaRepository

interface PointTotalRepository : JpaRepository<PointTotal, Long> {

    fun findByMemberId(memberId: Long): PointTotal?
}
