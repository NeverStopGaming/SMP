package de.nick.elevatorsystem.listener

import de.nick.elevatorsystem.utils.Elevator
import net.derfarmer.moduleloader.sendMSG
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.DaylightDetector
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

object BlockBreakListener : Listener {

    @EventHandler
    fun onBreak(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block

        if (block.type == Material.DAYLIGHT_DETECTOR) {
            if (block.location == Location(Bukkit.getWorld("world"), 0.0, 63.0, 0.0) || block.location == Location(
                    Bukkit.getWorld("world"),
                    0.0,
                    200.0,
                    0.0
                )
            ) {
                if (!player.hasPermission("EV1System.ByPass.SpawnProtection")) {
                    event.isCancelled = true
                    return
                }
            }
            val daylightDetector = block.state as DaylightDetector
            val holder = Elevator(daylightDetector)
            val isElevator = daylightDetector.persistentDataContainer.getOrDefault(
                NamespacedKey("ev1", "elevator"),
                PersistentDataType.BOOLEAN,
                false
            )
            if (!isElevator) {
                return
            }

            if (!holder.canAccess(player) && !player.isOp) {
                event.isCancelled = true
                player.sendMSG("elevator.noPermission")
                return
            }

            event.isCancelled = true
            block.type = Material.AIR
            val elevator = ItemStack(Material.DAYLIGHT_DETECTOR, 1)
            val elevatorMeta = elevator.itemMeta
            elevatorMeta.displayName(Component.text("§l§2Elevator"))
            elevatorMeta.lore(mutableListOf(Component.text("§l§3Platziere zwei Elevators übereinander.")))
            elevator.setItemMeta(elevatorMeta)
            elevator.editMeta { meta: ItemMeta ->
                meta.persistentDataContainer.set(NamespacedKey("ev1", "elevator"), PersistentDataType.BOOLEAN, true)
            }
            block.location.world.dropItemNaturally(block.location.toCenterLocation(), elevator)
        }
    }
}