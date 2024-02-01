package ru.anotherworld.jojack.chatcontroller

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

@Serializable
data class MessageDto(
    val text: String,
    val timestamp: Long,
    val username: String,
    val id: String
) {
    fun toMessage(): Message {
//        val date = Date(timestamp)
//        val formattedDate = DateFormat
//            .getDateInstance(DateFormat.DEFAULT)
//            .format(date)
        return Message(
            text = text,
            formattedTime = getCurrentTimeStamp(timestamp)!!,
            username = username
        )
    }
}

@SuppressLint("SimpleDateFormat")
fun getCurrentTimeStamp(time: Long): String? {
    val sdfDate = SimpleDateFormat("HH:mm")
    val now = Date(time)
    return sdfDate.format(now)
}