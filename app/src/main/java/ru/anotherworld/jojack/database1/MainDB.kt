package ru.anotherworld.jojack.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//If you must create new colum in previous database, just use migration
@Database(entities = [Item::class], version = 1)
abstract class MainDB: RoomDatabase() {
    abstract fun getDao(): Dao
    companion object{
        fun getDb(context: Context) : MainDB{
            return Room.databaseBuilder(
                context.applicationContext,
                MainDB::class.java,
                "database.db").build()
        }
    }
}