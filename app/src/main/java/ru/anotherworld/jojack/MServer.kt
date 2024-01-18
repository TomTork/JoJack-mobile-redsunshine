package ru.anotherworld.jojack

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*

//"""NU:{"login":"","hashPassword":""}""" - enter New User
var reg = false
class MyClient {
    fun main(){
        runBlocking {
            val selectorManager = SelectorManager(Dispatchers.IO)
            val socket = aSocket(selectorManager).tcp().connect("192.168.0.148", 8080)

            val receiveChannel = socket.openReadChannel()
            val sendChannel = socket.openWriteChannel(autoFlush = true)
            launch(Dispatchers.IO){
                while (true){  //Ожидаем строчку от сервера
                    val greeting = receiveChannel.readUTF8Line()  //Получить строчку от сервера
                    if (greeting != null){
                        if(":" in greeting){
                            val typeCommand = greeting.substring(0, 2)
                            val command = greeting.substring(3)
                            when(typeCommand){
                                "EE" -> {
                                    if(command == "0") showErrorEntered()
                                    else if(command == "-1") showError()
                                }
                                "ID" -> {
                                    database.setId(command.toInt())
                                    database.setLogin(timeLogin!!)
                                    database.setHashPassword(timeHashPassword!!)
                                }
                            }
                        }
                        println(greeting)
                    }
                }
            }
            while (true){
                if(reg){
//                    reg = false

                    sendChannel.writeStringUtf8("""NU:{"login":"$timeLogin","hashPassword":"$timeHashPassword"}""")
                }
            }
        }
    }
    fun sendToServer(text: String){
        runBlocking {
            val selectorManager = SelectorManager(Dispatchers.IO)
            val socket = aSocket(selectorManager).tcp().connect("192.168.0.148", 8080)
            val sendChannel = socket.openWriteChannel(autoFlush = true)
            launch(Dispatchers.IO) {
                sendChannel.writeStringUtf8(text)
            }
        }
    }
}
