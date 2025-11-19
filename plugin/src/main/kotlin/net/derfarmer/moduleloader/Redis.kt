package net.derfarmer.moduleloader

import redis.clients.jedis.Jedis

object Redis {

    val db = Jedis(System.getenv("REDIS_HOST"), System.getenv("REDIS_PORT").toInt())

    init {
        db.auth(System.getenv("REDIS_PASSWORD"))
    }
}