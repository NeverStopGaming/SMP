package net.derfarmer.levelsystem.gui

import net.derfarmer.levelsystem.player.PlayerLevelManager.playerLevel
import net.derfarmer.playersystem.utils.ItemBuilder
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

class PlayerExpGui(val player: Player) : InventoryHolder{

    private val inv = Bukkit.createInventory(this, 9*6, Component.text ("Test"))

    init {
        fillBg()
        genLevelProgressbar()
    }

    override fun getInventory(): Inventory {
        return inv
    }

    fun genLevelProgressbar() {
        val green = ItemBuilder(Material.GREEN_STAINED_GLASS_PANE)
        val red = ItemBuilder(Material.RED_STAINED_GLASS_PANE)

        val array = arrayOf(9,10,19,28,37,38,39,30,21,12,13,14,23,32,41,42,43,34,25,16,17)
        array.forEachIndexed { index, i ->
            val level = index + 1
            val item = if (level <= player.playerLevel) green else red

            item.displayName(Component.text("Level $level"))
            inv.setItem(i,item.build())
        }
    }
    fun fillBg() {
        val bgItem = ItemStack(Material.BLACK_STAINED_GLASS_PANE)
        val meta = bgItem.itemMeta
        meta.displayName(Component.empty())
        bgItem.itemMeta = meta

        for (i in 0..<inv.size) {
            inv.setItem(i, bgItem)
        }
    }
}