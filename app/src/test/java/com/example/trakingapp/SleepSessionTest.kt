package com.example.trakingapp

import com.example.trakingapp.data.model.EnvironmentRating
import com.example.trakingapp.data.model.SleepSession
import com.example.trakingapp.sensor.LuxCategory
import org.junit.Assert.assertEquals
import org.junit.Test

class SleepSessionTest {

    @Test
    fun testSleepDurationCalculation() {
        val start = 1700000000000L
        val end = start + (7 * 3600 * 1000L) + (30 * 60 * 1000L) // 7 hours 30 mins

        val session = SleepSession(
            startTime = start,
            endTime = end,
            targetWakeTime = "06:30",
            avgLux = 1.2f,
            maxLux = 3.5f,
            disturbanceCount = 1,
            environmentRating = "Optimal"
        )

        assertEquals(450L, session.durationMinutes)
        assertEquals("7j 30m", session.formattedDuration)
    }

    @Test
    fun testEnvironmentRatingCalculation() {
        // Optimal: < 3 lux and <= 2 disturbances
        val ratingOptimal = EnvironmentRating.calculate(avgLux = 1.0f, disturbanceCount = 1)
        assertEquals(EnvironmentRating.OPTIMAL, ratingOptimal)

        // Good: < 15 lux and <= 6 disturbances
        val ratingGood = EnvironmentRating.calculate(avgLux = 8.0f, disturbanceCount = 4)
        assertEquals(EnvironmentRating.GOOD, ratingGood)

        // Fair: < 40 lux and <= 12 disturbances
        val ratingFair = EnvironmentRating.calculate(avgLux = 25.0f, disturbanceCount = 10)
        assertEquals(EnvironmentRating.FAIR, ratingFair)

        // Needs improvement: too bright or too restless
        val ratingPoor = EnvironmentRating.calculate(avgLux = 60.0f, disturbanceCount = 20)
        assertEquals(EnvironmentRating.NEEDS_IMPROVEMENT, ratingPoor)
    }

    @Test
    fun testLuxCategory() {
        assertEquals(LuxCategory.PITCH_DARK, LuxCategory.fromLux(0.5f))
        assertEquals(LuxCategory.DIM, LuxCategory.fromLux(8.0f))
        assertEquals(LuxCategory.TOO_BRIGHT, LuxCategory.fromLux(45.0f))
    }
}
