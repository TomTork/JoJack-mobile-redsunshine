package ru.anotherworld.jojack.database

class DatabaseModel(id: Int? = null, login: String? = null,
    hashPassword: String? = null, serverId: Int? = null,
    level: Int? = null, trustLevel: Int? = null,
    device: String? = null, controlSum: String? = null,
    key: String? = null, info: String? = null) {
    var id: Int = 0
    var login: String = ""
    var hashPassword: String = ""
    var serverId: Int = -1
    var level: Int = -1
    var trustLevel: Int = 0
    var device: String = ""
    var controlSum: String = ""
    var key: String = ""
    var info: String = ""
    init {
        if (id != null)this.id = id
        if (login != null)this.login = login
        if (hashPassword != null)this.hashPassword = hashPassword
        if (serverId != null)this.serverId = serverId
        if (level != null)this.level = level
        if (trustLevel != null)this.trustLevel = trustLevel
        if (device != null)this.device = device
        if (controlSum != null)this.controlSum = controlSum
        if (key != null)this.key = key
        if (info != null)this.info = info
    }
}