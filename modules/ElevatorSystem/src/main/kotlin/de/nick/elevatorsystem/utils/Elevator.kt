package de.nick.elevatorsystem.utils

import jdk.javadoc.internal.tool.AccessLevel
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.DaylightDetector
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class Elevator (private val block: DaylightDetector) : InventoryHolder {

    private val namespacedAccessLevel = NamespacedKey("ev1", "elevator_access_level")
    private val namespacedOwner = NamespacedKey("ev1", "elevator_owner")

    var accessLevel = AccessLevel.valueOf(block.persistentDataContainer.getOrDefault(
        namespacedAccessLevel, PersistentDataType.STRING, AccessLevel.PRIVATE.toString()))
        set(value) {
            block.persistentDataContainer.set(
                namespacedAccessLevel, PersistentDataType.STRING, value.toString())
            block.update()
            field = value
        }

    private val inv = Bukkit.createInventory(this, InventoryType.DISPENSER,  Component.text("Elevator"))

    init {
        buildGUI()
    }

    fun buildGUI() {
        for (i in 0.. 8) {
            inventory.setItem(i, ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName("§d").build())
        }

        inv.setItem(4, accessLevel.item)
    }

    fun canAccess(player: Player) : Boolean{

        if (accessLevel == AccessLevel.PUBLIC) return true

        val id = block.persistentDataContainer.get(namespacedOwner, PersistentDataType.STRING)

        if(id == null) {
            block.persistentDataContainer.set(namespacedOwner, PersistentDataType.STRING, player.uniqueId.toString())
            block.update()
            return true
        }

        if (player.uniqueId.toString() == id) return true

        if(accessLevel != AccessLevel.CLAN) return false

        if (player.getClan() == PlayerManager.getClan(id)) return true

        return false
    }

    fun isOwner(player: Player): Boolean =
        block.persistentDataContainer.get(namespacedOwner, PersistentDataType.STRING) == player.uniqueId.toString()

    override fun getInventory(): Inventory {
        return inv
    }
}