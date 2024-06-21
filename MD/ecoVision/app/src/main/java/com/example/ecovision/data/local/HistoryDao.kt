package com.example.ecovision.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HistoryDao {
    @Insert
    suspend fun insert(historyItem: HistoryEntity)

    @Query("SELECT * FROM history_table ORDER BY id DESC")
    suspend fun getAllHistoryItems(): List<HistoryEntity>

    @Query("SELECT * FROM history_table ORDER BY id DESC LIMIT :limit")
    suspend fun getLimitedHistoryItems(limit: Int): List<HistoryEntity>

    @Query("DELETE FROM history_table WHERE id = :id")
    suspend fun deleteHistoryItem(id: Int)

    @Query("UPDATE history_table SET description = :description WHERE id = :id")
    suspend fun updateHistoryDescription(id: Int, description: String)
}