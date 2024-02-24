package ru.anotherworld.jojack.database

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseHelper{
    private val database = Database.connect("jdbc:h2:/data/data/ru.anotherworld.jojack/database",
        driver = "org.h2.Driver", user = "user", password = "GlobalPassword12")

    fun init(){
        transaction(database) {
            SchemaUtils.createMissingTablesAndColumns(
                tables = arrayOf(MainTable, LikesTable, ChatsTable)
            )
        }
    }
}

suspend fun <T> dbQuery(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO){ block() }