package ru.anotherworld.jojack

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*


class MyClient {
    fun main(){
        runBlocking {
            val selectorManager = SelectorManager(Dispatchers.IO)
            val socket = aSocket(selectorManager).tcp().connect("192.168.0.148", 8080)

            val receiveChannel = socket.openReadChannel()
            //val sendChannel = socket.openWriteChannel(autoFlush = true)

            launch(Dispatchers.IO){
                while (true){  //Ожидаем строчку от сервера
                    val greeting = receiveChannel.readUTF8Line()  //Получить строчку от сервера
                    if (greeting != null){
                        println(greeting)
                    }
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
