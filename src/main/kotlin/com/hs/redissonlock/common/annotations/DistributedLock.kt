package com.hs.redissonlock.common.annotations

import java.util.concurrent.TimeUnit

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DistributedLock(
    val lockName: String = "",
    val waitTime: Long,
    val leaseTime: Long,
    val timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
)
