package de.nick.waypointsharesystem.listener

import de.nick.waypointsharesystem.utils.ShareInventory
import net.derfarmer.moduleloader.sendMSG
import net.derfarmer.playersystem.PlayerManager
import net.derfarmer.playersystem.clan.ClanManager
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

object InventoryClickListener : Listener {

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {

        if (event.inventory.holder !is ShareInventory) return

        val player = event.whoClicked
        if(player !is Player) return

        event.isCancelled = true

        val waypointMessage = ChatListener.getLastWaypointMessage(player.uniqueId) ?: return

        when(event.slot) {
            10 -> {
                if (ChatListener.playerWaypointWaiting.contains(player)) {
                    player.closeInventory()
                    player.sendMSG("waypointshare.type.playername.inchat")
                    player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 10.0F, 10.0F)
                    player.closeInventory()
                    return
                }
                ChatListener.playerWaypointWaiting.add(player)
                player.sendMSG("waypointshare.type.playername.inchat")
                player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 10.0F, 10.0F)
                player.closeInventory()
            }
            13 -> {
                ChatListener.playerWaypointMessages.remove(player.uniqueId)

                Bukkit.getOnlinePlayers().forEach { onlinePlayer ->
                    onlinePlayer.sendMSG("")
                    onlinePlayer.sendMSG("waypointshare.send.global.message", player.name)
                    onlinePlayer.sendMSG("waypointshare.waypointMessage", waypointMessage)
                    onlinePlayer.sendMSG("")
                    onlinePlayer.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 10.0F, 10.0F)
                }
            }
            16 -> {
                ChatListener.playerWaypointMessages.remove(player.uniqueId)
                if (PlayerManager.getClanName(player).isBlank()) {

                    player.sendMSG("waypointshare.you.not.clan")
                    player.playSound(player.location, Sound.BLOCK_GLASS_BREAK, 10.0F, 10.0F)
                    return
                }


                val clan = PlayerManager.getClan(player)

                if (clan != null) {

                    ClanManager.sendClanMessage(player, clan, Component.text("hat ein waypoint geteilt"))
                    ClanManager.sendClanMessage(player, clan, Component.text(waypointMessage))

                    Bukkit.getOnlinePlayers().forEach { onlinePlayer ->
                        if (PlayerManager.getClan(onlinePlayer) == PlayerManager.getClan(player)) {
                            onlinePlayer.playSound(onlinePlayer.location, Sound.BLOCK_NOTE_BLOCK_BASS, 10.0F, 10.0F)
                        }
                    }
                }

                player.closeInventory()

            }
        }
    }
}