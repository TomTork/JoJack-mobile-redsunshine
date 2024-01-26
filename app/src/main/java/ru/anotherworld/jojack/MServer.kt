package ru.anotherworld.jojack

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.InternalAPI
import io.ktor.utils.io.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

val cipher = Cipher()
class Register{
    @OptIn(InternalAPI::class)
    suspend fun reg(login: String, pass: String): String{
        val password = cipher.hash(pass)
        val client = HttpClient(){
            install(ContentNegotiation){
                json()
            }
        }
        val response = client.post("http://192.168.0.148:8080/register"){
            contentType(ContentType.Application.Json)
            setBody(RegisterResponseRemote(login, password))
        }
        val token = Token(response.content.readUTF8Line())
        if (token.token == "User already exists") return ""
        return token.token!!
    }
}
class Login{
    @OptIn(InternalAPI::class)
    suspend fun log(login: String, pass: String): String{
        val password = cipher.hash(pass)
        val client = HttpClient(){
            install(ContentNegotiation){
                json()
            }
        }
        val response = client.post("http://192.168.0.148:8080/login"){
            contentType(ContentType.Application.Json)
            setBody(LoginResponseRemote(login, password))
        }
        val token = Token(response.content.readUTF8Line())
        if(token.token == "User not found") return "NF"
        else if(token.token == "Invalid password or login") return "PL"
        return token.token!!
    }
}
class GetPostVk{
    @OptIn(InternalAPI::class)
    suspend fun getPostVk(start: Int, end: Int): GetRPost {
        val client = HttpClient() {
            install(ContentNegotiation) {
                json()
            }
        }
        val response = client.post("http://192.168.0.148:8080/vk") {
            contentType(ContentType.Application.Json)
            setBody(VkResponseRemote(start, end, "-"))
        }
        val result = response.content.readUTF8Line().toString()
        return Json.decodeFromString<GetRPost>(result)
    }
}

class GetInfo{
    //Json.decodeFromString<VkResponseRemote>(jsonString)
    @OptIn(InternalAPI::class)
    suspend fun getMaxId(): Int{
        val client = HttpClient(){
            install(ContentNegotiation){
                json()
            }
        }
        val response = client.get("http://192.168.0.148:8080/info"){
            contentType(ContentType.Application.Json)
        }
        return Json.decodeFromString<Info>(response.content.readUTF8Line().toString()).maxId
    }
}

@Serializable
data class Info(
    val maxId: Int
)
@Serializable
data class GetRPost(
    val post: ArrayList<VkPost>
)

@Serializable
data class VkPost(
    val iconUrl: String,
    val groupName: String,
    val textPost: String,
    val imagesUrls: String,
    val like: Int,
    val commentsUrl: String,
    val originalUrl: String
)

@Serializable
data class VkResponseRemote(
    val startIndex: Int,
    val endIndex: Int,
    val token: String
)

@Serializable
private data class RegisterResponseRemote(
    val login: String,
    val password: String
)

@Serializable
private data class Token(
    val token: String?
)

@Serializable
private data class LoginResponseRemote(
    val login: String,
    val password: String
)