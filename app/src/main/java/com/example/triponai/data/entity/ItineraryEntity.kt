package com.example.triponai.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "itineraries")
data class ItineraryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tripId: Long,
    val dayNumber: Int,
    val activities: String, // Stored as JSON or simple text for now
    val hotelInfo: String? = null
)
