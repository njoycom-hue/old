package com.dunoetoktok.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dunoetoktok.app.data.local.dao.GameResultDao
import com.dunoetoktok.app.data.local.entity.GameResultEntity

@Database(entities = [GameResultEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameResultDao(): GameResultDao

    companion object {
        const val DATABASE_NAME = "dunoetoktok.db"
    }
}
