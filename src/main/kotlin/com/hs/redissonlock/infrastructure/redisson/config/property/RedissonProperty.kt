package com.hs.redissonlock.infrastructure.redisson.config.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(value = "spring.redisson")
data class RedissonProperty(
    val mode: String,
    val nodes: List<String>,
)
