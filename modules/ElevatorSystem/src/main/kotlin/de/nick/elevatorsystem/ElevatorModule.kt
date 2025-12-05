package de.nick.elevatorsystem

import de.nick.elevatorsystem.listener.*
import net.derfarmer.moduleloader.modules.Module
import net.derfarmer.playersystem.utils.ItemBuilder
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ShapedRecipe


object ElevatorModule : Module() {

    override fun onEnable() {
        val elevatorRecipe = ShapedRecipe(
            NamespacedKey("ev1", "elevator_recipe"),
            ItemBuilder(Material.DAYLIGHT_DETECTOR).setDisplayName("§2Elevator")
                .setLore("§l§3Platziere zwei Elevators übereinander.")
                .setData("ev1", "elevator", true).build()
        )

        elevatorRecipe.shape(" D ", " E ", " N ") // Das Rezeptmuster
        elevatorRecipe.setIngredient('D', Material.DIAMOND)
        elevatorRecipe.setIngredient('E', Material.DAYLIGHT_DETECTOR)
        elevatorRecipe.setIngredient('N', Material.ENDER_PEARL)

        register(elevatorRecipe)
        register(BlockBreakListener)
        register(BlockPlaceListener)
        register(InventoryClickListener)
        register(InventoryListener)
        register(PlayerInteractListener)
        register(PlayerJumpListener)
        register(PlayerToggleSneakListener)
    }

    override fun onDisable() {
    }

    override fun onReload() {
    }
}