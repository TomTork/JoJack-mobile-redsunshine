package ru.anotherworld.jojack.database

import android.os.Environment
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.anotherworld.jojack.Cipher
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

val daoMain = DAOMainDatabase()

class MainDatabase(){
    init {
        DatabaseHelper.init()
    }
    suspend fun init(data: MainData){
        try {
            daoMain.addNewMainDatabase(data)
        } catch (e: Exception){
            Log.e("DATABASE ::MainDatabase", e.message.toString())
        }
    }
    private suspend fun editAll(id: Int = 1, data: MainData) = daoMain.editMainDatabase(id, data)
    suspend fun getLogin(): String? = daoMain.getLogin()
    suspend fun getPrivacy(): Boolean? = daoMain.getPrivacy()
    suspend fun setPrivacy(value: Boolean): Boolean = daoMain.setPrivacy(value)
    suspend fun setLogin(value: String): Boolean = daoMain.setLogin(value)
    suspend fun getToken(): String? = daoMain.getToken()
    suspend fun setToken(value: String) = daoMain.setToken(value)
    suspend fun getTrustLevel(): Int? = daoMain.getTrustLevel()
    suspend fun setTrustLevel(value: Int) = daoMain.setTrustLevel(value)
    suspend fun getJob(): Int? = daoMain.getJob()
    suspend fun setJob(value: Int) = daoMain.setJob(value)
    suspend fun getServerId(): Int? = daoMain.getServerId()
    suspend fun setServerId(value: Int) = daoMain.setServerId(value)
    suspend fun getIcon(): String? = daoMain.getIcon()
    suspend fun setIcon(value: String) = daoMain.setIcon(value.replace(" ", ""))
    suspend fun getClosedKey(): String? = daoMain.getClosedKey()
    suspend fun setClosedKey(value: String) = daoMain.setClosedKey(value)
    suspend fun getOpenedKey(): String? = daoMain.getOpenedKey()
    suspend fun setOpenedKey(value: String) = daoMain.setOpenedKey(value)
    suspend fun getControlSum(): String? = daoMain.getControlSum()
    suspend fun setControlSum(value: String) = daoMain.setControlSum(value)
    suspend fun getDevice(): String? = daoMain.getDevice()
    suspend fun setDevice(value: String) = daoMain.setDevice(value)
    suspend fun setInfo(value: String) = daoMain.setInfo(value)
    suspend fun getInfo(): String? = daoMain.getInfo()
    suspend fun collapseDatabase() = editAll(data = MainData("", "", 0, 0, -1,
        false, "","", "", "", "", ""))
}

private fun strToByteArray(value: String?): ByteArray {
    if (value == null || value == "") return byteArrayOf()
    return value.substringAfter("[").substringBefore("]")
        .split(",").map { it.toByte() }.toByteArray()
}

val daoLikes = DAOLikesDatabase()

class LikesDatabase{
    suspend fun insertAll(originalUrl: String, liked: Boolean = true){
        daoLikes.addNewLikesDatabase(LikesData(originalUrl, liked))
    }
    suspend fun getLikedByOriginalUrl(originalUrl: String): Boolean?{
        return daoLikes.getLikedByOriginalUrl(originalUrl)
    }
    suspend fun existsData(originalUrl: String): Boolean{
        return try {
            daoLikes.existsData(originalUrl)
        } catch (e: Exception){
            false
        }
    }
    suspend fun setLikedByOriginalUrl(originalUrl: String, liked: Boolean = true){
        daoLikes.setLikedByOriginalUrl(liked, originalUrl)
    }
    suspend fun delete(){
        daoLikes.deleteLikesDatabase()
    }
}

val daoChatsDatabase = DAOChatsDatabase()

class ChatsDatabase{
    suspend fun insertAll(data: ChatsData){
        for (i in getAll()){
            if (i.chat == data.chat) return;
        }
        daoChatsDatabase.addNewChatsDatabase(data)
    }
    suspend fun deleteAll(){
        daoChatsDatabase.deleteAll()
    }
    suspend fun getAll(): List<ChatsData>{
        return daoChatsDatabase.getAll()
    }
    suspend fun delete(id: Int){
        daoChatsDatabase.delete(id)
    }
    suspend fun deleteByName(name: String){
        daoChatsDatabase.deleteByName(name)
    }
}

private val daoNotifications = DAONotifications()

class NotificationsDatabase{
    suspend fun insertAll(data: NotificationData){
        daoNotifications.addNewNotification(data)
    }
    suspend fun editAll(id: Int, data: NotificationData){
        daoNotifications.editNotification(id, data)
    }
    suspend fun updateRead(id: Int, read: Boolean){
        daoNotifications.updateRead(id, read)
    }
    suspend fun getAll(): List<NData>{
        return daoNotifications.getAllNotifications()
    }
}

data class NData(
    val id: Int,
    val label: String,
    val text: String,
    val read: Boolean,
    val action: String
)