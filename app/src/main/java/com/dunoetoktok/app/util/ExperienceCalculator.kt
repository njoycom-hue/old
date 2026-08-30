package com.dunoetoktok.app.util

import com.dunoetoktok.app.model.GameType
import kotlin.math.roundToInt

/** Player level derived from cumulative XP, with progress toward the next level. */
data class PlayerLevel(val level: Int, val xpIntoLevel: Int, val xpForNextLevel: Int) {
    val progress: Float
        get() = if (xpForNextLevel <= 0) 0f else xpIntoLevel.toFloat() / xpForNextLevel.toFloat()
}

/** Converts a single game result into XP and turns cumulative XP into a level. Tuned by feel, not a strict formula. */
object ExperienceCalculator {

    private const val FIRST_LEVEL_XP = 60
    private const val LEVEL_XP_GROWTH = 1.15

    fun xpFor(gameType: GameType, score: Int): Int = when (gameType) {
        GameType.MEMORY -> 10 + score * 12 // score = highest level reached, so more xp for going further
        GameType.SEQUENCE -> 8 + score * 6
        GameType.MATH, GameType.ODD_WORD -> 6 + score * 6
    }

    fun levelForTotalXp(totalXp: Int): PlayerLevel {
        var level = 1
        var xpForThisLevel = FIRST_LEVEL_XP
        var remaining = totalXp.coerceAtLeast(0)
        while (remaining >= xpForThisLevel) {
            remaining -= xpForThisLevel
            level++
            xpForThisLevel = (xpForThisLevel * LEVEL_XP_GROWTH).roundToInt()
        }
        return PlayerLevel(level = level, xpIntoLevel = remaining, xpForNextLevel = xpForThisLevel)
    }
}
