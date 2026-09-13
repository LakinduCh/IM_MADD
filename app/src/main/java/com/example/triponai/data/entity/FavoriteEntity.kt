package com.example.triponai.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userUid: String,
    val locationName: String,
    val description: String? = null,
    val addedAt: Long = System.currentTimeMillis()
)
