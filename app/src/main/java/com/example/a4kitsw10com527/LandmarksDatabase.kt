package com.example.a4kitsw10com527

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [LandmarksDataEntity::class], version = 1, exportSchema = false)
abstract class LandmarksDatabase: RoomDatabase() {
    abstract fun landmarksDataAccessObject(): LandmarksDataAccessObject

    companion object {
        private var instance: LandmarksDatabase? = null

        fun getDatabase(ctx:Context) : LandmarksDatabase {
            var tmpInstance = instance
            if(tmpInstance == null) {
                tmpInstance = Room.databaseBuilder(
                    ctx.applicationContext,
                    LandmarksDatabase::class.java,
                    "LandmarksDatabase"
                ).build()
                instance = tmpInstance
            }
            return tmpInstance
        }
    }
}