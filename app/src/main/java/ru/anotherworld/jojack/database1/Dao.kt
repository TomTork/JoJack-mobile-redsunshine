package ru.anotherworld.jojack.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {
    @Insert
    fun insertItem(item: Item)

    @Query("select * from items")
    fun getAllItems(): Flow<List<Item>>

    @Query("select Login from items where Identity=0")
    fun getLogin(): String

    @Query("select HashPassword from items where Identity=0")
    fun getHashPassword(): String

    @Query("select ServerId from items where Identity=0")
    fun getServerId(): Int

    @Query("select Level from items where Identity=0")
    fun getLevel(): Int

    @Query("select TrustLevel from items where Identity=0")
    fun getTrustLevel(): Boolean

    @Query("select Device from items where Identity=0")
    fun getDevice(): String

    @Query("select ControlSum from items where Identity=0")
    fun getControlSum(): String

    @Query("select KeyM from items where Identity=0")
    fun getKeyM(): String

    @Query("select Info from items where Identity=0")
    fun getInfo(): String

    @Update
    fun updateItem(item: Item)
}








