package net.derfarmer.playersystem.utils

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta

class ColorPickerUI(val player: Player, val callback: (String) -> Unit) : InventoryHolder {

    private val inv = Bukkit.createInventory(this, InventoryType.DISPENSER, Component.text("§aFarb-picker"))

    private var red = 0
    private var green = 0
    private var blue = 0

    init {

        setIcon(color())
        setButton()

        player.openInventory(inv)
    }

    fun add(r: Int, g: Int, b: Int) {
        red = (red + r) % 256
        green = (green + g) % 256
        blue = (blue + b) % 256

        setIcon(color())
        setButton()

    }

    private fun color() = Color.fromRGB(red, green, blue)

    fun toHex() = String.format("#%02X%02X%02X", red, green, blue)

    private fun setButton() {
        inv.setItem(3, ItemBuilder(Material.RED_WOOL).setDisplayName("§cRot: $red").build())
        inv.setItem(4, ItemBuilder(Material.GREEN_WOOL).setDisplayName("§2Grün: $green").build())
        inv.setItem(5, ItemBuilder(Material.BLUE_WOOL).setDisplayName("§bBlau: $blue").build())
    }

    private fun setIcon(color: Color) {

        val item = ItemStack(Material.LEATHER_CHESTPLATE)
        val meta = item.itemMeta as LeatherArmorMeta

        meta.lore(
            listOf(
                Component.text("§3Farbwahl"),
                Component.text("§3Klicke auf die Wolle, um eine Farbe zusammenzustellen."),
                Component.text("§3Halte Shift gedrückt, um 10 Schritte zu gehen."),
                Component.text("§3Klicke hier um zu bestätigen.")
            )
        );

        meta.setColor(color)
        item.itemMeta = meta

        inv.setItem(1, item)
    }

    override fun getInventory(): Inventory {
        return inv
    }
}

object ColorPickerListener : Listener {
    @EventHandler
    fun onClick(e: InventoryClickEvent) {

        val inv = e.clickedInventory ?: return
        if (inv.holder !is ColorPickerUI) return
        if (e.currentItem == null) return

        e.isCancelled = true

        val picker = inv.holder as ColorPickerUI

        val value = if (e.isShiftClick) 10 else 1

        when (e.currentItem!!.type) {
            Material.RED_WOOL -> {
                picker.add(value, 0, 0)
            }

            Material.GREEN_WOOL -> {
                picker.add(0, value, 0)
            }

            Material.BLUE_WOOL -> {
                picker.add(0, 0, value)
            }

            else -> {
                picker.callback(picker.toHex())
                e.whoClicked.closeInventory()
            }
        }
    }
}