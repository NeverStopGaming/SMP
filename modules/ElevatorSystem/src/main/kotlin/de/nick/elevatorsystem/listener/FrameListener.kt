package de.nick.elevatorsystem.listener

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemFrame
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.hanging.HangingBreakEvent
import org.bukkit.event.hanging.HangingPlaceEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

object FrameListener : Listener {

    @EventHandler
    fun onHangingBreak(event: HangingBreakEvent) {
        if (event.entity.type != EntityType.ITEM_FRAME) return

        val itemFrame = event.entity as ItemFrame

        if (!itemFrame.isVisible) {
            dropItemFrame(itemFrame)
            dropInvisibleItemFrame(itemFrame)
            itemFrame.remove()
        }
    }

    @EventHandler
    fun onHangingPlace(event: HangingPlaceEvent) {
        val itemStack = event.itemStack ?: return

        val container = itemStack.itemMeta.persistentDataContainer
        val key = NamespacedKey("ev1", "itemframe")

        if (container.getOrDefault(key, PersistentDataType.BOOLEAN, false)) {
            val itemFrame = event.entity as ItemFrame
            itemFrame.isVisible = false
        }
    }

    private fun dropItemFrame(itemFrame: ItemFrame) {
        itemFrame.location.world.dropItemNaturally(itemFrame.location, itemFrame.item)
    }

    private fun dropInvisibleItemFrame(itemFrame: ItemFrame) {
        val invisibleItemFrame = ItemStack(Material.ITEM_FRAME, 1)
        val meta = invisibleItemFrame.itemMeta
        if (meta != null) {
            meta.setDisplayName("§l§5Invisible Item Frame")
            meta.lore = listOf("")
            val key = NamespacedKey("ev1", "itemframe")
            meta.persistentDataContainer.set(key, PersistentDataType.BOOLEAN, true)
            invisibleItemFrame.setItemMeta(meta)
        }
        itemFrame.location.world.dropItemNaturally(itemFrame.location, invisibleItemFrame)
    }

}