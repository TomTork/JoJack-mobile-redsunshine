package ru.anotherworld.jojack.database

import android.content.Context
import kotlinx.coroutines.coroutineScope

class Controller(context: Context) {
    val db = MainDB.getDb(context)
    fun getLogin(): String{
        var login: String? = null
        Thread{
            login = db.getDao().getLogin()
        }.start()
        return login!!
    }
    fun setLogin(login: String){
        Thread{

        }
    }
}









