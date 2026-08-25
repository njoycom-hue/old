package com.dunoetoktok.app.util

import java.time.LocalDate

/** Counts consecutive played days, anchored at today (or yesterday, so the streak doesn't drop to 0 right after midnight). */
object StreakCalculator {

    fun currentStreak(playedDates: List<LocalDate>, today: LocalDate = LocalDate.now()): Int {
        if (playedDates.isEmpty()) return 0
        val days = playedDates.toHashSet()

        var cursor = today
        if (cursor !in days) {
            cursor = cursor.minusDays(1)
            if (cursor !in days) return 0
        }

        var streak = 0
        while (cursor in days) {
            streak++
            cursor = cursor.minusDays(1)
        }
        return streak
    }
}
