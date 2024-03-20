package ru.anotherworld.jojack

import android.app.Application
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.onUpload
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.InternalAPI
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.copyAndClose
import io.ktor.utils.io.readUTF8Line
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readBytes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.anotherworld.jojack.chatcontroller.Message
import ru.anotherworld.jojack.chatcontroller.MessageDto
import ru.anotherworld.jojack.chatcontroller.Resource
import ru.anotherworld.jojack.database.MainDatabase
import java.io.File
import kotlin.time.Duration.Companion.seconds


val cipher = Cipher()
const val BASE_URL = "http://$IP"
const val BASE_WS = "ws://$IP"
val sDatabase = MainDatabase()
class Register{
    @OptIn(InternalAPI::class)
    suspend fun reg(login: String, pass: String, coroutineScope: CoroutineScope): String{
        val password = cipher.hash(pass)
        val client = HttpClient(){
            install(ContentNegotiation){
                json()
            }
        }
        val pair = cipher.generatePairKeys()
        val response = client.post("$BASE_URL/register"){
            contentType(ContentType.Application.Json)
            setBody(RegisterResponseRemote(login, password, cipher.encrypt(pair.first,
                cipher.md5(pass)), pair.second))
        }
        coroutineScope.launch {
            sDatabase.setOpenedKey(pair.second)
            sDatabase.setClosedKey(pair.first)
        }
        val token = Token(response.content.readUTF8Line())
        if (token.token == "User already exists") return ""
        return token.token!!
    }
}
class Login{
    @OptIn(InternalAPI::class)
    suspend fun log(login: String, pass: String, coroutineScope: CoroutineScope): String{
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
        val result = response.content.readUTF8Line().toString()
        val token = Json.decodeFromString<TokenAndKeys>(result)

        when (token.token) {
            "User not found" -> return "NF"
            "Invalid password or login" -> return "PL"
            else -> {
                if (token.privateKey != "" && token.publicKey != ""){
                    coroutineScope.launch {
                        sDatabase.setOpenedKey(token.publicKey)
                        sDatabase.setClosedKey(cipher.decrypt(token.privateKey, cipher.md5(pass)))
                    }
                }
                else Log.e("MServer ::Login", "PRIVATE AND/OR PUBLIC KEYS ARE EMPTY")

            }
        }
        return token.token!!
    }
}

