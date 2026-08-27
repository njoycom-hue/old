package com.dunoetoktok.app.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class StreakCalculatorTest {

    private val today = LocalDate.of(2026, 8, 25)

    @Test
    fun `no history means zero streak`() {
        assertEquals(0, StreakCalculator.currentStreak(emptyList(), today))
    }

    @Test
    fun `counts consecutive days ending today`() {
        val days = listOf(today, today.minusDays(1), today.minusDays(2))
        assertEquals(3, StreakCalculator.currentStreak(days, today))
    }

    @Test
    fun `a gap stops the streak count`() {
        val days = listOf(today, today.minusDays(1), today.minusDays(3))
        assertEquals(2, StreakCalculator.currentStreak(days, today))
    }

    @Test
    fun `streak still counts if today is not played yet but yesterday was`() {
        val days = listOf(today.minusDays(1), today.minusDays(2))
        assertEquals(2, StreakCalculator.currentStreak(days, today))
    }

    @Test
    fun `streak resets once two days pass without play`() {
        val days = listOf(today.minusDays(2), today.minusDays(3))
        assertEquals(0, StreakCalculator.currentStreak(days, today))
    }

    @Test
    fun `order of input dates does not matter`() {
        val days = listOf(today.minusDays(2), today, today.minusDays(1))
        assertEquals(3, StreakCalculator.currentStreak(days, today))
    }

    @Test
    fun `longest streak is zero with no history`() {
        assertEquals(0, StreakCalculator.longestStreak(emptyList()))
    }

    @Test
    fun `longest streak finds the best run even if it is not the current one`() {
        // a 4-day run earlier, then a break, then a shorter 2-day run
        val days = listOf(
            today.minusDays(20), today.minusDays(19), today.minusDays(18), today.minusDays(17),
            today.minusDays(1), today,
        )
        assertEquals(4, StreakCalculator.longestStreak(days))
    }

    @Test
    fun `longest streak ignores duplicate dates`() {
        val days = listOf(today, today, today.minusDays(1))
        assertEquals(2, StreakCalculator.longestStreak(days))
    }
}
