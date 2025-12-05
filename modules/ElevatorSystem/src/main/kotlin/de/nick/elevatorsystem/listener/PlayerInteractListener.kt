package de.nick.elevatorsystem.listener

import de.nick.elevatorsystem.utils.Elevator
import net.derfarmer.moduleloader.sendMSG
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.DaylightDetector
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.persistence.PersistentDataType

object PlayerInteractListener : Listener {

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {

        val player = event.player

        if (event.clickedBlock == null) return
        if (event.clickedBlock?.type != Material.DAYLIGHT_DETECTOR) return

        val elevator = event.clickedBlock!!.state as DaylightDetector

        val isElevator = elevator.persistentDataContainer.getOrDefault(
            NamespacedKey("ev1", "elevator"),
            PersistentDataType.BOOLEAN,
            false
        )

        if (!isElevator) return

        if (event.action == Action.RIGHT_CLICK_BLOCK && player.isSneaking) {

            val holder = Elevator(elevator)

            event.isCancelled = true

            if (!holder.canAccess(player) && !player.isOp) {
                player.sendMSG("elevator.noPermission.Owner.Open")
                return
            }

            player.openInventory(holder.inventory)

        }
    }
}