@Serializable
private data class TokenAndKeys(
    val token: String?,
    val privateKey: String,
    val publicKey: String
)

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
        Log.d("INFO", result)
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
    suspend fun initSession(username: String, token: String): Resource<Unit> {
        return try {
            socket = client.webSocketSession {
                url("$BASE_WS/chat-socket?username=$username&token=${token}")
            }
            if (socket?.isActive == true){
                Resource.Success(Unit)

            } else {
                Log.e("STATUS-SOCKET", socket?.isActive.toString())
                Resource.Error("Couldn't establish a connection. ::ChatSocketServiceImpl")
            }
        } catch (e: Exception){
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
    suspend fun newLike(originalUrl: String, status: Boolean, token: String){
        val response = client.post("$BASE_URL/like"){
            contentType(ContentType.Application.Json)
            setBody(RegisterLike(originalUrl, status, token))
        }
    }
}

class ChatTwo(private val nameDb: String){
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
    suspend fun initSession(token: String): Resource<Unit> {
        return try {
            socket = client.webSocketSession {
                url("$BASE_WS/chat2?namedb=$nameDb&token2=${token}")
            }
            if (socket?.isActive == true){
                Resource.Success(Unit)

            } else {
                Resource.Error("Couldn't establish a connection. ::ChatSocketServiceImpl")
            }
        } catch (e: Exception){
            e.printStackTrace()
            Resource.Error(e.localizedMessage ?: "Unknown error ::ChatSocketServiceImpl")
        }
    }
    suspend fun sendMessage(message: TMessage2) {
        try {
            socket?.send(Frame.Text(Json.encodeToString<TMessage2>(message)))
        } catch (e: Exception){
            e.printStackTrace()
        }
    }
    suspend fun waitNewData(): TMessage2?{
        for(element in socket?.incoming!!){
            element as? Frame.Text ?: continue
            val json = element.readBytes().decodeToString()
            return Json.decodeFromString<TMessage2>(json)
        }
        return null
    }
    suspend fun closeSession() {
        socket?.close()
    }
    @OptIn(InternalAPI::class)
    suspend fun getRangeMessages(startIndex: Int, endIndex: Int): List<TMessage2>{
        return try{
            if("echat" !in nameDb){
                val response = client.post("$BASE_URL/getchat2?namedb=$nameDb"){
                    contentType(ContentType.Application.Json)
                    setBody(Indexes(startIndex, endIndex))
                }
                val result = response.content.readUTF8Line().toString()
                Json.decodeFromString<List<TMessage2>>(result)
            }
            else listOf()
        } catch (e: Exception){
            listOf()
        }
    }
    @OptIn(InternalAPI::class)
    suspend fun getCountMessages(): Int?{
        return try {
            if("echat" !in nameDb){
                val response = client.get("$BASE_URL/chatmessages?namedb=$nameDb"){
                    contentType(ContentType.Application.Json)
                }
                Json.decodeFromString<GetLengthMessages>(
                    response.content.readUTF8Line().toString()).length
            }
            else null
        } catch (e: Exception){
            null
        }

    }
}

class SearchIdOrName{
    //Configure searching -> "id:" for search only by id; "name:" for search only by login
    private companion object{
        private val client = HttpClient()
    }
    @OptIn(InternalAPI::class)
    suspend fun search(query: String): SearchP{
        return try {
            val response = client.post("$BASE_URL/search"){
                setBody(query)
            }
            val result = response.content.readUTF8Line().toString()
            Json.decodeFromString<SearchP>(result)
        } catch (e: Exception){
            Log.e("ERROR", e.message.toString())
            SearchP(arrayListOf())
        }
    }
}

class UpdatePrivacy{
    private companion object{
        private val client = HttpClient(){
            install(ContentNegotiation){
                json()
            }
        }
    }
    suspend fun updatePrivacy(privacy: Boolean, token: String){
        client.post("$BASE_URL/update-privacy"){
            contentType(ContentType.Application.Json)
            setBody(Privacy(token, privacy))
        }
    }
}

class MIcon{
    private val database = MainDatabase()
    private val file = File("data/data/ru.anotherworld.jojack/icon.png")
    private companion object{
        private val client = HttpClient(){
            install(ContentNegotiation){
                json()
            }
        }
    }
    private val client2 = HttpClient()
    suspend fun getIcon(login: String): File{
        return try{
            val response = client2.get("$BASE_URL/icon?login4=$login"){
            }
            if(file.exists()) file.delete()
            response.bodyAsChannel().copyAndClose(file.writeChannel())
            file
        } catch (e: java.lang.IllegalStateException){
            Log.e("ICON-ERROR", e.message.toString())
            file
        }
    }

    suspend fun uploadImage(text: String, byteArray: ByteArray?): Boolean {
        return try {
            if (byteArray != null) {
                val response: HttpResponse = client.submitFormWithBinaryData(
                    url = "$BASE_URL/set-icon?login3=${database.getLogin()}&token3=${database.getToken()}",
                    formData = formData {
                        append("text", text)
                        append("image", byteArray, Headers.build {
                            append(HttpHeaders.ContentType, "image/*")
                            append(HttpHeaders.ContentDisposition, "filename=icon.png")
                        })
                    }
                ) {
                    onUpload { bytesSentTotal, contentLength ->
                        println("Sent $bytesSentTotal bytes from $contentLength")
                    }
                }
            }
            true
        } catch (ex: Exception) {
            false
        }
    }
}

class MImage{
    private val cipher = Cipher()
    private val database = MainDatabase()
    private companion object{
        private val client = HttpClient(){
            install(ContentNegotiation){
                json()
            }
        }
    }
    private val client2 = HttpClient()
    suspend fun getImage(name: String): File?{
        return try{
            val response = client2.get("$BASE_URL/image?name=$name"){
            }
            val file = File("data/data/ru.anotherworld.jojack/$name.png")
            response.bodyAsChannel().copyAndClose(file.writeChannel())
            file
        } catch (e: java.lang.IllegalStateException){
            Log.e("IMAGE-ERROR", e.message.toString())
            null
        }
    }

    suspend fun uploadImage(text: String, byteArray: ByteArray?): Boolean {
        return try {
            if (byteArray != null) {
                val name = cipher.generateUniqueName()
                val response: HttpResponse = client.submitFormWithBinaryData(
                    url = "$BASE_URL/add-image?name=${name}&tokenx2=${database.getToken()!!}",
                    formData = formData {
                        append("text", text)
                        append("image", byteArray, Headers.build {
                            append(HttpHeaders.ContentType, "image/*")
                            append(HttpHeaders.ContentDisposition, "filename=${name}.png")
                        })
                    }
                ) {
                    onUpload { bytesSentTotal, contentLength ->
                        println("Sent $bytesSentTotal bytes from $contentLength")
                    }
                }
            }
            true
        } catch (ex: Exception) {
            false
        }
    }
}

class MTerminal{
    private val database = MainDatabase()
    private companion object{
        private val client = HttpClient(){
            install(ContentNegotiation){
                json()
            }
        }
    }
    @OptIn(InternalAPI::class)
    suspend fun sendQuery(query: String, password: String? = null): String{
        val receive = client.get("$BASE_URL/terminal"){
            contentType(ContentType.Application.Json)
            setBody(CommandLine(query, database.getToken()!!, password))
        }
        return Json.decodeFromString<Answer>(receive.content.readUTF8Line().toString()).answer
    }
}

class EncChatController(private val nameDb: String){
    private companion object {
        val client = HttpClient(CIO){
            install(Logging){
                logger = Logger.DEFAULT
                level = LogLevel.HEADERS
            }
            install(HttpTimeout){
                requestTimeoutMillis = 3.seconds.inWholeMilliseconds
            }
            install(ContentNegotiation){
                json()
            }
            install(WebSockets){
                pingInterval = 3.seconds.inWholeMilliseconds
            }
        }
        private var socket: WebSocketSession? = null
    }
    suspend fun initSession(token: String): Resource<Unit> {
        return try {
            socket = client.webSocketSession {
                url("$BASE_WS/echat2?namedbY=$nameDb&tokenY=${token}")
            }
            if (socket?.isActive == true){
                Resource.Success(Unit)
            } else {
                Resource.Error("Couldn't establish a connection. ::ChatSocketServiceImpl")
            }
        } catch (e: Exception){
            e.printStackTrace()
            Resource.Error(e.localizedMessage ?: "Unknown error ::ChatSocketServiceImpl")
        }
    }
    suspend fun sendMessage(message: DataMessengerEncrypted) {
        try {
            socket?.send(Frame.Text(Json.encodeToString<DataMessengerEncrypted>(message)))
        } catch (e: Exception){
            e.printStackTrace()
        }
    }
    suspend fun waitNewData(privateKey: String, login: String): DataMessengerEncrypted?{
        for(element in socket?.incoming!!) {
            element as? Frame.Text ?: continue
            val json = element.readBytes().decodeToString()
            try{
                val data = Json.decodeFromString<DataMessengerEncrypted>(json)
                if(data.sendTo == login) {
                    return DataMessengerEncrypted(
                        id = data.id,
                        author = data.author,
                        encText = RSAKotlin.decryptMessage(data.encText, privateKey),
                        time = data.time,
                        sendTo = data.sendTo
                    )
                }
                return DataMessengerEncrypted(data.id, data.author, data.encText, data.time, data.sendTo)
            } catch (e: Exception){
                println(e)
                return null
            }

//                else if(data.author == "NULL" && data.encText == """{"privacy": false}""")
//                    return DataMessengerEncrypted(0, "NULL", """{"privacy": false}""", 0)

        }
        Log.e("CLOSE", "CLOSE-SESSION")
        return null
    }
    suspend fun closeSession() {
        socket?.close()
    }
    @OptIn(InternalAPI::class)
    suspend fun getRangeMessages(startIndex: Long, endIndex: Long): List<DataMessengerEncrypted>{
        return try{
            val response = client.post("$BASE_URL/getemessages?namedb=$nameDb" +
                    "&token=${sDatabase.getToken()!!}"){
                contentType(ContentType.Application.Json)
                setBody(Indexes2(startIndex, endIndex))
            }
            val result = response.content.readUTF8Line()
            if(result == null) listOf()
            else Json.decodeFromString<List<DataMessengerEncrypted>>(result)
        } catch (e: Exception){
            listOf()
        }
    }
    @OptIn(InternalAPI::class)
    suspend fun getCountMessages(): Long?{
        return try {
            val response = client.get("$BASE_URL/ecount?namedb=$nameDb&token=${sDatabase.getToken()!!}"){
                contentType(ContentType.Application.Json)
            }
            Json.decodeFromString<CountMessages>(
                response.content.readUTF8Line().toString()).length
        } catch (e: Exception){
            null
        }

    }
    @OptIn(InternalAPI::class)
    suspend fun getAllUsers(): List<DataKeys>?{
        return try{
            val response = client.get("$BASE_URL/getencusers?namedb6=$nameDb&token6=${sDatabase.getToken()!!}"){
                contentType(ContentType.Application.Json)
            }
            Json.decodeFromString<List<DataKeys>>(
                response.content.readUTF8Line().toString()
            )
        } catch (_: Exception){
            null
        }
    }
    @OptIn(InternalAPI::class)
    suspend fun initUser(){
        val response = client.post("$BASE_URL/initnewuser2?namedbZ=$nameDb"){
            contentType(ContentType.Application.Json)
            setBody(InitEncUser(mDatabase.getLogin()!!, mDatabase.getToken()!!, mDatabase.getOpenedKey()!!))
        }
        Log.d("INFO-INIT-USER", response.status.toString())
    }
    suspend fun eChatDelete(): String{ //Удалить бд
        val response = client.post("$BASE_URL/echatdelete?namedb=$nameDb&token=${sDatabase.getToken()!!}")
        return response.status.toString()
    }
    suspend fun eChatAccess(login: String, access: Boolean): String{ //Изменить права доступа, во входных параметрах тот, у кого нужно изм. права
        val response = client.post("$BASE_URL/echataccess?namedb=$nameDb" +
                "&token=${sDatabase.getToken()!!}&qadmin=$login&newaccess=$access")
        return response.status.toString()
    }
    suspend fun eChatAddInBlacklist(guilty: String): String{
        val response = client.post("$BASE_URL/echataddinblacklist?namedb=$nameDb" +
                "&token=${sDatabase.getToken()!!}&guilty=${guilty}")
        return response.status.toString()
    }
    suspend fun eChatRemoveFromBlacklist(guilty: String): String{
        val response = client.post("$BASE_URL/echatremovefromblacklist?namedb=$nameDb" +
                "&token=${sDatabase.getToken()!!}&guilty=${guilty}")
        return response.status.toString()
    }
    @OptIn(InternalAPI::class)
    suspend fun eChatGetBlacklist(): List<String>{
        val response = client.get("$BASE_URL/echatgetblacklist?namedb=$nameDb&token=${sDatabase.getToken()!!}"){
            contentType(ContentType.Application.Json)
        }
        return try{
            Json.decodeFromString<RespondBlacklist>(response.content.readUTF8Line().toString()).list
        } catch (e: Exception){
            listOf()
        }
    }
    @OptIn(InternalAPI::class)
    suspend fun eChatGetAccess(): Boolean{
        val response = client.get("$BASE_URL/echatgetaccess?namedb=$nameDb&token=${sDatabase.getToken()!!}"){
            contentType(ContentType.Application.Json)
        }
        return try{
            Json.decodeFromString<Access>(response.content.readUTF8Line().toString()).access
        } catch (e: Exception){
            false
        }
    }
}

@Serializable
data class Access(
    val access: Boolean
)

@Serializable
data class RespondBlacklist(
    val list: List<String>
)

@Serializable
data class InitEncUser(
    val login: String,
    val token: String,
    val publicKey: String
)

@Serializable
data class DataKeys(val login: String, val publicKey: String)

@Serializable
data class DataMessengerEncrypted(
    val id: Long,
    val author: String,
    val encText: String,
    val time: Long,
    val sendTo: String
)

@Serializable
data class CountMessages(
    val length: Long
)

@Serializable
data class CommandLine(
    val query: String,
    val token: String,
    val password: String? = ""
)

@Serializable
data class Answer(
    val answer: String
)

@Serializable
data class SearchP(
    val arr: List<Pair<String, String>>
)

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
    val password: String,
    val privateKey: String,
    val publicKey: String
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
    val privacy: Boolean,
    val icon: String,
    val trustLevel: Int,
    val info: String? = ""
)

@Serializable
data class RegisterLike(
    val url: String,
    val status: Boolean,
    val token: String
)

@Serializable
data class TMessage(
    val id: Int,
    val author: String,
    val message: String,
    val time: Long
)

@Serializable
data class TMessage2(
    val id: Int,
    val author: String,
    val message: String,
    val timestamp: Long
)

@Serializable
data class GetLengthMessages(
    val length: Int
)

@Serializable
data class Indexes2(
    val startIndex: Long,
    val endIndex: Long
)

@Serializable
data class Indexes(
    val startIndex: Int,
    val endIndex: Int
)


@Serializable
data class Privacy(val token: String, val privacy: Boolean)

@Serializable
data class IconRemote(
    val login: String
)

@Serializable
data class ImageRemote(
    val name: String
)