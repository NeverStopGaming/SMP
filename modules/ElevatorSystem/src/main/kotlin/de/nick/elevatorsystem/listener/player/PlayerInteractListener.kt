package de.nick.elevatorsystem.listener.player

import de.nick.elevatorsystem.utils.Elevator
import de.nick.elevatorsystem.utils.Launcher
import net.derfarmer.moduleloader.sendMSG
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.DaylightDetector
import org.bukkit.block.Dispenser
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.persistence.PersistentDataType

object PlayerInteractListener : Listener {

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val clickedBlock = event.clickedBlock ?: return
        val player = event.player
        when (clickedBlock.type) {
            Material.DAYLIGHT_DETECTOR -> handleElevatorInteraction(clickedBlock.state as DaylightDetector, player, event)
            Material.DISPENSER -> handleLauncherInteraction(clickedBlock.state as Dispenser, player, event)
            else -> return
        }
    }


    private fun handleElevatorInteraction(elevatorDetector: DaylightDetector, player: Player, event: PlayerInteractEvent) {
        val isElevator = elevatorDetector.persistentDataContainer.getOrDefault(
            NamespacedKey("ev1", "elevator"),
            PersistentDataType.BOOLEAN,
            false
        )

        if (!isElevator) return

        if (event.action == Action.RIGHT_CLICK_BLOCK && player.isSneaking) {
            val holder = Elevator(elevatorDetector)
            event.isCancelled = true

            if (!holder.canAccess(player) && !player.isOp) {
                player.sendMSG("elevator.noPermission.Owner.Open")
                return
            }

            player.openInventory(holder.inventory)
        }
    }

    private fun handleLauncherInteraction(dispenser: Dispenser, player: Player, event: PlayerInteractEvent) {
        val launcherlvl = dispenser.persistentDataContainer.get(
            NamespacedKey("ev1", "elytra_launcher"),
            PersistentDataType.INTEGER
        )

        if (launcherlvl == null || launcherlvl == 100) {
            return
        }

        if (event.action == Action.RIGHT_CLICK_BLOCK && player.isSneaking) {
            val holder = Launcher(dispenser, launcherlvl)
            event.isCancelled = true

            if (!holder.canAccess(player) && !player.isOp) {
                player.sendMSG("launcher.notPermission.Inventory")
                return
            }

            player.openInventory(holder.inventory)
        }
    }
}