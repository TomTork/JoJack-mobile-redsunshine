package ru.anotherworld.jojack.database

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class Database {
    private companion object{
        private const val path = "jdbc:sqlite:data/data/ru.anotherworld.jojack/files/database.db"
        private const val tableName = "main"
        private const val initFirst = """CREATE TABLE IF NOT EXISTS $tableName(
            id INTEGER PRIMARY KEY,
            id_user INTEGER,
            login TEXT,
            token TEXT,
            level INTEGER,
            trust_level INTEGER,
            control_sum TEXT,
            device TEXT,
            theme INTEGER,
            info TEXT,
            open_key TEXT,
            close_key TEXT,
            static_key TEXT
            );"""
    }
    private var conn: Connection? = null
    init {
        try {
            conn = DriverManager.getConnection(path)
            if (conn != null){
                val statement = conn!!.createStatement()
                statement.execute(initFirst)
            }
        } catch (e: SQLException){
            println(e.message)
        }
    }
    fun selectAll(): String{
        val sql = "SELECT * FROM $tableName"
        try {
            val statement = conn!!.createStatement()
            val res = statement.executeQuery(sql)
            var result = ""
            while (res.next()){
                result += "${res.getInt("id")};" +
                        "${res.getInt("id_user")};" +
                        "${res.getString("login")};" +
                        "${res.getString("token")};" +
                        "${res.getInt("level")};" +
                        "${res.getInt("trust_level")};" +
                        "${res.getString("control_sum")};" +
                        "${res.getString("device")};" +
                        "${res.getInt("theme")};" +
                        "${res.getString("info")};" +
                        "${res.getString("open_key")};" +
                        "${res.getString("close_key")};" +
                        res.getString("static_key")
            }
            statement.close()
            return result
        } catch (e: SQLException){
            println(e.message)
            return "null"
        }
    }
    fun dropTable(){
        val sql = "DROP TABLE $tableName"
        val statement = conn!!.createStatement()
        statement.execute(sql)
        statement.close()
    }
    fun insertAll(idUser: Int, login: String, token: String, level: Int, trustLevel: Int,
                  controlSum: String, device: String, theme: Int, info: String,
                  openKey: String, closeKey: String, staticKey: String){
        val sql = """
            INSERT INTO $tableName VALUES(
            0, $idUser, "$login", "$token", $level, $trustLevel, "$controlSum", "$device",
            $theme, "$info", "$openKey", "$closeKey", "$staticKey"
            )
        """.trimIndent()
        try {
            val statement = conn!!.createStatement()
            statement.execute(sql)
        } catch (e: SQLException){
            println(e.message)
        }
    }
    fun setTheme(value: Int){
        val sql = """UPDATE $tableName set theme=$value where id=0"""
        try {
            val statement = conn!!.prepareStatement(sql)
            statement.executeUpdate()
        } catch (e: SQLException){
            println(e.message)
        }
    }
    fun getTheme(): Int{
        val sql = "SELECT theme FROM $tableName where id=0"
        return try {
            val statement = conn!!.createStatement()
            val res = statement.executeQuery(sql).getInt("theme")
            statement.close()
            res
        } catch (e: SQLException){
            -1
        }
    }
    fun setStaticKey(value: String){
        val sql = """UPDATE $tableName set static_key="$value" where id=0"""
        try {
            val statement = conn!!.prepareStatement(sql)
            statement.executeUpdate()
        } catch (e: SQLException){
            println(e.message)
        }
    }
    fun getStaticKey(): String{
        val sql = "SELECT static_key FROM $tableName where id=0"
        return try {
            val statement = conn!!.createStatement()
            val res = statement.executeQuery(sql).getString("static_key")
            statement.close()
            res
        } catch (e: SQLException){
            "null"
        }
    }
    fun setCloseKey(value: String){
        val sql = """UPDATE $tableName set close_key="$value" where id=0"""
        try {
            val statement = conn!!.prepareStatement(sql)
            statement.executeUpdate()
        } catch (e: SQLException){
            println(e.message)
        }
    }
    fun getCloseKey(): String{
        val sql = "SELECT close_key FROM $tableName where id=0"
        return try {
            val statement = conn!!.createStatement()
            val res = statement.executeQuery(sql).getString("close_key")
            statement.close()
            res
        } catch (e: SQLException){
            "null"
        }
    }
    fun setOpenKey(value: String){
        val sql = """UPDATE $tableName set open_key="$value" where id=0"""
        try {
            val statement = conn!!.prepareStatement(sql)
            statement.executeUpdate()
        } catch (e: SQLException){
            println(e.message)
        }
    }
    fun getOpenKey(): String{
        val sql = "SELECT open_key FROM $tableName where id=0"
        return try {
            val statement = conn!!.createStatement()
            val res = statement.executeQuery(sql).getString("open_key")
            statement.close()
            res
        } catch (e: SQLException){
            "null"
        }
    }
    fun setInfo(value: String){
        val sql = """UPDATE $tableName set info="$value" where id=0"""
        try {
            val statement = conn!!.prepareStatement(sql)
            statement.executeUpdate()
        } catch (e: SQLException){
            println(e.message)
        }
    }
    fun getInfo(): String{
        val sql = "SELECT info FROM $tableName where id=0"
        return try {
            val statement = conn!!.createStatement()
            val res = statement.executeQuery(sql).getString("info")
            statement.close()
            res
        } catch (e: SQLException){
            "null"
        }
    }
    fun setDevice(value: String){
        val sql = """UPDATE $tableName set device="$value" where id=0"""
        try {
            val statement = conn!!.prepareStatement(sql)
            statement.executeUpdate()
        } catch (e: SQLException){
            println(e.message)
        }
    }
    fun getDevice(): String{
        val sql = "SELECT device FROM $tableName where id=0"
        return try {
            val statement = conn!!.createStatement()
            val res = statement.executeQuery(sql).getString("device")
            statement.close()
            res
        } catch (e: SQLException){
            "null"
        }
    }
    fun setControlSum(value: String){
        val sql = """UPDATE $tableName set control_sum="$value" where id=0"""
        try {
            val statement = conn!!.prepareStatement(sql)
            statement.executeUpdate()
        } catch (e: SQLException){
            println(e.message)
        }
    }
    fun getControlSum(): String{
        val sql = "SELECT control_sum FROM $tableName where id=0"
        return try {
            val statement = conn!!.createStatement()
            val res = statement.executeQuery(sql).getString("control_sum")
            statement.close()
            res
        } catch (e: SQLException){
            "null"
        }
    }
    fun setTrustLevel(value: Int){
        val sql = """UPDATE $tableName set trust_level=$value where id=0"""
        try {
            val statement = conn!!.prepareStatement(sql)
            statement.executeUpdate()
        } catch (e: SQLException){
            println(e.message)
        }
    }
    fun getTrustLevel(): Int{
        val sql = "SELECT trust_level FROM $tableName where id=0"
        return try {
            val statement = conn!!.createStatement()
            val res = statement.executeQuery(sql).getInt("trust_level")
            statement.close()
            res
        } catch (e: SQLException){
            -1
        }
    }
    fun setLevel(value: Int){
        val sql = """UPDATE $tableName set level=$value where id=0"""
        try {
            val statement = conn!!.prepareStatement(sql)
            statement.executeUpdate()
        } catch (e: SQLException){
            println(e.message)
        }
    }
    fun getLevel(): Int{
        val sql = "SELECT level FROM $tableName where id=0"
        return try {
            val statement = conn!!.createStatement()
            val res = statement.executeQuery(sql).getInt("level")
            statement.close()
            res
        } catch (e: SQLException){
            -1
        }
    }
    fun setToken(value: String){
        val sql = """UPDATE $tableName set token="$value" where id=0"""
        try {
            val statement = conn!!.prepareStatement(sql)
            statement.executeUpdate()
        } catch (e: SQLException){
            println(e.message)
        }
    }
    fun getToken(): String{
        val sql = "SELECT token FROM $tableName where id=0"
        return try {
            val statement = conn!!.createStatement()
            val res = statement.executeQuery(sql).getString("token")
            statement.close()
            res
        } catch (e: SQLException){
            "null"
        }
    }
    fun setLogin(value: String){
        val sql = """UPDATE $tableName set login="$value" where id=0"""
        try {
            val statement = conn!!.prepareStatement(sql)
            statement.executeUpdate()
        } catch (e: SQLException){
            println(e.message)
        }
    }
    fun getLogin(): String{
        val sql = "SELECT login FROM $tableName where id=0"
        return try {
            val statement = conn!!.createStatement()
            val res = statement.executeQuery(sql).getString("login")
            statement.close()
            res
        } catch (e: SQLException){
            "null"
        }
    }
    fun setIdUser(value: Int){
        val sql = "UPDATE $tableName set id_user=$value where id=0"
        try {
            val statement = conn!!.prepareStatement(sql)
            statement.executeUpdate()
        } catch (e: SQLException){
            println(e.message)
        }
    }
    fun getIdUser(): Int{
        val sql = "SELECT id_user FROM $tableName where id=0"
        return try {
            val statement = conn!!.createStatement()
            val res = statement.executeQuery(sql).getInt("id_user")
            statement.close()
            res
        } catch (e: SQLException){
            -1
        }
    }
    fun updateAll(id: Int, idUser: Int, login: String, token: String, level: Int, trustLevel: Int,
                  controlSum: String, device: String, theme: Int, info: String,
                  openKey: String, closeKey: String, staticKey: String){
        val sql = """
            UPDATE $tableName set id_user=$idUser, login="$login", token="$token", level=$level,
             trust_level=$trustLevel, control_sum="$controlSum", device="$device", theme=$theme,
             info="$info", open_key="$openKey", close_key="$closeKey", static_key="$staticKey"
             where id=$id
        """.trimIndent()
        try {
            val statement = conn!!.createStatement()
            statement.execute(sql)
            statement.close()
        } catch (e: SQLException){
            println(e.message)
        }
    }
    fun delete(id: Int){
        val sql = "DELETE FROM $tableName where id=$id"
        try{
            val statement = conn!!.createStatement()
            statement.execute(sql)
            statement.close()
        } catch (e: SQLException){
            println(e.message)
        }
    }
}