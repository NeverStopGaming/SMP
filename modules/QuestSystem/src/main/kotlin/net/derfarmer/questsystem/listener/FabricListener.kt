package net.derfarmer.questsystem.listener

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import kotlinx.coroutines.delay
import net.derfarmer.moduleloader.Message
import net.derfarmer.playersystem.PlayerModule
import net.derfarmer.playersystem.utils.ItemBuilder
import net.derfarmer.questsystem.FabricManager
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object FabricListener : Listener {

    @EventHandler
    fun onLeave(e: PlayerQuitEvent) {
        FabricManager.unregisterPlayer(e.player)
    }

    @Suppress("UnstableApiUsage")
    @EventHandler
    fun onInteract(e: PlayerInteractEvent) {
        if (!e.hasItem()) return
        if (!e.action.isRightClick) return
        if (!e.item!!.itemMeta.customModelDataComponent.strings.contains("quest_book")) return

        FabricManager.openBook(e.player)
    }

    val questBook = ItemBuilder(Material.BOOK).displayName(Message.get("questbook.name", withPrefix = false))
        .setCustomModelData("quest_book").build()

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        PlayerModule.plugin.launch(PlayerModule.plugin.entityDispatcher(e.player)) {
            delay(1000 * 3)
            if (FabricManager.isFabricPlayer(e.player)) return@launch

            if (!e.player.hasPlayedBefore()) {
                e.player.inventory.setItem(8, questBook)
                Bukkit.getOnlinePlayers().forEach { player ->
                    player.playSound(player.location, Sound.ENTITY_CHICKEN_EGG, 1.0f, 1.0f)
                }
            }

            e.player.kick(Message.get("kick.modNotInstalled", withPrefix = false))
        }
    }
}