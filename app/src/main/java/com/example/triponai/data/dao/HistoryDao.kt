package com.example.triponai.data.dao

import androidx.room.*
import com.example.triponai.data.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM trip_history WHERE userUid = :userUid ORDER BY completionDate DESC")
    fun getHistoryForUser(userUid: String): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity)
}
