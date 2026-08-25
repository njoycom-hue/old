package com.dunoetoktok.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dunoetoktok.app.data.local.entity.GameResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameResultDao {

    @Insert
    suspend fun insert(result: GameResultEntity)

    @Query("SELECT MIN(score) FROM game_results WHERE gameType = :gameType")
    fun observeMinScore(gameType: String): Flow<Int?>

    @Query("SELECT MAX(score) FROM game_results WHERE gameType = :gameType")
    fun observeMaxScore(gameType: String): Flow<Int?>

    @Query("SELECT * FROM game_results WHERE gameType = :gameType ORDER BY playedAt DESC LIMIT :limit")
    fun observeRecentResults(gameType: String, limit: Int = 20): Flow<List<GameResultEntity>>

    @Query("SELECT DISTINCT playedAt FROM game_results ORDER BY playedAt DESC")
    fun observeAllPlayedAt(): Flow<List<Long>>
}
