package de.nick.waypointsharesystem.listener

import de.nick.waypointsharesystem.WaypointShareModule
import de.nick.waypointsharesystem.utils.ShareInventory
import de.nick.waypointsharesystem.utils.SkullBuilder
import io.papermc.paper.event.player.AsyncChatEvent
import net.derfarmer.moduleloader.sendMSG
import net.derfarmer.playersystem.PlayerManager
import net.derfarmer.playersystem.utils.ItemBuilder
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.*

object ChatListener : Listener {

    val playerWaypointMessages: HashMap<UUID, String> = HashMap()
    val playerWaypointWaiting = arrayListOf<Player>()

    @EventHandler
    fun onChat(event: AsyncChatEvent) {

        val messageComponent = event.message()
        val messagesString = PlainTextComponentSerializer.plainText().serialize(messageComponent)

        if (playerWaypointWaiting.contains(event.player)) {
            playerWaypointWaiting.remove(event.player)
            event.isCancelled = true

            val targetPlayer = Bukkit.getServer().getPlayer(messagesString)

            if (targetPlayer != null && targetPlayer.isOnline) {

                targetPlayer.sendMSG("")
                targetPlayer.sendMSG("waypointshare.send.privat.message", event.player.name)
                targetPlayer.sendMSG("waypointshare.send.privat.waypointmessage", getLastWaypointMessage(event.player.uniqueId).toString())
                targetPlayer.sendMSG("")
                targetPlayer.playSound(targetPlayer.location, Sound.BLOCK_NOTE_BLOCK_BASS, 10.0F, 10.0F)

                event.player.sendMessage("§7Du hast §aerfolgreich §7den Waypoint an den Spieler §a" + targetPlayer.name + " §7geschickt")
                return
            }
            event.player.sendMSG("waypointshare.player.notOnline", messagesString)
            event.player.playSound(event.player.location, Sound.BLOCK_GLASS_BREAK, 10.0F, 10.0F)
        }

        if (!messagesString.lowercase().startsWith("xaero-waypoint:")) return

        event.isCancelled = true

        playerWaypointMessages[event.player.uniqueId] = messagesString

        val sendInventory = ShareInventory().inventory


        for (i in 0.. 26) {
            sendInventory.setItem(i, ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build())
        }

        sendInventory.setItem(10, SkullBuilder()
            .setDisplayName("§6Schicke den §e§lWaypoint §6an einen Freund")
            .setSkullOwner("76cbae7246cc2c6e888587198c7959979666b4f5a4088f24e26e075f140ae6c3")
            .build())

        sendInventory.setItem(13, SkullBuilder()
            .setDisplayName("§9Schicke den §b§lWaypoint §9an alle.")
            .setSkullOwner("bdde594dead88b35bc21ad1ab238dcae411253e34a585d925258ce674c642617")
            .build())

        if (PlayerManager.getClanName(event.player).isBlank()) {
            sendInventory.setItem(16, SkullBuilder()
                .setDisplayName("§2Du bist leider in keinem Clan")
                .setSkullOwner("4a2fe01a1f7d76f3cd6ddb53d5325a398ad748d718ae720a6bc23382867d6531")
                .build())


            Bukkit.getRegionScheduler().execute(WaypointShareModule.plugin, event.player.location) {
                event.player.openInventory(sendInventory)
            }
            return
        }

        sendInventory.setItem(16, SkullBuilder()
            .setDisplayName("§2Schicke den §a§lWaypoint §2nur an Clanmitglieder.")
            .setSkullOwner("e730127542440ef0e18b4a2994fbd097e9529d59f6cedb57dbfd5fb6134a606d")
            .build())


        Bukkit.getRegionScheduler().execute(WaypointShareModule.plugin, event.player.location) {
            event.player.openInventory(sendInventory)
            event.player.playSound(event.player.location, Sound.BLOCK_CHEST_OPEN, 10.0F, 10.0F)
        }
    }
    fun getLastWaypointMessage(player: UUID): String? {
        return playerWaypointMessages[player]
    }
}