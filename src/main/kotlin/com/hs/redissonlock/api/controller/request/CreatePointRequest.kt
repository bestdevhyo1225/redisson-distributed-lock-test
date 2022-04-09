package com.hs.redissonlock.api.controller.request

import com.hs.redissonlock.common.annotations.DistributedLockUniqueKey
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive

data class CreatePointRequest(

    @field:Positive(message = "memberId는 0보다 큰 값이어야 합니다.")
    @DistributedLockUniqueKey
    val memberId: Long,

    @field:NotBlank(message = "code는 반드시 입력해야 합니다.")
    val code: String,

    @field:NotNull(message = "amounts는 반드시 입력해야 합니다.")
    val amounts: Int,
)
