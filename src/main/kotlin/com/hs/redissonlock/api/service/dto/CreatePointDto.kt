package com.hs.redissonlock.api.service.dto

data class CreatePointDto(
    val memberId: Long,
    val code: String,
    val amounts: Int,
)
