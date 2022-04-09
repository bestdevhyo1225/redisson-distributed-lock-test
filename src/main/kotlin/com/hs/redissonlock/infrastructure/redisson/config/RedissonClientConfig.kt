package com.hs.redissonlock.infrastructure.redisson.config

import com.hs.redissonlock.infrastructure.redisson.config.property.RedissonProperty
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.client.codec.StringCodec
import org.redisson.config.ClusterServersConfig
import org.redisson.config.Config
import org.redisson.config.SingleServerConfig
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
@EnableConfigurationProperties(value = [RedissonProperty::class])
class RedissonClientConfig(private val redissonProperty: RedissonProperty) {

    companion object {
        const val LOCK_NAME_PREFIX: String = "point::"
    }

    @Bean
    fun redissonClient(): RedissonClient {
        val config: Config = Config().setCodec(StringCodec.INSTANCE)

        when (RedissonMode.valueOf(redissonProperty.mode.uppercase(Locale.getDefault()))) {
            RedissonMode.SINGLE -> getSingleServerConfig(config)
            RedissonMode.CLUSTER -> getClusterServersConfig(config)
        }

        return Redisson.create(config)
    }

    private fun getSingleServerConfig(config: Config) {
        val singleServerConfig: SingleServerConfig = config.useSingleServer()
        singleServerConfig.address = redissonProperty.nodes.first()
    }

    private fun getClusterServersConfig(config: Config) {
        val clusterServersConfig: ClusterServersConfig = config.useClusterServers()
        redissonProperty.nodes.forEach { node -> clusterServersConfig.addNodeAddress(node) }
    }
}
