package com.example.triponai.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userUid: String,
    val destination: String,
    val startDate: Long,
    val endDate: Long,
    val tripType: String,
    val travelGroup: String,
    val budget: Double = 0.0,
    val travelers: Int = 1,
    val aiResponseJson: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
