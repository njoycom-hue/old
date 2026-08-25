package com.dunoetoktok.app.data.repository

import com.dunoetoktok.app.model.GameResult
import com.dunoetoktok.app.model.GameType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface GameRepository {
    suspend fun saveResult(gameType: GameType, score: Int)

    /** Best score for [gameType], respecting whether a lower or higher score wins. Null if never played. */
    fun observeBestScore(gameType: GameType): Flow<Int?>

    fun observeRecentResults(gameType: GameType, limit: Int = 20): Flow<List<GameResult>>

    /** Distinct calendar dates (device-local) the user completed any game, newest first. */
    fun observePlayedDates(): Flow<List<LocalDate>>
}
