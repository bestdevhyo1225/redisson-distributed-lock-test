package com.hs.redissonlock.api.controller.response

data class FailResponse(
    val status: String = "fail",
    val message: String,
)
