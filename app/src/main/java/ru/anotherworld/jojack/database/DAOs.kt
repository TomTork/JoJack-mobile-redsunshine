package ru.anotherworld.jojack.database

import android.util.Log
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

class DAOMainDatabase{
    private fun resultRowToMainDatabase(row: ResultRow) = MainData(
        login = row[MainTable.login],
        token = row[MainTable.token],
        trustLevel = row[MainTable.trustLevel],
        job = row[MainTable.job],
        serverId = row[MainTable.serverId],
        privacy = row[MainTable.privacy],
        icon = row[MainTable.icon],
        closedKey = row[MainTable.closedKey],
        openedKey = row[MainTable.openedKey],
        controlSum = row[MainTable.controlSum],
        device = row[MainTable.device],
        info = row[MainTable.info]
    )
    suspend fun addNewMainDatabase(data: MainData) = dbQuery {
        val insertStatement = MainTable.insert {
            it[login] = data.login
            it[token] = data.token
            it[trustLevel] = data.trustLevel
            it[job] = data.job
            it[serverId] = data.serverId
            it[privacy] = data.privacy
            it[icon] = data.icon
            it[closedKey] = data.closedKey
            it[openedKey] = data.openedKey
            it[controlSum] = data.controlSum
            it[device] = data.device
            it[info] = data.info
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToMainDatabase)
    }
    suspend fun editMainDatabase(id: Int, data: MainData): Boolean = dbQuery {
        MainTable.update({ MainTable.id eq id }) {
            it[login] = data.login
            it[token] = data.token
            it[trustLevel] = data.trustLevel
            it[job] = data.job
            it[serverId] = data.serverId
            it[privacy] = data.privacy
            it[icon] = data.icon
            it[closedKey] = data.closedKey
            it[openedKey] = data.openedKey
            it[controlSum] = data.controlSum
            it[device] = data.device
            it[info] = data.info
        } > 0
    }
    suspend fun deleteMainDatabase(): Boolean = dbQuery {
        MainTable.deleteAll() > 0
    }
    suspend fun getLogin(): String? = dbQuery {
        return@dbQuery MainTable
            .selectAll()
            .where { MainTable.id eq 1 }
            .map(::resultRowToMainDatabase)
            .singleOrNull()
            ?.login
    }
    suspend fun getPrivacy(): Boolean? = dbQuery {
        return@dbQuery MainTable
            .selectAll()
            .where { MainTable.id eq 1 }
            .map(::resultRowToMainDatabase)
            .singleOrNull()
            ?.privacy
    }
    suspend fun getIcon(): String? = dbQuery {
        return@dbQuery MainTable
            .selectAll()
            .where { MainTable.id eq 1 }
            .map(::resultRowToMainDatabase)
            .singleOrNull()
            ?.icon
    }
    suspend fun setPrivacy(value: Boolean): Boolean = dbQuery {
        MainTable
            .update({ MainTable.id eq 1 }) {
                it[privacy] = value
            } > 0
    }
    suspend fun setIcon(value: String): Boolean = dbQuery {
        MainTable
            .update({ MainTable.id eq 1 }) {
                it[icon] = value
            } > 0
    }
    suspend fun setLogin(value: String): Boolean = dbQuery {
        MainTable
            .update({ MainTable.id eq 1 }) {
                it[login] = value
            } > 0
    }
    suspend fun getToken(): String? = dbQuery {
        return@dbQuery MainTable
            .selectAll()
            .where { MainTable.id eq 1 }
            .map(::resultRowToMainDatabase)
            .singleOrNull()
            ?.token
    }
    suspend fun setToken(value: String): Boolean = dbQuery {
        MainTable
            .update({ MainTable.id eq 1 }) {
                it[token] = value
            } > 0
    }
    suspend fun getTrustLevel(): Int? = dbQuery {
        return@dbQuery MainTable
            .selectAll()
            .where { MainTable.id eq 1 }
            .map(::resultRowToMainDatabase)
            .singleOrNull()
            ?.trustLevel
    }
    suspend fun setTrustLevel(value: Int): Boolean = dbQuery {
        MainTable
            .update({ MainTable.id eq 1 }) {
                it[trustLevel] = value
            } > 0
    }
    suspend fun getJob(): Int? = dbQuery {
        return@dbQuery MainTable
            .selectAll()
            .where { MainTable.id eq 1 }
            .map(::resultRowToMainDatabase)
            .singleOrNull()
            ?.job
    }
    suspend fun setJob(value: Int): Boolean = dbQuery {
        MainTable
            .update({ MainTable.id eq 1 }) {
                it[job] = value
            } > 0
    }
    suspend fun getServerId(): Int? = dbQuery {
        return@dbQuery MainTable
            .selectAll()
            .where { MainTable.id eq 1 }
            .map(::resultRowToMainDatabase)
            .singleOrNull()
            ?.serverId
    }
    suspend fun setServerId(value: Int): Boolean = dbQuery {
        MainTable
            .update({ MainTable.id eq 1 }) {
                it[serverId] = value
            } > 0
    }
    suspend fun getClosedKey(): String? = dbQuery {
        return@dbQuery MainTable
            .selectAll()
            .where { MainTable.id eq 1 }
            .map(::resultRowToMainDatabase)
            .singleOrNull()
            ?.closedKey
    }
    suspend fun setClosedKey(value: String): Boolean = dbQuery {
        MainTable
            .update({ MainTable.id eq 1 }) {
                it[closedKey] = value
            } > 0
    }
    suspend fun getOpenedKey(): String? = dbQuery {
        return@dbQuery MainTable
            .selectAll()
            .where { MainTable.id eq 1 }
            .map(::resultRowToMainDatabase)
            .singleOrNull()
            ?.openedKey
    }
    suspend fun setOpenedKey(value: String): Boolean = dbQuery {
        MainTable
            .update({ MainTable.id eq 1 }) {
                it[openedKey] = value
            } > 0
    }
    suspend fun getControlSum(): String? = dbQuery {
        return@dbQuery MainTable
            .selectAll()
            .where { MainTable.id eq 1 }
            .map(::resultRowToMainDatabase)
            .singleOrNull()
            ?.controlSum
    }
    suspend fun setControlSum(value: String): Boolean = dbQuery {
        MainTable
            .update({ MainTable.id eq 1 }) {
                it[controlSum] = value
            } > 0
    }
    suspend fun getDevice(): String? = dbQuery {
        return@dbQuery MainTable
            .selectAll()
            .where { MainTable.id eq 1 }
            .map(::resultRowToMainDatabase)
            .singleOrNull()
            ?.device
    }
    suspend fun setDevice(value: String): Boolean = dbQuery {
        MainTable
            .update({ MainTable.id eq 1 }) {
                it[device] = value
            } > 0
    }
    suspend fun getInfo(): String? = dbQuery {
        return@dbQuery MainTable
            .selectAll()
            .where { MainTable.id eq 1 }
            .map(::resultRowToMainDatabase)
            .singleOrNull()
            ?.info
    }
    suspend fun setInfo(value: String): Boolean = dbQuery {
        MainTable
            .update({ MainTable.id eq 1 }) {
                it[info] = value
            } > 0
    }
}

