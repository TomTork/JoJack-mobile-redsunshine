package ru.anotherworld.jojack

import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.plugins.websocket.ws
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.InternalAPI
import io.ktor.utils.io.readUTF8Line
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readBytes
import io.ktor.websocket.readText
import io.ktor.websocket.serialization.receiveDeserializedBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import ru.anotherworld.jojack.chatcontroller.Message
import ru.anotherworld.jojack.chatcontroller.MessageDto
import ru.anotherworld.jojack.chatcontroller.Resource
import ru.anotherworld.jojack.database.MainDatabase
import kotlin.time.Duration.Companion.seconds


val cipher = Cipher()
const val BASE_URL = "http://192.168.31.196:8080"
const val BASE_WS = "ws://192.168.31.196:8080"
val sDatabase = MainDatabase()
class Register{
    @OptIn(InternalAPI::class)
    suspend fun reg(login: String, pass: String): String{
        val password = cipher.hash(pass)
        val client = HttpClient(){
            install(ContentNegotiation){
                json()
            }
        }
        val response = client.post("$BASE_URL/register"){
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
        val response = client.post("$BASE_URL/login"){
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
        val response = client.post("$BASE_URL/vk") {
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
        val response = client.get("$BASE_URL/info"){
            contentType(ContentType.Application.Json)
        }
        return Json.decodeFromString<Info>(response.content.readUTF8Line().toString()).maxId
    }
}

class InitUser{
    @OptIn(InternalAPI::class)
    suspend fun getInit(login: String, token: String): InitRemote{
        val client = HttpClient(){
            install(ContentNegotiation){
                json()
            }
        }
        val response = client.post("$BASE_URL/initialInfo"){
            contentType(ContentType.Application.Json)
            setBody(Token2(login, token))
        }
        val result = response.content.readUTF8Line().toString()
        return Json.decodeFromString<InitRemote>(result)
    }
}

class ChatController{
    private companion object {
        val client = HttpClient(CIO){
            install(Logging){
                logger = Logger.DEFAULT
                level = LogLevel.HEADERS
            }
            install(HttpTimeout){
                requestTimeoutMillis = 5.seconds.inWholeMilliseconds
            }
            install(ContentNegotiation){
                json()
            }
            install(WebSockets){
                pingInterval = 5.seconds.inWholeMilliseconds
            }
        }
        private var socket: WebSocketSession? = null
    }
    @OptIn(InternalAPI::class)
    suspend fun getAllMessages(): List<Message> {
        return try {
            val get = client.get("$BASE_URL/messages"){
                contentType(ContentType.Application.Json)
            }
            return Json.decodeFromString<List<MessageDto>>(get.content.readUTF8Line().toString()).map { it.toMessage() }
        } catch (e: Exception){
            e.printStackTrace()
            emptyList()
        }
    }
    suspend fun initSession(username: String): Resource<Unit> {
        return try {
            Log.d("WHAT", "$username ${sDatabase.getToken()}")
            socket = client.webSocketSession {
                url("$BASE_WS/chat-socket?username=$username&token=${sDatabase.getToken()}")
            }
            if (socket?.isActive == true){
                Resource.Success(Unit)

            } else {
                Log.e("STATUS-SOCKET", socket?.isActive.toString())
                Resource.Error("Couldn't establish a connection. ::ChatSocketServiceImpl")
            }
        } catch (e: Exception){
            Log.d("WHAT", "$username ${sDatabase.getToken()}")
            Log.e("ERROR34", "TRUE")
            e.printStackTrace()
            Resource.Error(e.localizedMessage ?: "Unknown error ::ChatSocketServiceImpl")
        }
    }
    suspend fun sendMessage(message: String) {
        try {
            socket?.send(Frame.Text(message))
        } catch (e: Exception){
            e.printStackTrace()
        }
    }
    suspend fun waitNewData(): Message?{
        for(element in socket?.incoming!!){
            element as? Frame.Text ?: continue
            val json = element.readBytes().decodeToString()
            return Json.decodeFromString<MessageDto>(json).toMessage()
        }
        return null
    }
    suspend fun closeSession() {
        socket?.close()
    }
}
class LikeController{
    private companion object{
        val client = HttpClient(CIO){
            install(ContentNegotiation){
                json()
            }
            install(HttpTimeout){
                requestTimeoutMillis = 5.seconds.inWholeMilliseconds
            }
        }
    }
    suspend fun newLike(originalUrl: String, status: Boolean){
        val response = client.post("$BASE_URL/like"){
            contentType(ContentType.Application.Json)
            setBody(RegisterLike(originalUrl, status, mDatabase.getToken()))
        }
    }
}

data class ChatM(
    val messages: List<Message> = emptyList()
)

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
    val imagesUrls: VkImageAndVideo,
    val like: Int,
    val commentsUrl: String,
    val originalUrl: String,
    val exclusive: Boolean,
    val reposted: Boolean,
    val origName: String? = "",
    val origPost: String? = ""
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

@Serializable
private data class Token2(
    val login: String,
    val token: String
)

@Serializable
data class VkImageAndVideo(
    val images: List<String>,
    val video: String
)

@Serializable
data class InitRemote(
    val id: Int,
    val job: Int,
    val trustLevel: Int,
    val info: String? = ""
)

@Serializable
data class RegisterLike(
    val url: String,
    val status: Boolean,
    val token: String
)