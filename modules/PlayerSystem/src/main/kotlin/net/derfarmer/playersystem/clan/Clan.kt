package net.derfarmer.playersystem.clan

import org.bukkit.Bukkit
import java.util.*

data class Clan(
    val name: String,
    val tag: String,
    var color: String,
    val members: MutableList<ClanMember>,
    val invited: MutableList<UUID>
)

class ClanMember(
    val uuid: UUID,
    var rank: ClanRank
) {
    fun offlinePlayer() = Bukkit.getOfflinePlayer(uuid)

    fun isOnline() = offlinePlayer().isOnline
    fun name() = offlinePlayer().name
}

enum class ClanRank(val msgKey: String, val canInvite: Boolean, val canKick: Boolean) {
    OWNER("clan.OWNER", true, true),
    ADMIN("clan.ADMIN", true, true),
    MOD("clan.MOD", true, false),
    DEFAULT("clan.DEFAULT", false, false)
}