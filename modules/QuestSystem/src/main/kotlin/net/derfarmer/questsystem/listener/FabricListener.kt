package net.derfarmer.questsystem.listener

import net.derfarmer.questsystem.FabricManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

object FabricListener : Listener {

    @EventHandler
    fun onLeave(e: PlayerQuitEvent) {
        FabricManager.unregisterPlayer(e.player)
    }
}