package net.derfarmer.playersystem

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import net.derfarmer.moduleloader.*
import net.derfarmer.playersystem.clan.Clan
import net.derfarmer.playersystem.clan.ClanManager
import net.derfarmer.playersystem.clan.ClanRank
import net.derfarmer.playersystem.events.*
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

object PlayerManager {

    fun getPlayerValue(player: OfflinePlayer, key: String): String? = Redis.db.hget("player_${player.uniqueId}", key)
    fun setPlayerValue(player: OfflinePlayer, key: String, value: String): Long? =
        Redis.db.hset("player_${player.uniqueId}", key, value)

    fun getClanName(player: Player): String = getPlayerValue(player, "clan") ?: ""
    fun getClan(player: Player) = ClanManager.getClan(getClanName(player))
    private fun setClanName(player: Player, clanName: String) = setPlayerValue(player, "clan", clanName)

    fun getBannedTime(player: OfflinePlayer): Long = (getPlayerValue(player, "banned") ?: "0").toLong()
    fun isBanned(player: OfflinePlayer): Boolean = getBannedTime(player) != 0L
    fun setBannedTime(player: OfflinePlayer, time: Long): Long? = setPlayerValue(player, "banned", time.toString())

    private fun setSuffix(player: Player, clan: Clan?) {
        if (clan == null) {
            player.playerListName(player.name())
            return
        }

        val parsed = mm.deserialize("<${clan.color}>${player.name} <dark_gray>[<${clan.color}>${clan.tag}<dark_gray>]")

        player.playerListName(parsed)
    }

    object PlayerManagerListener : Listener {

        @EventHandler
        fun onJoin(event: PlayerJoinEvent) {

            val player = event.player

            if (isBanned(player)) {
                event.joinMessage(Component.empty())
                Bukkit.getPluginManager().callEvent(PlayerBannedEvent(player, getBannedTime(player)))
                return
            }
            event.joinMessage(Message["join", event.player.name])

            val clanName = getClanName(player)
            val clan = ClanManager.getClan(clanName)

            if (clanName.isNotBlank()) {
                if (clan == null) {
                    setClanName(player, "")
                    return
                }
            }

            setSuffix(player, clan)
        }

        @EventHandler
        fun onQuit(event: PlayerQuitEvent) {
            if (isBanned(event.player)) {
                event.quitMessage(Component.empty())
            }
            event.quitMessage(Message["leave", event.player.name])
        }

        @EventHandler
        fun onClanJoin(event: ClanMemberJoinEvent) {
            setSuffix(event.player, event.clan)
            setClanName(event.player, event.clan.name)

            if (event.clanMember.rank == ClanRank.OWNER) return
            val msg = Message["clan.join", event.player.name]

            sendToAllClanMember(event.clan, msg)
        }

        @EventHandler
        fun onClanInvite(event: ClanInviteEvent) {
            event.player.sendMSG("clan.invited", event.clan.name, event.clan.name)
        }

        @EventHandler
        fun onClanLeave(event: ClanMemberLeaveEvent) {
            setSuffix(event.player, null)
            setClanName(event.player, "")

            val msg = Message["clan.leave", event.player.name]
            sendToAllClanMember(event.clan, msg)

            event.player.sendMSG("clan.youLeft", event.clan.name)
        }

        @EventHandler
        fun onClanNewRank(event: ClanNewRankEvent) {
            val msg = Message["clan.newRank", event.clanMember.name()
                ?: "ERROR"].append(Message.get(event.clanMember.rank.msgKey, withPrefix = false))
            sendToAllClanMember(event.clan, msg)
        }

        @EventHandler
        fun onClanColorChange(event: ClanColorChangeEvent) {
            for (member in event.clan.members) {

                val player = if (member.offlinePlayer() is Player) member.offlinePlayer() as Player else continue

                setSuffix(player, event.clan)
            }
        }

        @EventHandler
        fun onClanMessage(event: ClanMessageEvent) {
            val msg = Message["clan.chat.msg", event.sender.name].append(event.message)
            sendToAllClanMember(event.clan, msg)
        }

        @EventHandler
        fun onBan(event: PlayerBannedEvent) {
            val p = event.player

            if (event.timestamp < 0L) {
                p.kick(p.getMSG("ban.perma", withPrefix = false))
            }

            val zoneId = ZoneId.of("Europe/Berlin")
            val time = Instant.ofEpochSecond(event.timestamp).atZone(zoneId).toLocalDateTime()

            if (LocalDateTime.now().isAfter(time)) {
                PlayerModule.plugin.launch(PlayerModule.plugin.entityDispatcher(p)) {
                    setBannedTime(p, 0L)
                    p.kick(Component.text("Rejoin"))
                }
                return
            }

            val germanFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.GERMANY)

            PlayerModule.plugin.launch(PlayerModule.plugin.entityDispatcher(p)) {
                p.kick(p.getMSG("ban.time", time.format(germanFormatter), withPrefix = false))
            }
        }

        private fun sendToAllClanMember(clan: Clan, message: Component) {
            for (member in clan.members) {

                val receiver = member.offlinePlayer()

                if (receiver !is Player) continue

                receiver.sendMessage(message)
            }
        }
    }
}