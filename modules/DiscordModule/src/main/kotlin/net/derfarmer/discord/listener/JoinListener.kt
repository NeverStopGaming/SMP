package net.derfarmer.discord.listener

import net.derfarmer.discord.DiscordManager.discordID
import net.derfarmer.discord.utils.DiscordConfig
import net.derfarmer.moduleloader.Message
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

object JoinListener : Listener {

    @EventHandler
    fun onJoin(event : PlayerJoinEvent) {
        if (!DiscordConfig.whitelistEnabled) return
        if (event.player.discordID.isNotBlank()) return

        event.player.kick(Message["discord.registered"])
    }
}