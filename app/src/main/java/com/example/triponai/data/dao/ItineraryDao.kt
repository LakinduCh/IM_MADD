package com.example.triponai.data.dao

import androidx.room.*
import com.example.triponai.data.entity.ItineraryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItineraryDao {
    @Query("SELECT * FROM itineraries WHERE tripId = :tripId ORDER BY dayNumber ASC")
    fun getItineraryForTrip(tripId: Long): Flow<List<ItineraryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItinerary(itinerary: ItineraryEntity)

    @Query("DELETE FROM itineraries WHERE tripId = :tripId")
    suspend fun deleteItineraryForTrip(tripId: Long)
}
