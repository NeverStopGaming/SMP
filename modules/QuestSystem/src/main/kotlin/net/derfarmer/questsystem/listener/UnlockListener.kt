package net.derfarmer.questsystem.listener

import io.papermc.paper.event.entity.EntityEffectTickEvent
import net.derfarmer.moduleloader.Redis.db
import net.derfarmer.questsystem.QuestDataManager.QUEST_TREE_DATA_DB_KEY
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.Crafter
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.block.CrafterCraftEvent
import org.bukkit.event.entity.EntityPotionEffectEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerPortalEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffectType

object UnlockListener : Listener {

    fun hasToUnlock(material: Material): String? {
        return db.hget("unlock", material.name)
    }

    fun hasUnlocked(questID: String, playerUUID: String): Boolean {
        return db.hget(QUEST_TREE_DATA_DB_KEY + playerUUID, questID).toBoolean()
    }

    @EventHandler
    fun onCraft(e: PrepareItemCraftEvent) {
        val result = e.inventory.result ?: return

        val player = e.viewers.first()
        if (player !is Player) return

        val lock = hasToUnlock(result.type) ?: return

        if (!hasUnlocked(lock, player.uniqueId.toString()))
            e.inventory.result = null
    }

    @EventHandler
    fun onPortal(e: PlayerPortalEvent) {
        when (e.cause) {
            PlayerTeleportEvent.TeleportCause.NETHER_PORTAL -> {
                if (!hasUnlocked("85", e.player.uniqueId.toString())) e.isCancelled = true
            }

            PlayerTeleportEvent.TeleportCause.END_PORTAL -> {
                if (!hasUnlocked("412", e.player.uniqueId.toString())) e.isCancelled = true
            }

            PlayerTeleportEvent.TeleportCause.END_GATEWAY -> {
                if (!hasUnlocked("94", e.player.toString())) e.isCancelled = true
            }

            else -> {}
        }
    }

    @EventHandler
    fun onVillageInteract(e: PlayerInteractAtEntityEvent) {
        if (e.rightClicked !is Villager) return
        if (!hasUnlocked("103", e.player.uniqueId.toString())) e.isCancelled = true
    }

    @EventHandler
    fun onVillageInteract(e: PlayerInteractEntityEvent) {
        if (e.rightClicked !is Villager) return
        if (!hasUnlocked("103", e.player.uniqueId.toString())) e.isCancelled = true
    }

    @EventHandler
    fun onEffect(e: EntityPotionEffectEvent) {
        if (e.newEffect != PotionEffectType.BAD_OMEN) return
        if (e.entity !is Player) return
        val player = e.entity as Player
        if (hasUnlocked("105", player.uniqueId.toString())) return

        e.isCancelled = true
    }

    @EventHandler
    fun onEffect(e: EntityEffectTickEvent) {
        if (e.type != PotionEffectType.BAD_OMEN) return
        if (e.entity !is Player) return
        val player = e.entity as Player
        if (hasUnlocked("105", player.uniqueId.toString())) return

        e.isCancelled = true
    }

    @EventHandler
    fun onCrafter(e: CrafterCraftEvent) {
        val result = e.result

        if (!e.block.hasMetadata("owner")) return

        val crafter = e.block.state as Crafter
        val uuid = crafter.persistentDataContainer.getOrDefault(
            NamespacedKey("ev1", "owner"),
            PersistentDataType.STRING, ""
        )
        if (uuid.isBlank()) return

        val lock = hasToUnlock(result.type) ?: return

        if (!hasUnlocked(lock, uuid))
            e.isCancelled = true
    }

    @EventHandler
    fun onCraftPlace(e: BlockPlaceEvent) {
        if (e.blockPlaced !is Crafter) return
        val crafter = e.blockPlaced.state as Crafter
        crafter.persistentDataContainer.set(
            NamespacedKey("ev1", "owner"),
            PersistentDataType.STRING,
            e.player.uniqueId.toString()
        )
        crafter.update()
    }
}