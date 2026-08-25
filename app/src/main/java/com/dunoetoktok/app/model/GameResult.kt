package com.dunoetoktok.app.model

data class GameResult(
    val gameType: GameType,
    val score: Int,
    val playedAt: Long,
)
