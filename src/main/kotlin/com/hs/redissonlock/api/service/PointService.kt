package com.hs.redissonlock.api.service

import com.hs.redissonlock.api.service.dto.CreatePointDto
import com.hs.redissonlock.api.service.dto.CreatePointResultDto
import com.hs.redissonlock.api.service.dto.FindPageableResultDto
import com.hs.redissonlock.api.service.dto.FindPointResultDto
import com.hs.redissonlock.api.service.dto.FindPointTotalResultDto
import com.hs.redissonlock.domain.entity.Point
import com.hs.redissonlock.domain.entity.PointTotal
import com.hs.redissonlock.domain.repository.PointRepository
import com.hs.redissonlock.domain.repository.PointTotalRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PointService(
    private val pointRepository: PointRepository,
    private val pointTotalRepository: PointTotalRepository,
) {

    @Transactional
    fun createPoint(serviceDto: CreatePointDto): CreatePointResultDto {
        val pointTotal = findPointTotalByMemberId(memberId = serviceDto.memberId)
        val point = Point.newInstance(
            memberId = serviceDto.memberId,
            code = serviceDto.code,
            amounts = serviceDto.amounts,
            currentAmounts = pointTotal.currentAmounts
        )

        pointRepository.save(point)
        pointTotal.changeCurrentAmounts(value = point.currentAmounts)

        return CreatePointResultDto(pointId = point.id!!)
    }

    fun findPoint(id: Long): FindPointResultDto {
        val point = pointRepository.findByIdOrNull(id) ?: throw NoSuchElementException("Point가 존재하지 않습니다.")

        return FindPointResultDto(
            pointId = point.id!!,
            memberId = point.memberId,
            code = point.code.name,
            amounts = point.amounts,
            currentAmounts = point.currentAmounts,
        )
    }

    fun findPointTotal(memberId: Long): FindPointTotalResultDto {
        val pointTotal = findPointTotalByMemberId(memberId = memberId)

        return FindPointTotalResultDto(memberId = memberId, currentAmounts = pointTotal.currentAmounts)
    }

    fun findPointsByPageable(memberId: Long, start: Int, count: Int): FindPageableResultDto<FindPointResultDto> {
        val pageResult = pointRepository.findAll(PageRequest.of(start / count, count))

        return FindPageableResultDto(
            items = pageResult.content.map {
                FindPointResultDto(
                    pointId = it.id!!,
                    memberId = it.memberId,
                    code = it.code.name,
                    amounts = it.amounts,
                    currentAmounts = it.currentAmounts,
                )
            },
            start = start,
            count = count,
            total = pageResult.totalElements
        )
    }

    private fun findPointTotalByMemberId(memberId: Long): PointTotal {
        return pointTotalRepository.findByMemberId(memberId = memberId)
            ?: pointTotalRepository.save(PointTotal(memberId = memberId))
    }
}
