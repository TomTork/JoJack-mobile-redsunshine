package ru.anotherworld.jojack.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object{
        private const val DB_NAME = "database"
        private const val DB_VERSION = 1
        private const val TABLE_NAME = "database_list"
        private const val ID = "id"
        private const val LOGIN = "login"
        private const val HASH_PASSWORD = "hash_password"
        private const val SERVER_ID = "server_id"
        private const val LEVEL = "level"
        private const val TRUST_LEVEL = "trust_level"
        private const val DEVICE = "device"
        private const val CONTROL_SUM = "control_sum"
        private const val KEY = "key"
        private const val INFO = "info"
        private const val THEME = "theme"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS $TABLE_NAME($ID INTEGER PRIMARY KEY, $LOGIN TEXT, $HASH_PASSWORD TEXT, $SERVER_ID INTEGER,$LEVEL INTEGER, $TRUST_LEVEL INTEGER, $DEVICE TEXT, $CONTROL_SUM TEXT, $KEY TEXT, $INFO TEXT, $THEME INTEGER);"
        db?.execSQL(CREATE_TABLE)
        if(getAll().isEmpty()){
            addAll(DatabaseModel())
        }
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(DROP_TABLE)
        onCreate(db)
    }
    @SuppressLint("Recycle", "Range")
    fun getAll(): List<DatabaseModel>{
        val databaseModel = ArrayList<DatabaseModel>()
        val db = writableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(selectQuery, null)
        if(cursor != null){
            if(cursor.moveToFirst()){
                do {
                    val databaseM = DatabaseModel()
                    databaseM.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ID)))
                    databaseM.login = cursor.getString(cursor.getColumnIndex(LOGIN))
                    databaseM.hashPassword = cursor.getString(cursor.getColumnIndex(HASH_PASSWORD))
                    databaseM.serverId = cursor.getString(cursor.getColumnIndex(SERVER_ID)).toInt()
                    databaseM.level = cursor.getString(cursor.getColumnIndex(LEVEL)).toInt()
                    databaseM.trustLevel = cursor.getString(cursor.getColumnIndex(TRUST_LEVEL)).toInt()
                    databaseM.device = cursor.getString(cursor.getColumnIndex(DEVICE))
                    databaseM.controlSum = cursor.getString(cursor.getColumnIndex(CONTROL_SUM))
                    databaseM.key = cursor.getString(cursor.getColumnIndex(KEY))
                    databaseM.info = cursor.getString(cursor.getColumnIndex(INFO))
                    databaseM.theme = cursor.getString(cursor.getColumnIndex(THEME)).toInt()
                    databaseModel.add(databaseM)
                }while (cursor.moveToNext())
            }
        }
        cursor.close()
        return databaseModel
    }
    fun addAll(model: DatabaseModel): Boolean{
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(LOGIN, model.login)
        values.put(HASH_PASSWORD, model.hashPassword)
        values.put(SERVER_ID, model.serverId)
        values.put(LEVEL, model.level)
        values.put(TRUST_LEVEL, model.trustLevel)
        values.put(DEVICE, model.device)
        values.put(CONTROL_SUM, model.controlSum)
        values.put(KEY, model.key)
        values.put(INFO, model.info)
        values.put(THEME, model.theme)
        val _success = db.insert(TABLE_NAME, null, values)
        db.close()
        return (Integer.parseInt("$_success") != -1)
    }
    @SuppressLint("Range", "Recycle")
    fun getData(_id: Int = 0): DatabaseModel{
        val databaseM = DatabaseModel()
        val db = writableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $ID=$_id"
        val cursor = db.rawQuery(selectQuery, null)
        cursor?.moveToFirst()
        databaseM.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ID)))
        databaseM.login = cursor.getString(cursor.getColumnIndex(LOGIN))
        databaseM.hashPassword = cursor.getString(cursor.getColumnIndex(HASH_PASSWORD))
        databaseM.serverId = cursor.getString(cursor.getColumnIndex(SERVER_ID)).toInt()
        databaseM.level = cursor.getString(cursor.getColumnIndex(LEVEL)).toInt()
        databaseM.trustLevel = cursor.getString(cursor.getColumnIndex(TRUST_LEVEL)).toInt()
        databaseM.device = cursor.getString(cursor.getColumnIndex(DEVICE))
        databaseM.controlSum = cursor.getString(cursor.getColumnIndex(CONTROL_SUM))
        databaseM.key = cursor.getString(cursor.getColumnIndex(KEY))
        databaseM.info = cursor.getString(cursor.getColumnIndex(INFO))
        databaseM.theme = cursor.getString(cursor.getColumnIndex(THEME)).toInt()
        cursor.close()
        return databaseM
    }
    fun deleteData(_id: Int=0): Boolean{
        val db = this.writableDatabase
        val _success = db.delete(TABLE_NAME, ID + "=?", arrayOf(_id.toString())).toLong()
        db.close()
        return Integer.parseInt("$_success") != -1
    }
    fun updateData(model: DatabaseModel): Boolean{
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(LOGIN, model.login)
        values.put(HASH_PASSWORD, model.hashPassword)
        values.put(SERVER_ID, model.serverId)
        values.put(LEVEL, model.level)
        values.put(TRUST_LEVEL, model.trustLevel)
        values.put(DEVICE, model.device)
        values.put(CONTROL_SUM, model.controlSum)
        values.put(KEY, model.key)
        values.put(INFO, model.info)
        values.put(THEME, model.theme)
        val _success = db.update(TABLE_NAME, values, ID + "=?", arrayOf(model.id.toString())).toLong()
        db.close()
        return Integer.parseInt("$_success") != -1
    }
    fun updateLogin(login: String){
        val query = "update $TABLE_NAME set $LOGIN=\"$login\" where $ID=0"
        val db = this.writableDatabase
        db?.execSQL(query)
        db.close()
    }
    fun updateHashPassword(hashPassword: String){
        val query = "update $TABLE_NAME set $HASH_PASSWORD=\"$hashPassword\" where $ID=0"
        val db = this.writableDatabase
        db?.execSQL(query)
        db.close()
    }
    fun updateServerId(serverId: Int){
        val query = "update $TABLE_NAME set $SERVER_ID=$serverId where $ID=0"
        val db = this.writableDatabase
        db?.execSQL(query)
        db.close()
    }
    fun updateTheme(theme: Int){
        val query = "update $TABLE_NAME set $THEME=$theme where $ID=0"
        val db = this.writableDatabase
        db?.execSQL(query)
        db.close()
    }
    fun updateLevel(level: Int){
        val query = "update $TABLE_NAME set $LEVEL=$level where $ID=0"
        val db = this.writableDatabase
        db?.execSQL(query)
        db.close()
    }
    fun updateTrustLevel(trustLevel: Int){
        val query = "update $TABLE_NAME set $TRUST_LEVEL=$trustLevel where $ID=0"
        val db = this.writableDatabase
        db?.execSQL(query)
        db.close()
    }
    fun updateDevice(device: String){
        val query = "update $TABLE_NAME set $DEVICE=\"$device\" where $ID=0"
        val db = this.writableDatabase
        db?.execSQL(query)
        db.close()
    }
    fun updateControlSum(controlSum: String){
        val query = "update $TABLE_NAME set $CONTROL_SUM=\"$controlSum\" where $ID=0"
        val db = this.writableDatabase
        db?.execSQL(query)
        db.close()
    }
    fun updateKey(key: String){
        val query = "update $TABLE_NAME set $KEY=\"$key\" where $ID=0"
        val db = this.writableDatabase
        db?.execSQL(query)
        db.close()
    }
    fun updateInfo(info: String){
        val query = "update $TABLE_NAME set $INFO=\"$info\" where $ID=0"
        val db = this.writableDatabase
        db?.execSQL(query)
        db.close()
    }
}