package net.derfarmer.levelsystem.player

import net.derfarmer.levelsystem.player.PlayerLevelManager.playerLevel
import net.derfarmer.levelsystem.player.PlayerLevelManager.playerXP
import net.derfarmer.moduleloader.mm
import net.derfarmer.playersystem.utils.ItemBuilder
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import kotlin.math.floor

class PlayerExpGui(val player: Player) : InventoryHolder {

    val nextLevel = player.playerLevel + 1
    private val inv = Bukkit.createInventory(this, 9*6, title())

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
            val item = if (level < nextLevel) green else red

            item.displayName(Component.text("Level $level"))
            inv.setItem(i,item.build())
        }
    }
    fun fillBg() {
        val bgItem = ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName(Component.empty()).build()

        for (i in 0..<inv.size) {
            inv.setItem(i, bgItem)
        }
    }

    fun title() : Component {

        val currentXP = player.playerXP
        val neededXP = PlayerLevelManager.calcXPRequiredForLevel(nextLevel)
        val segments = 20

        val frac = (currentXP.coerceIn(0, neededXP).toDouble() / neededXP.toDouble())
        val filledSegments = floor(frac * segments).toInt()

        var msg = "<dark_gray>[<gradient:green:blue>"
        for (i in 1..filledSegments) {
            msg += "|"
        }
        msg += "</gradient><gray>"
        for (i in 1 .. segments -filledSegments) {
            msg += "|"
        }
        msg += "</gray>]"

        val progressText = Component.text(" $currentXP / $neededXP")
        return mm.deserialize(msg).append(progressText)
    }
}