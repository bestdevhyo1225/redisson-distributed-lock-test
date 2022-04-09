package com.hs.redissonlock.domain.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
class Point(
    memberId: Long,
    code: PointCode,
    amounts: Int,
    currentAmounts: Int,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Column(nullable = false)
    var memberId: Long = memberId
        protected set

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var code: PointCode = code
        protected set

    @Column(nullable = false)
    var amounts: Int = amounts
        protected set

    @Column(nullable = false)
    var currentAmounts: Int = currentAmounts
        protected set

    companion object {
        fun create(memberId: Long, code: String, amounts: Int, currentAmounts: Int): Point {
            return Point(
                memberId = memberId,
                code = PointCode.convertFromStringToEnum(value = code),
                amounts = amounts,
                currentAmounts = currentAmounts.plus(amounts)
            )
        }
    }
}
