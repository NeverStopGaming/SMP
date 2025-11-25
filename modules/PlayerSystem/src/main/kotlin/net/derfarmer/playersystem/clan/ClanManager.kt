package net.derfarmer.playersystem.clan

import net.derfarmer.moduleloader.Redis
import net.derfarmer.moduleloader.gson
import net.derfarmer.playersystem.PlayerManager
import net.derfarmer.playersystem.clan.ClanManager.ClanResponse.*
import net.derfarmer.playersystem.events.*
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Event
import java.util.*

object ClanManager {

    private val clanNameBlackList = hashSetOf(
        "Bot", "Owner", ".", "Admin", "Developer", "Mod", "Wichtiger",
        "carl-bot", "EV1", "PandaSMP", "*", "SoundCloud", "EV1System", "SMP"
    )

    fun createClan(player: Player, name: String, tag: String, color: String): ClanResponse {

        if (clanNameBlackList.contains(name) || name.length > 32 || name.length < 3) return NAME_INVAILD

        if ((Redis.db.get("clan_$name") ?: "").isNotBlank()) return NAME_INVAILD

        if ((Redis.db.hget("clantags", tag) ?: "").isNotBlank() || tag.length < 3 || tag.length > 6) return TAG_INVAILD

        if (PlayerManager.getClanName(player).isNotBlank()) return ALREADY_IN_CLAN

        val owner = ClanMember(player.uniqueId, ClanRank.OWNER)

        val clan = Clan(name, tag, color, mutableListOf(owner), mutableListOf())

        Redis.db.hset("clantags", tag, name)

        save(clan)

        callEvent(ClanMemberJoinEvent(clan, owner, player))

        return SUCCESSFUL
    }

    fun delete(player: Player, clan: Clan): ClanResponse {

        if (getClanMember(clan, player.uniqueId)?.rank != ClanRank.OWNER) return NO_PERMISSION

        Redis.db.hdel("clantags", clan.tag)
        Redis.db.del("clan_${clan.name}")

        for (member in clan.members) {
            if (member.isOnline()) {
                callEvent(ClanMemberLeaveEvent(clan, member.offlinePlayer() as Player))
            }
        }

        return SUCCESSFUL
    }

    fun invite(inviter: Player, invitee: Player, clan: Clan): ClanResponse {

        if (clan.invited.contains(invitee.uniqueId)) return IS_ALREADY_INVITED

        if (clan.members.any { it.uuid == invitee.uniqueId }) return ALREADY_IN_CLAN

        getClanMember(clan, inviter.uniqueId)?.rank?.canInvite?.let { if (!it) return NO_PERMISSION }

        clan.invited.add(invitee.uniqueId)

        save(clan)

        callEvent(ClanInviteEvent(clan, invitee))

        return SUCCESSFUL
    }

    fun accept(accepter: Player, clan: Clan): ClanResponse {

        if (PlayerManager.getClanName(accepter).isNotBlank()) return ALREADY_IN_CLAN
        if (!clan.invited.contains(accepter.uniqueId)) return NOT_INVITED

        clan.invited.remove(accepter.uniqueId)

        val member = ClanMember(accepter.uniqueId, ClanRank.DEFAULT)

        clan.members.add(member)

        save(clan)

        callEvent(ClanMemberJoinEvent(clan, member, accepter))

        return SUCCESSFUL
    }

    fun changeColor(clan: Clan, color: String, changer: ClanMember): ClanResponse {

        if (changer.rank != ClanRank.OWNER) return NO_PERMISSION

        clan.color = color

        save(clan)

        callEvent(ClanColorChangeEvent(clan))

        return SUCCESSFUL
    }

    fun changeRank(pomoter: Player, pomotee: Player, rank: ClanRank, clan: Clan): ClanResponse {

        if (getClanMember(clan, pomoter.uniqueId)?.rank != ClanRank.OWNER) return NO_PERMISSION

        val member = getClanMember(clan, pomotee.uniqueId) ?: return NOT_IN_THE_CLAN

        // is this necessary? (jvm why no fucking pointers???)
        clan.members.remove(member)
        member.rank = rank
        clan.members.add(member)

        save(clan)

        callEvent(ClanNewRankEvent(clan, member))

        return SUCCESSFUL
    }

    fun leave(player: Player, clan: Clan): ClanResponse {

        val member = getClanMember(clan, player.uniqueId)

        if (member?.rank == ClanRank.OWNER) return CANNOT_LEAVE

        clan.members.remove(member)

        save(clan)

        callEvent(ClanMemberLeaveEvent(clan, player))

        return SUCCESSFUL
    }

    fun kick(kicker: Player, kickee: Player, clan: Clan): ClanResponse {

        getClanMember(clan, kicker.uniqueId)?.rank?.canKick?.let { if (!it) return NO_PERMISSION }

        val member = getClanMember(clan, kickee.uniqueId) ?: return NOT_IN_THE_CLAN

        if(member.rank == ClanRank.OWNER) return NO_PERMISSION

        clan.members.remove(member)

        save(clan)

        callEvent(ClanMemberLeaveEvent(clan, kickee))

        return SUCCESSFUL
    }

    fun sendClanMessage(player: Player, clan: Clan, message: Component): ClanResponse {

        callEvent(ClanMessageEvent(clan, player, message))

        return SUCCESSFUL
    }

    fun getClanMember(clan: Clan, uuid: UUID): ClanMember? {
        return clan.members.find { it.uuid == uuid }
    }

    fun getClan(clanName: String): Clan? {

        val json = Redis.db.get("clan_$clanName")

        return gson.fromJson(json, Clan::class.java)
    }

    private fun save(clan: Clan) {
        Redis.db.set("clan_${clan.name}", gson.toJson(clan))
    }

    private fun callEvent(event: Event) {
        Bukkit.getPluginManager().callEvent(event)
    }

    enum class ClanResponse {
        NO_PERMISSION,
        SUCCESSFUL,
        NOT_INVITED,
        IS_ALREADY_INVITED,
        NAME_INVAILD,
        TAG_INVAILD,
        ALREADY_IN_CLAN,
        NOT_IN_THE_CLAN,
        CANNOT_LEAVE,
    }
}