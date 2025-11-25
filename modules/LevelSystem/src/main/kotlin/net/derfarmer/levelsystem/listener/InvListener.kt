package net.derfarmer.levelsystem.listener

import net.derfarmer.levelsystem.player.PlayerExpGui
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

object InvListener : Listener{

    @EventHandler
    fun onInvClick(event : InventoryClickEvent) {

        val inv = event.clickedInventory ?: return
        if (inv.holder !is PlayerExpGui) return
        if (event.currentItem == null) return

        event.isCancelled = true

        val gui = inv.holder as PlayerExpGui
    }
}