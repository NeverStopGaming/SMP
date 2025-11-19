package net.derfarmer.discord.utils

@Suppress("HasPlatformType")
object DiscordConfig {
    val token = System.getenv("DISCORD_TOKEN")
    val guildID = System.getenv("DISCORD_GUILD_ID")
    val whitelistChannelID = System.getenv("WHITELIST_CHANNEL_ID")
    val admins = System.getenv("DISCORD_ADMINS_IDS").split(";")
    val whitelistEnabled = (System.getenv("DISCORD_WHITELIST_ENABLED") ?: "true").toBoolean()
    val serverIP = System.getenv("SERVER_IP")
}