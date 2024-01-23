package ru.anotherworld.jojack

import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.InternalAPI
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable

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