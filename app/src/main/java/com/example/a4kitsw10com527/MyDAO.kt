package com.example.a4kitsw10com527

import androidx.room.*

@Dao
interface MyDAO {
    @Query("SELECT * FROM points_of_interest WHERE id=:id")
    fun getById(id: Long): MyDataEntity?

    @Query("SELECT * FROM points_of_interest")
    fun getAll(): List<MyDataEntity>

    @Insert
    fun insert(row: MyDataEntity) : Long

    @Update
    fun update(row: MyDataEntity) : Int

    @Delete
    fun delete(row: MyDataEntity) : Int
}