package com.hs.redissonlock.api.service.dto

data class FindPageableResultDto<T>(
    val items: List<T>,
    val start: Int,
    val count: Int,
    val total: Long,
)
