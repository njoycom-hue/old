package com.dunoetoktok.app.util

import com.dunoetoktok.app.model.GameType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExperienceCalculatorTest {

    @Test
    fun `xp is always positive for every game type`() {
        for (gameType in GameType.entries) {
            for (score in 0..12) {
                assertTrue(
                    "xp should be positive for $gameType score=$score",
                    ExperienceCalculator.xpFor(gameType, score) > 0,
                )
            }
        }
    }

    @Test
    fun `zero total xp is level 1 with nothing earned yet`() {
        val level = ExperienceCalculator.levelForTotalXp(0)
        assertEquals(1, level.level)
        assertEquals(0, level.xpIntoLevel)
    }

    @Test
    fun `earning exactly the xp needed advances exactly one level`() {
        val level1 = ExperienceCalculator.levelForTotalXp(0)
        val level2 = ExperienceCalculator.levelForTotalXp(level1.xpForNextLevel)
        assertEquals(2, level2.level)
        assertEquals(0, level2.xpIntoLevel)
    }

    @Test
    fun `more total xp never produces a lower level`() {
        var previousLevel = ExperienceCalculator.levelForTotalXp(0).level
        for (xp in 0..5000 step 50) {
            val level = ExperienceCalculator.levelForTotalXp(xp).level
            assertTrue(level >= previousLevel)
            previousLevel = level
        }
    }

    @Test
    fun `progress into a level stays within zero to one`() {
        for (xp in 0..5000 step 37) {
            val level = ExperienceCalculator.levelForTotalXp(xp)
            assertTrue(level.progress in 0f..1f)
        }
    }
}
