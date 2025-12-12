package de.nick.waypointsharesystem.utils

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class ShareInventory : InventoryHolder {
    override fun getInventory(): Inventory {

        return  Bukkit.createInventory(this, 3*9, Component.text("WaypointShare"))
    }
}