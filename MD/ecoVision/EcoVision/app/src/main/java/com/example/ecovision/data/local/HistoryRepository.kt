package com.example.ecovision.data.local

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HistoryRepository(context: Context) {

    private val historyDao = HistoryDatabase.getDatabase(context).historyDao()

    suspend fun addHistoryItem(historyItem: HistoryEntity) {
        withContext(Dispatchers.IO) {
            historyDao.insert(historyItem)
        }
    }

    suspend fun getAllHistoryItems(): List<HistoryEntity> {
        return withContext(Dispatchers.IO) {
            historyDao.getAllHistoryItems()
        }
    }

    suspend fun getLimitedHistoryItems(limit: Int): List<HistoryEntity> {
        return withContext(Dispatchers.IO) {
            historyDao.getLimitedHistoryItems(limit)
        }
    }

    suspend fun deleteHistoryItem(historyItem: HistoryEntity) {
        withContext(Dispatchers.IO) {
            historyDao.deleteHistoryItem(historyItem.id)
        }
    }

    suspend fun updateHistoryDescription(historyItem: HistoryEntity, newDescription: String) {
        withContext(Dispatchers.IO) {
            historyDao.updateHistoryDescription(historyItem.id, newDescription)
        }
    }
}