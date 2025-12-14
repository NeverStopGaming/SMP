package de.nick.elevatorsystem

import de.nick.elevatorsystem.command.SpawnLauncherCommand
import de.nick.elevatorsystem.listener.BlockBreakListener
import de.nick.elevatorsystem.listener.BlockPlaceListener
import de.nick.elevatorsystem.listener.FrameListener
import de.nick.elevatorsystem.listener.InventoryClickListener
import de.nick.elevatorsystem.listener.InventoryListener
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
import org.bukkit.potion.PotionType


object ElevatorModule : Module() {

    override fun onEnable() {

        /**
         * elevator
         */

        val elevatorRecipe = ShapedRecipe(
            NamespacedKey("ev1", "elevator_recipe"),
            ItemBuilder(Material.DAYLIGHT_DETECTOR).setDisplayName("§2Elevator")
                .setLore("§l§3Platziere zwei Elevators übereinander.")
                .setData("ev1", "elevator", true).build()
        )

        elevatorRecipe.shape(" D ", " E ", " N ")
        elevatorRecipe.setIngredient('D', Material.DIAMOND)
        elevatorRecipe.setIngredient('E', Material.DAYLIGHT_DETECTOR)
        elevatorRecipe.setIngredient('N', Material.ENDER_PEARL)

        /**
         * elytra launcher
         */

        val elytraLauncherRecipe = ShapedRecipe(
            NamespacedKey("ev1", "elytralauncher_recipe"),
            ItemBuilder(Material.DISPENSER).setDisplayName("§l§2Elytra Launcher")
                .setLore("§3Platziere den Elytra Launcher mit dem Loch nach oben und befülle ihn mit Treibstoff\", \"§3Anschließend kannst du dich boosten lassen, indem du sneakst.")
                .setData("ev1", "elytra_launcher", 1).build()
        )

        elytraLauncherRecipe.shape("***", "*A*", "*B*")
        elytraLauncherRecipe.setIngredient('*', Material.FIREWORK_ROCKET)
        elytraLauncherRecipe.setIngredient('A', Material.DISPENSER)
        elytraLauncherRecipe.setIngredient('B', Material.FIRE_CHARGE)

        /**
         * Light Block
         */

        val lightBlockRecipe = ShapedRecipe(NamespacedKey("ev1", "lightblock_recipe"), ItemBuilder(Material.LIGHT).setDisplayName("§5Light").build())

        lightBlockRecipe.shape("*A*", "ABA", "*A*")
        lightBlockRecipe.setIngredient('A', Material.GLOWSTONE_DUST)
        lightBlockRecipe.setIngredient('B', Material.COAL)

        /**
         * Light Block
         */

        val invisibleItemFrameRecipe = ShapedRecipe(NamespacedKey("ev1", "invisibleitemframe_recipe"),
            ItemBuilder(Material.ITEM_FRAME).setDisplayName("§5Invisible Item Frame").setData("ev1", "itemframe", true).setAmount(4).build())

        val invisiblePotion = ItemBuilder(Material.POTION).setPotion(PotionType.INVISIBILITY).build()

        invisibleItemFrameRecipe.shape("*A*", "BCD", "*E*")
        invisibleItemFrameRecipe.setIngredient('A', Material.ITEM_FRAME)
        invisibleItemFrameRecipe.setIngredient('B', Material.ITEM_FRAME)
        invisibleItemFrameRecipe.setIngredient('C', invisiblePotion)
        invisibleItemFrameRecipe.setIngredient('D', Material.ITEM_FRAME)
        invisibleItemFrameRecipe.setIngredient('E', Material.ITEM_FRAME)

        try {
            register(elevatorRecipe)
            register(elytraLauncherRecipe)
            register(lightBlockRecipe)
            register(invisibleItemFrameRecipe)
        } catch (e: Exception) {
            println(e)
        }


        register(SpawnLauncherCommand)

        register(BlockBreakListener)
        register(BlockPlaceListener)
        register(FrameListener)
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