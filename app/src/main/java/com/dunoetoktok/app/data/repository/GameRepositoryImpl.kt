package com.dunoetoktok.app.data.repository

import com.dunoetoktok.app.data.local.dao.GameResultDao
import com.dunoetoktok.app.data.local.entity.GameResultEntity
import com.dunoetoktok.app.model.GameResult
import com.dunoetoktok.app.model.GameType
import com.dunoetoktok.app.model.PlayerStats
import com.dunoetoktok.app.util.ExperienceCalculator
import com.dunoetoktok.app.util.StreakCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val dao: GameResultDao,
) : GameRepository {

    override suspend fun saveResult(gameType: GameType, score: Int) {
        dao.insert(
            GameResultEntity(
                gameType = gameType.storageKey,
                score = score,
                playedAt = System.currentTimeMillis(),
            )
        )
    }

    override fun observeBestScore(gameType: GameType): Flow<Int?> =
        if (gameType.lowerScoreIsBetter) {
            dao.observeMinScore(gameType.storageKey)
        } else {
            dao.observeMaxScore(gameType.storageKey)
        }

    override fun observeRecentResults(gameType: GameType, limit: Int): Flow<List<GameResult>> =
        dao.observeRecentResults(gameType.storageKey, limit).map { entities ->
            entities.map { GameResult(gameType = gameType, score = it.score, playedAt = it.playedAt) }
        }

    override fun observePlayedDates(): Flow<List<LocalDate>> =
        dao.observeAllPlayedAt().map { timestamps ->
            timestamps
                .map { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }
                .distinct()
        }

    override fun observePlayerStats(): Flow<PlayerStats> =
        dao.observeAllResults().map { entities ->
            val totalXp = entities.sumOf { entity ->
                GameType.fromStorageKey(entity.gameType)?.let { ExperienceCalculator.xpFor(it, entity.score) } ?: 0
            }
            val bestScores = GameType.entries.associateWith { gameType ->
                val scores = entities.filter { it.gameType == gameType.storageKey }.map { it.score }
                if (scores.isEmpty()) null else if (gameType.lowerScoreIsBetter) scores.min() else scores.max()
            }
            val playedDates = entities
                .map { Instant.ofEpochMilli(it.playedAt).atZone(ZoneId.systemDefault()).toLocalDate() }
                .distinct()
            PlayerStats(
                totalGamesPlayed = entities.size,
                totalXp = totalXp,
                bestScores = bestScores,
                currentStreak = StreakCalculator.currentStreak(playedDates),
                longestStreak = StreakCalculator.longestStreak(playedDates),
            )
        }
}