class DAOLikesDatabase{
    private fun resultToLikesDatabase(row: ResultRow) = LikesData(
        originalUrl = row[LikesTable.originalUrl],
        liked = row[LikesTable.liked]
    )
    suspend fun addNewLikesDatabase(data: LikesData) = dbQuery{
        val insertStatement = LikesTable.insert {
            it[originalUrl] = data.originalUrl
            it[liked] = data.liked
        }
    }
    suspend fun editLikesDatabase(id: Int, data: LikesData) = dbQuery {
        LikesTable.update({ LikesTable.id eq id }) {
            it[originalUrl] = data.originalUrl
            it[liked] = data.liked
        }
    }
    suspend fun deleteLikesDatabase() = dbQuery {
        LikesTable.deleteAll()
    }
    suspend fun getLikedByOriginalUrl(originalUrl: String): Boolean? = dbQuery {
        return@dbQuery LikesTable
            .selectAll()
            .where { LikesTable.originalUrl eq originalUrl }
            .map(::resultToLikesDatabase)
            .singleOrNull()
            ?.liked
    }
    suspend fun existsData(originalUrl: String): Boolean = dbQuery {
        return@dbQuery LikesTable
            .selectAll()
            .where { LikesTable.originalUrl eq originalUrl }
            .map(::resultToLikesDatabase)
            .toList().isNotEmpty()
    }
    suspend fun setLikedByOriginalUrl(liked1: Boolean, originalUrl1: String) = dbQuery {
        LikesTable
            .update({ LikesTable.originalUrl eq originalUrl1 }) {
                it[liked] = liked1
            }
    }
}

