package ru.anotherworld.jojack

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.content.Context.WIFI_SERVICE
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import kotlinx.serialization.json.Json
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.util.*


class Cipher {
    fun hash(raw: String): String{
        val md = MessageDigest.getInstance("SHA-512")
        val digest = md.digest(raw.toByteArray())
        val sb = StringBuilder()
        for (i in digest.indices) {
            sb.append(((digest[i].toInt() and 0xff) + 0x100).toString(16).substring(1))
        }
        return sb.toString()
    }
    fun getInfoDevice(): String{
        return "sdk:${android.os.Build.VERSION.SDK_INT};" +
                "codename:${android.os.Build.VERSION.CODENAME};" +
                "device:${android.os.Build.DEVICE};" +
                "model:${android.os.Build.MODEL}"
    }
    @SuppressLint("ServiceCast", "HardwareIds")
    fun getControlSum(context: Context): String{
        val wifiInfo = context.getSystemService(WIFI_SERVICE) as WifiInfo
        val MAC_ADDRESS = wifiInfo.macAddress
        return MAC_ADDRESS.toString()
    }
    fun decrypt(cipherText: String, key: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key.toByteArray(), "AES"),
            IvParameterSpec(ByteArray(16)))
        val plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText))
        return String(plainText)
    }
    fun encrypt(inputText: String, key: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key.toByteArray(), "AES"),
            IvParameterSpec(ByteArray(16)))
        val cipherText = cipher.doFinal(inputText.toByteArray())
        return Base64.getEncoder().encodeToString(cipherText)
    }
}
