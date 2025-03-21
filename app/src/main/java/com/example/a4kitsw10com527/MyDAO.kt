package com.example.a4kitsw10com527

import androidx.room.*

@Dao
interface MyDAO {
    @Query("SELECT * FROM points_of_interest WHERE id=:id")
    fun getById(id: Long): DataEntity?

    @Query("SELECT * FROM points_of_interest")
    fun getAll(): List<DataEntity>

    @Insert
    fun insert(row: DataEntity) : Long

    @Update
    fun update(row: DataEntity) : Int

    @Delete
    fun delete(row: DataEntity) : Int
}