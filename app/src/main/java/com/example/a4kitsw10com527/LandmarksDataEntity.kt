package com.example.a4kitsw10com527

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="landmarks")

data class LandmarksDataEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    val type: String,
    val latitude: Double,
    val longitude: Double,
    val rooms: Int,
    val meals: Boolean
)