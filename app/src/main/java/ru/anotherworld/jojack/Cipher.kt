package ru.anotherworld.jojack

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.content.Context.WIFI_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import java.security.MessageDigest


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
}