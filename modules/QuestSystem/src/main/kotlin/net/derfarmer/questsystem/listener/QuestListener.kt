package net.derfarmer.questsystem.listener

import net.derfarmer.questsystem.QuestManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

object QuestListener : Listener {

    @EventHandler
    fun onItem(event: EntityPickupItemEvent) {
        if (event.entity !is Player) return
        QuestManager.haveItem(event.entity as Player, event.item.itemStack)
    }

    @EventHandler
    fun onItem(event: InventoryMoveItemEvent) {
        if (event.destination !is PlayerInventory) return
        val inv = event.destination as PlayerInventory

        if (inv.holder !is Player) return

        QuestManager.haveItem(inv.holder as Player, event.item)
    }

    @EventHandler
    fun onCraft(event: CraftItemEvent) {
        if (event.whoClicked !is Player) return
        val player = event.whoClicked as Player
        val result: ItemStack = event.inventory.result ?: return

        QuestManager.craftItem(player, result)
        QuestManager.haveItem(player, result)
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        QuestManager.breakBlock(event.player, event.block)
    }

    @EventHandler
    fun onKillMob(event: EntityDeathEvent) {
        if (event.damageSource !is Player) return
        QuestManager.killMob(event.damageSource as Player, event.entity)
    }
}