class DAOChatsDatabase{
    private fun resultToChatsDatabase(row: ResultRow) = ChatsData(
        chat = row[ChatsTable.chat],
        name = row[ChatsTable.name],
        users = row[ChatsTable.users],
        icon = row[ChatsTable.icon]
    )
    suspend fun addNewChatsDatabase(data: ChatsData) = dbQuery{
        ChatsTable.insert {
            it[chat] = data.chat
            it[name] = data.name
            it[users] = data.users
            it[icon] = data.icon
        }
    }
    suspend fun editChatsDatabase(id: Int, data: ChatsData) = dbQuery {
        ChatsTable.update({ ChatsTable.id eq id }) {
            it[chat] = data.chat
            it[name] = data.name
            it[users] = data.users
            it[icon] = data.icon
        }
    }
    suspend fun deleteAll() = dbQuery{
        ChatsTable.deleteAll()
    }
    suspend fun delete(id: Int) = dbQuery{
        ChatsTable.deleteWhere { ChatsTable.id eq id }
    }
    suspend fun deleteByName(name: String) = dbQuery {
        ChatsTable.deleteWhere { ChatsTable.name eq name }
    }
    suspend fun getChat(id: Int): String? = dbQuery{
        return@dbQuery ChatsTable
            .selectAll()
            .where { ChatsTable.id eq id }
            .map(::resultToChatsDatabase)
            .singleOrNull()
            ?.chat
    }
    suspend fun setChat(value: String, id: Int): Boolean = dbQuery {
        return@dbQuery ChatsTable.update({ ChatsTable.id eq id }) {
            it[chat] = value
        } > 0
    }
    suspend fun getName(id: Int): String? = dbQuery{
        return@dbQuery ChatsTable
            .selectAll()
            .where { ChatsTable.id eq id }
            .map(::resultToChatsDatabase)
            .singleOrNull()
            ?.name
    }
    suspend fun setName(value: String, id: Int): Boolean = dbQuery {
        return@dbQuery ChatsTable.update({ ChatsTable.id eq id }) {
            it[name] = value
        } > 0
    }
    suspend fun getUsers(id: Int): String? = dbQuery{
        return@dbQuery ChatsTable
            .selectAll()
            .where { ChatsTable.id eq id }
            .map(::resultToChatsDatabase)
            .singleOrNull()
            ?.users
    }
    suspend fun setUsers(value: String, id: Int): Boolean = dbQuery {
        return@dbQuery ChatsTable.update({ ChatsTable.id eq id }) {
            it[users] = value
        } > 0
    }
    suspend fun getIcon(id: Int): String? = dbQuery{
        return@dbQuery ChatsTable
            .selectAll()
            .where { ChatsTable.id eq id }
            .map(::resultToChatsDatabase)
            .singleOrNull()
            ?.icon
    }
    suspend fun setIcon(value: String, id: Int): Boolean = dbQuery {
        return@dbQuery ChatsTable.update({ ChatsTable.id eq id }) {
            it[icon] = value
        } > 0
    }
    suspend fun getAll(): List<ChatsData> = dbQuery {
        return@dbQuery ChatsTable
            .selectAll()
            .map(::resultToChatsDatabase)
    }
}

class DAONotifications{
    private fun resultToNotificationsDatabase(row: ResultRow) = NotificationData(
        label = row[NotificationsTable.label],
        text = row[NotificationsTable.text],
        read = row[NotificationsTable.read],
        action = row[NotificationsTable.action]
    )
    private fun resultTo2(row: ResultRow) = NData(
        id = row[NotificationsTable.id],
        label = row[NotificationsTable.label],
        text = row[NotificationsTable.text],
        read = row[NotificationsTable.read],
        action = row[NotificationsTable.action]
    )
    suspend fun addNewNotification(data: NotificationData) = dbQuery{
        NotificationsTable.insert {
            it[label] = data.label
            it[text] = data.text
            it[read] = data.read
            it[action] = data.action
        }
    }
    suspend fun editNotification(id: Int, data: NotificationData) = dbQuery {
        NotificationsTable.update({ NotificationsTable.id eq id }) {
            it[label] = data.label
            it[text] = data.text
            it[read] = data.read
            it[action] = data.action
        }
    }
    suspend fun getAllNotifications(): List<NData> = dbQuery{
        return@dbQuery NotificationsTable.selectAll().map(::resultTo2)
    }
    suspend fun updateRead(id: Int, read: Boolean) = dbQuery{
        NotificationsTable.update({ NotificationsTable.id eq id }) {
            it[NotificationsTable.read] = read
        }
    }
    suspend fun deleteAll() = dbQuery {
        NotificationsTable.deleteAll()
    }
}