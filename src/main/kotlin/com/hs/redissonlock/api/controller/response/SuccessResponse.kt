package com.hs.redissonlock.api.controller.response

data class SuccessResponse<T : Any>(
    val status: String = "sucess",
    val data: T,
)
