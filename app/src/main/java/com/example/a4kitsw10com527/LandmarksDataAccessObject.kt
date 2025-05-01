package com.example.a4kitsw10com527

import androidx.room.*

@Dao
interface LandmarksDataAccessObject {
    @Query("SELECT * FROM landmarks WHERE location=:location")
    fun getAll(location: String): List<LandmarksDataEntity>

    @Insert
    fun insert(row: LandmarksDataEntity) : Long
}