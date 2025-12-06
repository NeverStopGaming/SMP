package de.nick.elevatorsystem.listener

import de.nick.elevatorsystem.utils.AccessLevel
import de.nick.elevatorsystem.utils.Elevator
import net.derfarmer.moduleloader.sendMSG
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


        if (event.slot == 4) {
            event.isCancelled = true

            if (!holder.isOwner(player) && !player.isOp) {
                player.sendMSG("elevator.noPermission.Owner")
                return
            }

            holder.accessLevel =
                AccessLevel.entries[(holder.accessLevel.ordinal + 1) % AccessLevel.entries.size]
            holder.buildGUI()

            return
        }

        if (event.slot != 4) {
            event.isCancelled = true
            return
        }
    }
}