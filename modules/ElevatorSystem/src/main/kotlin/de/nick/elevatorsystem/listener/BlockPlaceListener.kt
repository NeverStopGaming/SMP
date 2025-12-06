package de.nick.elevatorsystem.listener

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.DaylightDetector
import org.bukkit.block.Dispenser
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

object BlockPlaceListener : Listener {

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val itemInHand = event.itemInHand

        /*

            Elevator

         */

        if (isElevator(itemInHand)) {
            handleElevatorPlacement(event)
        }

        /*

               Launcher

         */

        if (launcherLvl(itemInHand) > 0) {
            handleElytraLauncherPlacement(event)
        }

        if (launcherLvl(itemInHand) == 100) {
            handleSpawnElytraLauncherPlacement(event)
        }

    }

    private fun isElevator(item: ItemStack): Boolean {
        return item.itemMeta.persistentDataContainer.getOrDefault(
            NamespacedKey("ev1", "elevator"), PersistentDataType.BOOLEAN, false
        )
    }

    private fun handleElevatorPlacement(event: BlockPlaceEvent) {
        if (event.blockPlaced.type == Material.DAYLIGHT_DETECTOR) {
            val daylightDetector = event.blockPlaced.state as DaylightDetector
            daylightDetector.persistentDataContainer.set(
                NamespacedKey("ev1", "elevator"),
                PersistentDataType.BOOLEAN,
                true
            )
            daylightDetector.update()
        }
    }

    /*

        Launcher

     */

    private fun launcherLvl(item: ItemStack): Int {
        return item.itemMeta.persistentDataContainer.getOrDefault(
            NamespacedKey("ev1", "elytra_launcher"), PersistentDataType.INTEGER, 0)
    }


    private fun handleElytraLauncherPlacement(event: BlockPlaceEvent) {
        if (event.blockPlaced.type == Material.DISPENSER) {

            val dispenser = event.blockPlaced.state as Dispenser

            dispenser.persistentDataContainer.set(
                NamespacedKey("ev1", "elytra_launcher"),
                PersistentDataType.INTEGER, launcherLvl(event.itemInHand))

            dispenser.update()
        }
    }


    private fun handleSpawnElytraLauncherPlacement(event: BlockPlaceEvent) {
        if (event.blockPlaced.type == Material.DISPENSER) {
            val dispenser = event.blockPlaced.state as Dispenser
            dispenser.persistentDataContainer.set(
                NamespacedKey("ev1", "elytra_launcher"),
                PersistentDataType.INTEGER, 100)

            dispenser.update()
        }
    }

}