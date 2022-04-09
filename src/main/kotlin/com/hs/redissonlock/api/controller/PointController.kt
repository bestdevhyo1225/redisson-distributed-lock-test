package com.hs.redissonlock.api.controller

import com.hs.redissonlock.api.controller.request.CreatePointRequest
import com.hs.redissonlock.api.controller.response.SuccessResponse
import com.hs.redissonlock.api.service.PointService
import com.hs.redissonlock.api.service.dto.CreatePointDto
import com.hs.redissonlock.api.service.dto.CreatePointResultDto
import com.hs.redissonlock.api.service.dto.FindPageableResultDto
import com.hs.redissonlock.api.service.dto.FindPointResultDto
import com.hs.redissonlock.api.service.dto.FindPointTotalResultDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/points")
class PointController(
    private val pointService: PointService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createPoint(@RequestBody @Valid request: CreatePointRequest): ResponseEntity<SuccessResponse<CreatePointResultDto>> {
        val serviceDto = CreatePointDto(memberId = request.memberId, code = request.code, amounts = request.amounts)
        val serviceResultDto = pointService.createPoint(serviceDto = serviceDto)
        return ResponseEntity.ok(SuccessResponse(data = serviceResultDto))
    }

    @GetMapping("/{id}")
    fun findPointById(@PathVariable id: Long): ResponseEntity<SuccessResponse<FindPointResultDto>> {
        val serviceResultDto = pointService.findPoint(id = id)
        return ResponseEntity.ok(SuccessResponse(data = serviceResultDto))
    }

    @GetMapping("/total/{memberId}")
    fun findPointTotalByMemberId(@PathVariable memberId: Long): ResponseEntity<SuccessResponse<FindPointTotalResultDto>> {
        val serviceResultDto = pointService.findPointTotal(memberId = memberId)
        return ResponseEntity.ok(SuccessResponse(data = serviceResultDto))
    }

    @GetMapping("/history/{memberId}")
    fun findPointsHistoryByMemberId(
        @PathVariable memberId: Long,
        @RequestParam(defaultValue = "0") start: Int,
        @RequestParam(defaultValue = "5") count: Int
    ): ResponseEntity<SuccessResponse<FindPageableResultDto<FindPointResultDto>>> {
        val serviceResultDto = pointService.findPointsByPageable(memberId = memberId, start = start, count = count)
        return ResponseEntity.ok(SuccessResponse(data = serviceResultDto))
    }
}
