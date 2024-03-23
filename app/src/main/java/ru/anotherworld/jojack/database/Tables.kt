package ru.anotherworld.jojack.database

import org.jetbrains.exposed.sql.Table

object MainTable : Table("main"){
    val id = integer("id").autoIncrement()
    val login = varchar("login", 64)
    val token = varchar("token", 64)
    val trustLevel = integer("trustLevel")
    val job = integer("job")
    val serverId = integer("serverId")
    val privacy = bool("privacy")
    val icon = varchar("icon", 256)
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
    val privacy: Boolean,
    val icon: String,
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
    val chat = varchar("chat", 128)  //Ссылка на чат
    val name = varchar("name", 128) //Имя чата
    val users = varchar("users", 32768) //Списиок пользователей
    val icon = varchar("icon", 128) //Ссылка на изображение

    override val primaryKey = PrimaryKey(id)
}

object NotificationsTable : Table("notifications"){
    val id = integer("id").autoIncrement()
    val label = varchar("label", 128)
    val text = varchar("text", 8192)
    val read = bool("read")
    val action = varchar("action", 128)

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

data class NotificationData(
    val label: String,
    val text: String,
    val read: Boolean,
    val action: String
)

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

data class Comments(
    val id: Int,
    val author: String,
    val text: String,
    val likes: Int,
    val time: Long,
    val answer: String = ""
)