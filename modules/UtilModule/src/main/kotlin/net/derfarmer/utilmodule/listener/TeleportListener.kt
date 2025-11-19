package net.derfarmer.utilmodule.listener

import net.derfarmer.utilmodule.commands.DisableEndCommand
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent

object TeleportListener : Listener {

    @EventHandler
    fun onPortalEnter(e: PlayerTeleportEvent) {
        if (e.cause != PlayerTeleportEvent.TeleportCause.END_PORTAL) return
        if (DisableEndCommand.isEndEnabled) return

        e.isCancelled = true
    }
}