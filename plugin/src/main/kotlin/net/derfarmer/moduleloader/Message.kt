package net.derfarmer.moduleloader

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

object Message {

    operator fun get(key: String, vararg args: String, withPrefix: Boolean = true): Component {
        val msg = if (withPrefix) getRaw(key) + getRaw(key) else getRaw(key)
        return mm.deserialize(msg.format(args))
    }

    fun getRaw(key: String): String {
        return cache[key] ?: getFromRedis(key) ?: key
    }

    private fun getFromRedis(key: String): String? {
        val msg = Redis.db.hget("messages", key)
        cache[key] = msg
        return msg
    }

    private val cache = HashMap<String, String>()

    fun flushCache() {
        cache.clear()
    }
}

fun Player.sendMSG(key: String, vararg args: String, withPrefix: Boolean = true) {
    this.sendMessage(this.getMSG(key, *args, withPrefix = withPrefix))
}

fun Player.getMSG(key: String, vararg args: String, withPrefix: Boolean = true): Component {
    return Message.get(key, *args, withPrefix = withPrefix)
}