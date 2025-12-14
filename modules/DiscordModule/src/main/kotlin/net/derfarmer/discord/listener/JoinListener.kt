package net.derfarmer.discord.listener

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import kotlinx.coroutines.withContext
import net.derfarmer.discord.DiscordManager.discordID
import net.derfarmer.discord.DiscordModule
import net.derfarmer.discord.utils.DiscordConfig
import net.derfarmer.moduleloader.Message
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

object JoinListener : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        if (!DiscordConfig.whitelistEnabled) return
        if (event.player.discordID.isNotBlank()) return
        if (event.player.isOp) return

        DiscordModule.plugin.launch {
            withContext(DiscordModule.plugin.entityDispatcher(event.player)) {
                event.player.kick(Message.get("discord.registered", withPrefix = false))
            }
        }
    }
}