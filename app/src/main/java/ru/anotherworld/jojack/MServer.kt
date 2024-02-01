package ru.anotherworld.jojack

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.get
import androidx.compose.runtime.State
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.InternalAPI
import io.ktor.utils.io.*
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readBytes
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ru.anotherworld.jojack.chatcontroller.MessageDto
import ru.anotherworld.jojack.chatcontroller.Message
import ru.anotherworld.jojack.chatcontroller.Resource
import ru.anotherworld.jojack.database.MainDatabase

val cipher = Cipher()
const val BASE_URL = "http://192.168.0.148:8080"
const val BASE_WS = "ws://192.168.0.148:8080"
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

class ChatController{
    private companion object {
        val client = HttpClient(){
            install(WebSockets)
            install(Logging)
            install(ContentNegotiation){
                json()
            }

        }
        private var socket: WebSocketSession? = null
        private val username = sDatabase.getLogin()
        private val chatM = mutableStateOf(ChatM())
        val state: State<ChatM> = chatM

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

    suspend fun getAllMessages3(): List<Message>{
        chatM.value = state.value.copy()
        val result = getAllMessages()
        chatM.value = state.value.copy(
            messages = result)
        return chatM.value.messages
    }
    suspend fun initSession(username: String): Resource<Unit> {
        return try {
            socket = client.webSocketSession {
                url("$BASE_WS/chat-socket?username=$username&token=${sDatabase.getToken()}")
            }
            if (socket?.isActive == true){
                Log.d("SOCKET", "TRUE11")
                Resource.Success(Unit)
            } else {
                Log.e("STATUS-SOCKET", socket?.isActive.toString())
                Resource.Error("Couldn't establish a connection. ::ChatSocketServiceImpl")
            }
        } catch (e: Exception){
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
    fun observeMessages(): Flow<Message> {
        return try {
            socket?.incoming
                ?.receiveAsFlow()
                ?.filter { it is Frame.Text }
                ?.map {
                    val json = (it as? Frame.Text)?.readText() ?: ""
                    Log.d("JSON-NEW", json)
                    val messageDto = Json.decodeFromString<MessageDto>(json)
                    messageDto.toMessage()
                } ?: flow {  }
        } catch (e: Exception){
            e.printStackTrace()
            flow {  }
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
    suspend fun getAllM2(): List<Message>{
        when(val result = initSession(username)){
            is Resource.Success -> {
                observeMessages()
                    .onEach { message ->
                        val newList = ArrayList<Message>().apply {
                            add(0, element = message)
                        }
                        chatM.value = state.value.copy(
                            messages = newList
                        )
                    }

            }
            is Resource.Error -> {
                Log.e("ERROR", "Unknown error ::MServer::Chat")
            }
        }
        return chatM.value.messages
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