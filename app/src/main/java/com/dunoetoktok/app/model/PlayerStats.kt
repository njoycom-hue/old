package com.dunoetoktok.app.model

/** Aggregate stats across every game ever played, used for the level/XP display and achievement unlocking. */
data class PlayerStats(
    val totalGamesPlayed: Int = 0,
    val totalXp: Int = 0,
    val bestScores: Map<GameType, Int?> = emptyMap(),
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
)
