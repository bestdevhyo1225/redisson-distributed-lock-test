package com.hs.redissonlock.domain.entity

enum class PointCode(
    private val description: String,
    private val validDays: Int
) {
    REWARD_ORDER("주문 보상 포인트", 14);

    companion object {
        fun convertFromStringToEnum(value: String): PointCode {
            try {
                return valueOf(value)
            } catch (exception: Exception) {
                throw IllegalArgumentException(exception.localizedMessage)
            }
        }
    }
}
