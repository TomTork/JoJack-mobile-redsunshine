package ru.anotherworld.jojack.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class Item (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Identity")
    var id: Int? = null,
    @ColumnInfo(name = "Login")
    var login: String,
    @ColumnInfo(name = "HashPassword")
    var hashPassword: String,
    @ColumnInfo(name = "ServerId")
    var serverId: Int,
    @ColumnInfo(name = "Level")
    var level: Int,
    @ColumnInfo(name = "TrustLevel")
    var trustLevel: Boolean,
    @ColumnInfo(name = "Device")
    var device: String,
    @ColumnInfo(name = "ControlSum")
    var controlSum: String,
    @ColumnInfo(name = "KeyM")
    var keyM: String,
    @ColumnInfo(name = "Info")
    var info: String
)