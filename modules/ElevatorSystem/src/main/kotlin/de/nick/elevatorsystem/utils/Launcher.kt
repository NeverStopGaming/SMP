package de.nick.elevatorsystem.utils

import net.derfarmer.moduleloader.Redis
import net.derfarmer.playersystem.PlayerManager
import net.derfarmer.playersystem.utils.ItemBuilder
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.Dispenser
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.persistence.PersistentDataType

class Launcher(private val block: Dispenser, var level: Int) : InventoryHolder {

    private val namespacedAccessLevel = NamespacedKey("ev1", "elytra_launcher_access_level")
    private val namespacedOwner = NamespacedKey("ev1", "elytra_launcher_owner")

    var accessLevel = AccessLevel.valueOf(
        block.persistentDataContainer.getOrDefault(
            namespacedAccessLevel, PersistentDataType.STRING, AccessLevel.OWNER.toString()
        )
    )
        set(value) {
            block.persistentDataContainer.set(
                namespacedAccessLevel, PersistentDataType.STRING, value.toString()
            )
            block.update()
            field = value
        }

    private val inv =
        Bukkit.createInventory(this, InventoryType.DISPENSER, Component.text("Elytra Launcher Level: $level"))

    init {
        buildGUI()
    }

    fun buildGUI() {
        for (i in 0..8) {
            inventory.setItem(i, ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName("§d").build())
        }

        inventory.setItem(4, ItemBuilder(Material.NETHERITE_BLOCK).setAmount(level).build())
        inv.setItem(8, accessLevel.item)
    }

    fun upgrade(level: Int) {
        this.level = level

        block.persistentDataContainer.set(
            NamespacedKey("ev1", "elytra_launcher"),
            PersistentDataType.INTEGER, level
        )

        block.update()
    }

    fun canAccess(player: Player): Boolean {

        if (accessLevel == AccessLevel.PUBLIC) return true

        val id = block.persistentDataContainer.get(namespacedOwner, PersistentDataType.STRING)

        if (id == null) {
            block.persistentDataContainer.set(namespacedOwner, PersistentDataType.STRING, player.uniqueId.toString())
            block.update()
            return true
        }

        if (player.uniqueId.toString() == id) return true

        if (accessLevel != AccessLevel.CLAN) return false

        if (PlayerManager.getClanName(player) == Redis.db.hget("player_$id", "clan")) return true

        return false
    }

    fun isOwner(player: Player): Boolean =
        block.persistentDataContainer.get(namespacedOwner, PersistentDataType.STRING) == player.uniqueId.toString()

    override fun getInventory(): Inventory {
        return inv
    }
}
