package com.hs.redissonlock.api.service.dto

data class FindPointResultDto(
    val pointId: Long,
    val memberId: Long,
    val code: String,
    val amounts: Int,
    val currentAmounts: Int,
)
