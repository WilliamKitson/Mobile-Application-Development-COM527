package com.example.a4kitsw10com527

import androidx.room.*

@Dao
interface LandmarksDataAccessObject {
    @Query("SELECT * FROM landmarks")
    fun getAll(): List<LandmarksDataEntity>

    @Insert
    fun insert(row: LandmarksDataEntity) : Long
}