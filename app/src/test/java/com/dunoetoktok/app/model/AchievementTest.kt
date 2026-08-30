package com.dunoetoktok.app.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AchievementTest {

    @Test
    fun `every achievement id is unique`() {
        val ids = ACHIEVEMENTS.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `fresh player has no achievements unlocked`() {
        val stats = PlayerStats()
        assertTrue(ACHIEVEMENTS.none { it.isUnlocked(stats) })
    }

    @Test
    fun `first game achievement unlocks after exactly one played game`() {
        val stats = PlayerStats(totalGamesPlayed = 1)
        val achievement = ACHIEVEMENTS.first { it.id == "first_game" }
        assertTrue(achievement.isUnlocked(stats))
    }

    @Test
    fun `streak achievements read from longestStreak, not currentStreak`() {
        val stats = PlayerStats(currentStreak = 0, longestStreak = 7)
        val sevenDay = ACHIEVEMENTS.first { it.id == "streak_7" }
        val thirtyDay = ACHIEVEMENTS.first { it.id == "streak_30" }
        assertTrue(sevenDay.isUnlocked(stats))
        assertFalse(thirtyDay.isUnlocked(stats))
    }

    @Test
    fun `memory ace requires clearing all levels, not just an early one`() {
        val achievement = ACHIEVEMENTS.first { it.id == "memory_ace" }
        assertTrue(achievement.isUnlocked(PlayerStats(bestScores = mapOf(GameType.MEMORY to 5))))
        assertFalse(achievement.isUnlocked(PlayerStats(bestScores = mapOf(GameType.MEMORY to 3))))
    }

    @Test
    fun `newly unlocked only includes achievements that flipped from locked to unlocked`() {
        val before = PlayerStats(totalGamesPlayed = 0, longestStreak = 0)
        val after = PlayerStats(totalGamesPlayed = 1, longestStreak = 3)

        val newlyUnlocked = findNewlyUnlockedAchievements(before, after)

        assertTrue(newlyUnlocked.any { it.id == "first_game" })
        assertTrue(newlyUnlocked.any { it.id == "streak_3" })
        assertEquals(2, newlyUnlocked.size)
    }

    @Test
    fun `already unlocked achievements never reappear as newly unlocked`() {
        val stats = PlayerStats(totalGamesPlayed = 5)
        assertTrue(findNewlyUnlockedAchievements(stats, stats).isEmpty())
    }
}
