package com.hs.redissonlock.domain.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class PointTotal(
    memberId: Long,
    currentAmounts: Int = 0,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Column(nullable = false)
    var memberId: Long = memberId
        protected set

    @Column(nullable = false)
    var currentAmounts: Int = currentAmounts
        protected set

    fun changeCurrentAmounts(value: Int) {
        currentAmounts = value
    }
}
