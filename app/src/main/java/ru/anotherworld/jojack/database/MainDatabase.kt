package ru.anotherworld.jojack.database

import android.os.Environment
import android.util.Log
import ru.anotherworld.jojack.Cipher
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

class MainDatabase {
    companion object{
        private const val path = "data/data/ru.anotherworld.jojack/"
        private fun d(name: String): String = "$path$name.txt"
        private const val key_ = "ProtectDatabaseM"
        private val cipher = Cipher()
    }
    val id = File(d("id"))
    private val login = File(d("login"))
    private val token = File(d("token"))
    private val hash = File(d("hash_password"))
    private val serverId = File(d("server_id"))
    private val level = File(d("level"))
    private val trustLevel = File(d("trust_level"))
    private val device = File(d("device"))
    private val controlSum = File(d("control_sum"))
    private val key = File(d("key"))
    private val info = File(d("info"))
    private val theme = File(d("theme"))

    init {
        try {
            if (!id.exists() || !login.exists() || !hash.exists() ||
                !serverId.exists() || !level.exists() || !trustLevel.exists() ||
                !device.exists() || !controlSum.exists() || !key.exists() ||
                !info.exists() || !theme.exists() || !token.exists()){
                id.createNewFile()
                login.createNewFile()
                hash.createNewFile()
                serverId.createNewFile()
                level.createNewFile()
                trustLevel.createNewFile()
                device.createNewFile()
                controlSum.createNewFile()
                key.createNewFile()
                info.createNewFile()
                theme.createNewFile()
                token.createNewFile()

                base()
            }
        }catch (e: FileNotFoundException){
            id.createNewFile()
            login.createNewFile()
            hash.createNewFile()
            serverId.createNewFile()
            level.createNewFile()
            trustLevel.createNewFile()
            device.createNewFile()
            controlSum.createNewFile()
            key.createNewFile()
            info.createNewFile()
            theme.createNewFile()
            token.createNewFile()

            base()
        }
    }
    private fun base(){ setId(-1); setLogin(""); setHashPassword(""); setServerId(-1);
    setLevel(-1); setTrustLevel(-1); setDevice("Android"); setControlSum("");
    setKey(cipher.hash(List(16) { (('a'..'z') + ('A'..'Z') + ('0'..'9')).random() }.joinToString("")));
    setInfo(""); setTheme(0); setToken("") }
    fun getId(): Int = cipher.decrypt(FileInputStream(id).bufferedReader().readText(), key_).toInt()
    fun getLogin(): String = cipher.decrypt(FileInputStream(login).bufferedReader().readText(), key_)
    fun getToken(): String = cipher.decrypt(FileInputStream(token).bufferedReader().readText(), key_)
    fun getHashPassword(): String = cipher.decrypt(FileInputStream(hash).bufferedReader().readText(), key_)
    fun getServerId(): Int = cipher.decrypt(FileInputStream(serverId).bufferedReader().readText(), key_).toInt()
    fun getLevel(): Int = cipher.decrypt(FileInputStream(level).bufferedReader().readText(), key_).toInt()
    fun getTrustLevel(): Int = cipher.decrypt(FileInputStream(trustLevel).bufferedReader().readText(), key_).toInt()
    fun getDevice(): String = cipher.decrypt(FileInputStream(device).bufferedReader().readText(), key_)
    fun getControlSum(): String = cipher.decrypt(FileInputStream(controlSum).bufferedReader().readText(), key_)
    fun getKey(): String = cipher.decrypt(FileInputStream(key).bufferedReader().readText(), key_)
    fun getInfo(): String = cipher.decrypt(FileInputStream(info).bufferedReader().readText(), key_)
    fun getTheme(): Int = cipher.decrypt(FileInputStream(theme).bufferedReader().readText(), key_).toInt()
    fun setId(v: Int) = id.writeText(cipher.encrypt(v.toString(), key_))
    fun setLogin(v: String) = login.writeText(cipher.encrypt(v, key_))
    fun setToken(v: String) = token.writeText(cipher.encrypt(v, key_))
    fun setHashPassword(v: String) = hash.writeText(cipher.encrypt(v, key_))
    fun setServerId(v: Int) = serverId.writeText(cipher.encrypt(v.toString(), key_))
    fun setLevel(v: Int) = level.writeText(cipher.encrypt(v.toString(), key_))
    fun setTrustLevel(v: Int) = trustLevel.writeText(cipher.encrypt(v.toString(), key_))
    fun setDevice(v: String) = device.writeText(cipher.encrypt(v, key_))
    fun setControlSum(v: String) = controlSum.writeText(cipher.encrypt(v, key_))
    fun setKey(v: String) = key.writeText(cipher.encrypt(v, key_))
    fun setInfo(v: String) = info.writeText(cipher.encrypt(v, key_))
    fun setTheme(v: Int) = theme.writeText(cipher.encrypt(v.toString(), key_))
    fun boolToInt(v: Boolean): Int{
        if(v)return 1
        return 0
    }
    fun collapseDatabase(){
        setId(-1)
        login.delete()
        hash.delete()
        serverId.delete()
        level.delete()
        trustLevel.delete()
        device.delete()
        controlSum.delete()
        key.delete()
        info.delete()
        theme.delete()
        token.delete()
    }
}

