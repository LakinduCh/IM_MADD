package com.example.triponai.data.dao

import androidx.room.*
import com.example.triponai.data.entity.TripEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM trips WHERE userUid = :userUid ORDER BY startDate ASC")
    fun getTripsForUser(userUid: String): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE id = :tripId")
    suspend fun getTripById(tripId: Long): TripEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity): Long

    @Delete
    suspend fun deleteTrip(trip: TripEntity)
}
