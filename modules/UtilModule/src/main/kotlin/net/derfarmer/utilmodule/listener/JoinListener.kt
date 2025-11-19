package net.derfarmer.utilmodule.listener

import net.derfarmer.utilmodule.UtilModule
import net.derfarmer.utilmodule.commands.SpecCommand
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

object JoinListener : Listener {

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        SpecCommand.returnLocation.forEach { (uuid, _) ->
            Bukkit.getPlayer(uuid)?.let { e.player.hidePlayer(UtilModule.plugin, it); e.player.unlistPlayer(it) }
        }
    }
}