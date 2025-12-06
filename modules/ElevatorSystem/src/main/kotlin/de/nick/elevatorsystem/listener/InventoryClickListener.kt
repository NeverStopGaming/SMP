package de.nick.elevatorsystem.listener

import de.nick.elevatorsystem.utils.AccessLevel
import de.nick.elevatorsystem.utils.Elevator
import de.nick.elevatorsystem.utils.Launcher
import net.derfarmer.moduleloader.sendMSG
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType

object InventoryClickListener : Listener {

    @EventHandler
    fun onClick(event: InventoryClickEvent) {

        val holder = event.inventory.holder
        val player = event.whoClicked

        if (player !is Player) return

        if (holder is Elevator) {
            handleElevatorAccessLevel(holder, player, event)
        }

        if (holder is Launcher) {
            handleLauncherAccessLevel(holder, player, event)
        }

    }

    private fun handleElevatorAccessLevel(holder: Elevator, player: Player, event: InventoryClickEvent) {
        if (event.slot == 4) {
            event.isCancelled = true

            if (!holder.isOwner(player) && !player.isOp) {
                player.sendMSG("elevator.noPermission.Owner")
                return
            }

            holder.accessLevel =
                AccessLevel.entries[(holder.accessLevel.ordinal + 1) % AccessLevel.entries.size]
            holder.buildGUI()

            return
        }

        if (event.slot != 4) {
            event.isCancelled = true
            return
        }
    }

    private fun handleLauncherAccessLevel(holder: Launcher, player: Player, event: InventoryClickEvent) {
        val item = event.currentItem ?: return

        if (event.clickedInventory?.type == InventoryType.PLAYER) {
            if (item.type == Material.NETHERITE_BLOCK && event.isShiftClick) {
                event.isCancelled = true
                player.sendMSG("launcher.Error.ShiftClick")
            }
            return
        }

        if(event.slot == 8) {
            event.isCancelled = true

            if (!holder.isOwner(player)) {
                player.sendMSG("launcher.notPermission")
                return
            }

            holder.accessLevel = AccessLevel.entries[(holder.accessLevel.ordinal + 1) % AccessLevel.entries.size]
            holder.buildGUI()
            return
        }

        if (event.slot != 4) {
            event.isCancelled = true
            return
        }

        var netheriteBlockCount = 0

        for (itemStack in event.inventory.contents) {
            if (itemStack != null && itemStack.type == Material.NETHERITE_BLOCK) {
                netheriteBlockCount += itemStack.amount
            }
        }

        val cursorItem = event.cursor
        val currentItem = event.currentItem

        if (cursorItem.type == Material.NETHERITE_BLOCK) {
            netheriteBlockCount += if(event.isRightClick) {
                1
            }else {
                cursorItem.amount
            }
        } else if (currentItem != null && currentItem.type == Material.NETHERITE_BLOCK) {
            netheriteBlockCount -= if (event.isRightClick && !event.isShiftClick) {
                if (currentItem.amount == 3) 2 else 1
            } else {
                currentItem.amount
            }
        }

        if (netheriteBlockCount > 3) {
            event.isCancelled = true
            return
        }

        if (netheriteBlockCount == 0) {
            event.isCancelled = true
            player.sendMSG("launcher.Error.Remove.Default.Upgrade.Inventory")
            return
        }

        holder.upgrade(netheriteBlockCount)
        player.sendMSG("launcher.upgrade.lvl", holder.level.toString())
    }
}
