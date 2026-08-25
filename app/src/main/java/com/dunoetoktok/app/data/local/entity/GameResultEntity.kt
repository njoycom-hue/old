package com.dunoetoktok.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * One completed play of a game.
 *
 * [score] means different things per game (see [com.dunoetoktok.app.model.GameType.lowerScoreIsBetter]):
 * memory = move count (lower is better), sequence = rounds survived, math/odd_word = correct count out of 10.
 */
@Entity(tableName = "game_results")
data class GameResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val gameType: String,
    val score: Int,
    val playedAt: Long,
)
