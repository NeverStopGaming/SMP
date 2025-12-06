package de.nick.elevatorsystem

import de.nick.elevatorsystem.listener.*
import de.nick.elevatorsystem.listener.player.PlayerInteractListener
import de.nick.elevatorsystem.listener.player.PlayerJumpListener
import de.nick.elevatorsystem.listener.player.PlayerMoveListener
import de.nick.elevatorsystem.listener.player.PlayerToggleSneakListener
import de.nick.elevatorsystem.utils.PlayerFlyStart
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

        val elytraLauncherRecipe = ShapedRecipe(NamespacedKey("ev1", "elytralauncher_recipe"),
            ItemBuilder(Material.DISPENSER).setDisplayName("§l§2Elytra Launcher")
                .setLore("§3Platziere den Elytra Launcher mit dem Loch nach oben und befülle ihn mit Treibstoff\", \"§3Anschließend kannst du dich boosten lassen, indem du sneakst.")
                .setData("ev1", "elytra_launcher", 1).build())

        elytraLauncherRecipe.shape("***", "*A*", "*B*")
        elytraLauncherRecipe.setIngredient('*', Material.FIREWORK_ROCKET)
        elytraLauncherRecipe.setIngredient('A', Material.DISPENSER)
        elytraLauncherRecipe.setIngredient('B', Material.FIRE_CHARGE)


        try {
            register(elevatorRecipe)
            register(elytraLauncherRecipe)
        } catch (e: Exception) {
            println(e)
        }


        register(BlockBreakListener)
        register(BlockPlaceListener)
        register(InventoryClickListener)
        register(InventoryListener)
        register(PlayerInteractListener)
        register(PlayerJumpListener)
        register(PlayerMoveListener)
        register(PlayerToggleSneakListener)
        register(PlayerFlyStart)
    }

    override fun onDisable() {
    }

    override fun onReload() {
    }
}