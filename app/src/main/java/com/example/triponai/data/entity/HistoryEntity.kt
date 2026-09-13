package com.example.triponai.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trip_history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userUid: String,
    val destination: String,
    val completionDate: Long,
    val rating: Float? = null
)
