package net.derfarmer.questsystem.listener

import net.derfarmer.questsystem.QuestDataManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import kotlin.time.measureTime


object QuestListener : Listener {

    @EventHandler
    fun onItem(event: EntityPickupItemEvent) {
        if (event.entity !is Player) return

        QuestDataManager.haveItem(event.entity as Player, event.item.itemStack)
    }

    @EventHandler
    fun onItem(event: InventoryMoveItemEvent) {
        if (event.destination !is PlayerInventory) return
        val inv = event.destination as PlayerInventory

        if (inv.holder !is Player) return

        QuestDataManager.haveItem(inv.holder as Player, event.item)
    }

    @EventHandler
    fun onCraft(event: CraftItemEvent) {
        if (event.whoClicked !is Player) return
        val player = event.whoClicked as Player
        val result: ItemStack = event.recipe.result

        QuestDataManager.craftItem(player, result)
        QuestDataManager.haveItem(player, result)
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        QuestDataManager.breakBlock(event.player, event.block.type)
    }

    @EventHandler
    fun onKillMob(event: EntityDeathEvent) {
        if (event.entity.killer == null) return

        QuestDataManager.killMob(event.entity.killer!!, event.entity)
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val timeTaken = measureTime {
            QuestDataManager.initTrackers(event.player)
        }
        event.player.sendMessage("TimeTaken: $timeTaken")
        println("TimeTaken: $timeTaken")

        //TODO: Check if any server quest were completed
    }
}