package net.derfarmer.questsystem.listener

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import net.derfarmer.questsystem.QuestModule.plugin
import net.derfarmer.questsystem.QuestDataManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerJoinEvent

object QuestListener : Listener {

    @EventHandler
    fun onItem(event: EntityPickupItemEvent) {
        if (event.entity !is Player) return
        plugin.launch(plugin.entityDispatcher(event.entity)) {
            QuestDataManager.haveItem(event.entity as Player, event.item.itemStack)
        }
    }

    @EventHandler
    fun onItem(event: InventoryCloseEvent) {

        val player = event.player
        if (player !is Player) return

        plugin.launch (plugin.entityDispatcher(player)){
            QuestDataManager.onClose(player)
        }
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
        QuestDataManager.initTrackers(event.player)
        //TODO: Check if any server quest were completed only for new players
    }
}