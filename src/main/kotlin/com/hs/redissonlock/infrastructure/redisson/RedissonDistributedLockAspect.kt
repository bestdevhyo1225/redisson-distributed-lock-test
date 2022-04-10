package com.hs.redissonlock.infrastructure.redisson

import com.hs.redissonlock.common.annotations.DistributedLock
import com.hs.redissonlock.common.annotations.DistributedLockUniqueKey
import com.hs.redissonlock.infrastructure.redisson.config.RedissonClientConfig
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.ObjectUtils
import java.lang.reflect.Field
import java.util.concurrent.TimeUnit

@Aspect
@Component // 다른 분산 락 Aspect를 사용하는 경우, 반드시 주석 처리해야 함
class RedissonDistributedLockAspect(private val redissonClient: RedissonClient) {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val uniqueLockMemberIdField = "memberId"

    @Around("@annotation(distributedLock)")
    fun <T : Any> executeWithLock(joinPoint: ProceedingJoinPoint, distributedLock: DistributedLock): T {
        val lockName = getLockName(joinPoint = joinPoint)
        val rLock = getLock(lockName = lockName)

        tryLock(
            rLock = rLock,
            lockName = lockName,
            waitTime = distributedLock.waitTime,
            leaseTime = distributedLock.leaseTime,
            timeUnit = distributedLock.timeUnit
        )

        try {
            @Suppress("UNCHECKED_CAST")
            return joinPoint.proceed() as T
        } finally {
            releaseLock(rLock = rLock, lockName = lockName)
        }
    }

    private fun getLockName(joinPoint: ProceedingJoinPoint): String {
        if (ObjectUtils.isEmpty(joinPoint.args)) {
            throw IllegalArgumentException("적용하려는 메서드의 인자가 존재하지 않습니다.")
        }

        return RedissonClientConfig.LOCK_NAME_PREFIX + getMemberId(joinPoint = joinPoint)
    }

    private fun getMemberId(joinPoint: ProceedingJoinPoint): Any {
        joinPoint.args.forEach { arg ->
            val field = getDeclaredMemberIdField(arg = arg)
            field.getAnnotation(DistributedLockUniqueKey::class.java)?.let {
                field.isAccessible = true
                return field.get(arg)
            }
        }

        throw RuntimeException("memberId 필드에 @DistributedLockUniqueKey을 설정해주세요.")
    }

    private fun getDeclaredMemberIdField(arg: Any): Field {
        try {
            return arg.javaClass.getDeclaredField(uniqueLockMemberIdField)
        } catch (exception: NoSuchFieldException) {
            throw IllegalAccessException("memberId 필드가 존재하지 않아서 접근할 수 없습니다.")
        }
    }

    private fun getLock(lockName: String): RLock {
        logger.info("getLock (key = {})", lockName)

        return redissonClient.getLock(lockName)
    }

    private fun tryLock(rLock: RLock, lockName: String, waitTime: Long, leaseTime: Long, timeUnit: TimeUnit) {
        val isAcquiredLock: Boolean

        try {
            isAcquiredLock = rLock.tryLock(waitTime, leaseTime, timeUnit)
        } catch (exception: InterruptedException) {
            throw RuntimeException("잠금을 획득하는 과정에서 예외로 인해 작업이 중단되었습니다.")
        }

        if (!isAcquiredLock) {
            throw RuntimeException("일시적으로 작업을 수행할 수 없습니다. 잠시 후에 다시 시도해주세요.")
        }

        logger.info("tryLock (key = {})", lockName)
    }

    private fun releaseLock(rLock: RLock, lockName: String) {
        if (rLock.isLocked && rLock.isHeldByCurrentThread) {
            logger.info("releaseLock (key = {})", lockName)
            return rLock.unlock()
        }

        logger.info("Already releaseLock (key = {})", lockName)
    }
}
