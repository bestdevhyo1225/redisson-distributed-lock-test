package com.hs.redissonlock.domain.repository

import com.hs.redissonlock.domain.entity.Point
import org.springframework.data.jpa.repository.JpaRepository

interface PointRepository : JpaRepository<Point, Long>
