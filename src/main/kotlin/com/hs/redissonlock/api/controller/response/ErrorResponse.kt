package com.hs.redissonlock.api.controller.response

data class ErrorResponse(
    val status: String = "error",
    val message: String,
)
