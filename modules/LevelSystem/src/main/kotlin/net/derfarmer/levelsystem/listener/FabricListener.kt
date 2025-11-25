package net.derfarmer.levelsystem.listener

import net.derfarmer.levelsystem.FabricManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

object FabricListener : Listener{

    @EventHandler
    fun onLeave(e : PlayerQuitEvent) {
        FabricManager.fabricPlayers.remove(e.player)
    }
}