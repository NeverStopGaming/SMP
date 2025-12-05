package de.nick.elevatorsystem

import de.nick.elevatorsystem.listener.BlockBreakListener
import de.nick.elevatorsystem.listener.BlockPlaceListener
import de.nick.elevatorsystem.listener.InventoryClickListener
import de.nick.elevatorsystem.listener.InventoryListener
import de.nick.elevatorsystem.listener.PlayerInteractListener
import de.nick.elevatorsystem.listener.PlayerJumpListener
import de.nick.elevatorsystem.listener.PlayerToggleSneakListener
import net.derfarmer.moduleloader.modules.Module
import net.derfarmer.playersystem.utils.ItemBuilder
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ShapedRecipe


object ElevatorModule : Module() {

    override fun onEnable() {
        val elevatorRecipe = ShapedRecipe(NamespacedKey("ev1", "elevator_recipe"),
            ItemBuilder(Material.DAYLIGHT_DETECTOR).setDisplayName("§2Elevator")
                .setLore("§l§3Platziere zwei Elevators übereinander.")
                .setData("ev1", "elevator", true).build())

        elevatorRecipe.shape(" D ", " E ", " N ") // Das Rezeptmuster
        elevatorRecipe.setIngredient('D', Material.DIAMOND)
        elevatorRecipe.setIngredient('E', Material.DAYLIGHT_DETECTOR)
        elevatorRecipe.setIngredient('N', Material.ENDER_PEARL)

        Bukkit.getServer().addRecipe(elevatorRecipe)

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