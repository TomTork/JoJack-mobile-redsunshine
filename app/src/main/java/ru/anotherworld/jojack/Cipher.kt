package ru.anotherworld.jojack

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiInfo
import android.content.Context.WIFI_SERVICE
import java.io.IOException
import java.math.BigInteger
import java.security.GeneralSecurityException
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.util.*
import java.util.Base64
import java.security.*


class Cipher {
    fun generatePassword(size: Int = 16): String{
        val alp: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        alp.plus("!@#$%^&*(0_+-=/|\\~".map { it }.toCharArray())
        return List(size) { alp.random() }.joinToString("")
    }
    fun hash(raw: String): String{
        val md = MessageDigest.getInstance("SHA-512")
        val digest = md.digest(raw.toByteArray())
        val sb = StringBuilder()
        for (i in digest.indices) {
            sb.append(((digest[i].toInt() and 0xff) + 0x100).toString(16).substring(1))
        }
        return sb.toString()
    }
    fun md5(input:String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16)
            .padStart(32, '0').substring(0, 16)
    }
    fun generatePairKeys(): Pair<String, String>{ //Private and public keys
        val privateKey: PrivateKey
        val publicKey: PublicKey
        val keyGen = KeyPairGenerator.getInstance("RSA")
        keyGen.initialize(1024)
        val pair = keyGen.generateKeyPair()
        privateKey = pair.private
        publicKey = pair.public
        return Pair(Base64.getEncoder().encodeToString(privateKey.encoded),
            Base64.getEncoder().encodeToString(publicKey.encoded))
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

class RSAKotlin {
    private var privateKey: PrivateKey
    private var publicKey: PublicKey

    companion object {

        @Throws(GeneralSecurityException::class, IOException::class)
        fun loadPublicKey(stored: String): Key {
            val data: ByteArray = Base64.getDecoder().
            decode(stored.toByteArray())
            val spec = X509EncodedKeySpec(data)
            val fact = KeyFactory.getInstance("RSA")
            return fact.generatePublic(spec)
        }

        @Throws(Exception::class)
        fun encryptMessage(plainText: String, publickey: String): String {
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.ENCRYPT_MODE, loadPublicKey(publickey))
            return Base64.getEncoder().encodeToString(cipher.doFinal

                (plainText.toByteArray()))
        }

        @Throws(Exception::class)
        fun decryptMessage(encryptedText: String?, privatekey: String):
                String {
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.DECRYPT_MODE, loadPrivateKey(privatekey))
            return String(cipher.
            doFinal(Base64.getDecoder().decode(encryptedText)))
        }

        @Throws(GeneralSecurityException::class)
        fun loadPrivateKey(key64: String): PrivateKey {
            val clear: ByteArray = Base64.getDecoder().
            decode(key64.toByteArray())
            val keySpec = PKCS8EncodedKeySpec(clear)
            val fact = KeyFactory.getInstance("RSA")
            val priv = fact.generatePrivate(keySpec)
            Arrays.fill(clear, 0.toByte())
            return priv
        }
    }

    init {
        val keyGen = KeyPairGenerator.getInstance("RSA")
        keyGen.initialize(1024)
        val pair = keyGen.generateKeyPair()
        privateKey = pair.private
        publicKey = pair.public
    }
}


