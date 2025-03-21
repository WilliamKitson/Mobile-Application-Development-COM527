package com.example.a4kitsw10com527

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MyDataEntity::class], version = 1, exportSchema = false)
abstract class MyDatabase: RoomDatabase() {
    abstract fun myDAO(): MyDAO

    companion object {
        private var instance: MyDatabase? = null

        fun getDatabase(ctx:Context) : MyDatabase {
            var tmpInstance = instance
            if(tmpInstance == null) {
                tmpInstance = Room.databaseBuilder(
                    ctx.applicationContext,
                    MyDatabase::class.java,
                    "MyDatabase"
                ).build()
                instance = tmpInstance
            }
            return tmpInstance
        }
    }
}