package de.nick.elevatorsystem.listener

import de.nick.elevatorsystem.utils.Elevator
import jdk.javadoc.internal.tool.AccessLevel
import net.derfarmer.playersystem.PlayerManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

object InventoryListener : Listener {

    @EventHandler
    fun onClick(event: InventoryClickEvent) {

        val holder = event.inventory.holder

        if (holder !is Elevator) return

        val player = event.whoClicked

        if (player !is Player) return


        if(event.slot == 4) {
            event.isCancelled = true

            if (!holder.isOwner(player) && !player.isOp) {
                player.sendMessage("Nur der Besitzer des Lanchers kann diese ändern")
                return
            }

            holder.accessLevel = AccessLevel.entries[(holder.accessLevel.ordinal + 1) % AccessLevel.entries.size]
            holder.buildGUI()

            return
        }

        if (event.slot != 4) {
            event.isCancelled = true
            return
        }
    }

}