package ru.anotherworld.jojack.database

import org.jetbrains.exposed.sql.Table

object MainTable : Table("main"){
    val id = integer("id").autoIncrement()
    val login = varchar("login", 64)
    val token = varchar("token", 64)
    val trustLevel = integer("trustLevel")
    val job = integer("job")
    val serverId = integer("serverId")
    val closedKey = varchar("closedKey", 2048)
    val openedKey = varchar("openedKey", 2048)
    val controlSum = varchar("controlSum", 512)
    val device = varchar("device", 32)
    val info = varchar("info", 256)

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

data class MainData(
    val login: String,
    val token: String,
    val trustLevel: Int,
    val job: Int,
    val serverId: Int,
    val closedKey: String,
    val openedKey: String,
    val controlSum: String,
    val device: String,
    val info: String
)

object LikesTable : Table("likes"){
    val id = integer("id").autoIncrement()
    val originalUrl = varchar("originalUrl", 128)
    val liked = bool("liked")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

object ChatsTable : Table("chats"){
    val id = integer("id").autoIncrement()
    val chat = varchar("chat", 32)  //Ссылка на чат
    val name = varchar("name", 128) //Имя чата
    val users = varchar("users", 2048) //Списиок пользователей
    val icon = varchar("icon", 128) //Ссылка на изображение

    override val primaryKey = PrimaryKey(id)
}

data class ChatsData(
    val chat: String,
    val name: String,
    val users: String,
    val icon: String
)

data class LikesData(
    val originalUrl: String,
    val liked: Boolean
)