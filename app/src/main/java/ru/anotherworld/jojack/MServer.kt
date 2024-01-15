package ru.anotherworld.jojack

import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.Charset
import java.util.Scanner

class MServer(){
    fun sendMessage(client: Socket, message: String){
        client.outputStream.write(message.toByteArray())
        client.close()
    }
    fun waitAnswer(){
        val server = ServerSocket(8080)
        println("Server running on port ${server.localPort}")
        val client = server.accept()
        println("Client connected : ${client.inetAddress.hostAddress}")
        val scanner = Scanner(client.inputStream)
        while (scanner.hasNextLine()) {
            println(scanner.nextLine())
            break
        }
        server.close()
    }
}
