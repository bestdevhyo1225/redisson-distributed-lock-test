package com.hs.redissonlock

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RedissonlockApplication

fun main(args: Array<String>) {
	runApplication<RedissonlockApplication>(*args)
}
