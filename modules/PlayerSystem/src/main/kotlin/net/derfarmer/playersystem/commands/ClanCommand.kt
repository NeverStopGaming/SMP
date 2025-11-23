package net.derfarmer.playersystem.commands

import net.derfarmer.moduleloader.Message
import net.derfarmer.moduleloader.Redis
import net.derfarmer.moduleloader.commands.Command
import net.derfarmer.moduleloader.commands.annotations.CommandArgument
import net.derfarmer.moduleloader.commands.annotations.CommandSubPath
import net.derfarmer.moduleloader.commands.provider.PlayerCommandSuggestionProvider
import net.derfarmer.moduleloader.sendMSG
import net.derfarmer.playersystem.PlayerManager
import net.derfarmer.playersystem.clan.Clan
import net.derfarmer.playersystem.clan.ClanManager
import net.derfarmer.playersystem.clan.ClanRank
import net.derfarmer.playersystem.utils.ColorPickerUI
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object ClanCommand : Command("clan") {

    override fun default(player: Player) {
        player.sendMSG("clan.help")
    }

    @CommandSubPath("create <name> <tag>")
    fun create(player: Player, @CommandArgument("name") name: String, @CommandArgument("tag") tag: String) {
        val callback = { color: String ->
            val res = ClanManager.createClan(player, name, tag, color)

            when (res) {
                ClanManager.ClanResponse.SUCCESSFUL -> player.sendMSG("clan.created")
                ClanManager.ClanResponse.NAME_INVAILD -> player.sendMSG("clan.nameInvalid")
                ClanManager.ClanResponse.TAG_INVAILD -> player.sendMSG("clan.tagInvalid")
                ClanManager.ClanResponse.ALREADY_IN_CLAN -> player.sendMSG("clan.alreadyInClan")
                else -> player.sendMSG("error.internal")
            }
        }
        ColorPickerUI(player, callback)
    }

    fun info(player: Player, clan: Clan?) {
        if (clan == null) {
            player.sendMSG("clan.notFound")
            return
        }

        player.sendMSG(
            "clan.info.header",
            clan.color,
            clan.name,
            clan.color,
            clan.tag,
            clan.color,
            clan.members.size.toString()
        )
        for (member in clan.members) {
            player.sendMSG(
                "clan.info.member",
                clan.color,
                member.offlinePlayer().name ?: "ERROR",
                Message.getRaw(member.rank.msgKey),
                withPrefix = false
            )
        }
    }

    @CommandSubPath("info")
    fun handleInfo(player: Player) {
        info(player, PlayerManager.getClan(player))
    }

    @CommandSubPath("info <clanTag>")
    fun handleInfo(player: Player, @CommandArgument("clanTag") clanTag: String) {
        info(player, ClanManager.getClan(Redis.db.hget("clantags", clanTag)))
    }

    @CommandSubPath("delete")
    fun delete(player: Player) {
        val clanName = PlayerManager.getClanName(player)

        if (clanName.isBlank()) {
            player.sendMSG("clan.notInAClan")
            return
        }

        player.sendMSG("clan.deleteConfirm", clanName, player.name)
    }

    @CommandSubPath("delete <clanName> <playerName>")
    fun delete(
        player: Player,
        @CommandArgument("clanName") clanName: String,
        @CommandArgument("playerName") playerName: String
    ) {
        val clan = PlayerManager.getClan(player) ?: run {
            player.sendMSG("clan.notInAClan")
            return
        }
        if (clan.name != clanName || player.name != playerName) return

        val res = ClanManager.delete(player, clan)

        when (res) {
            ClanManager.ClanResponse.NO_PERMISSION -> player.sendMSG("clan.notPermission")
            ClanManager.ClanResponse.SUCCESSFUL -> player.sendMSG("clan.deleted")
            else -> player.sendMSG("error.internal")
        }
    }

    @CommandSubPath("invite <inviteeName>")
    fun invite(player: Player, @CommandArgument("inviteeName") inviteeName: String) {
        val clan = PlayerManager.getClan(player) ?: run {
            player.sendMSG("clan.notInAClan")
            return
        }

        val invitee = Bukkit.getPlayer(inviteeName) ?: run {
            player.sendMSG("clan.playerNotFound")
            return
        }

        val res = ClanManager.invite(player, invitee, clan)

        when (res) {
            ClanManager.ClanResponse.NO_PERMISSION -> player.sendMSG("clan.notPermission")
            ClanManager.ClanResponse.SUCCESSFUL -> player.sendMSG("clan.invited", invitee.name)
            ClanManager.ClanResponse.IS_ALREADY_INVITED -> player.sendMSG("clan.isAlreadyInvited")
            ClanManager.ClanResponse.ALREADY_IN_CLAN -> player.sendMSG("clan.alreadyInClan")
            else -> player.sendMSG("error.internal")
        }
    }

    @CommandSubPath("accept <clanName>")
    fun accept(player: Player, @CommandArgument("clanName") clanName: String) {
        when (ClanManager.accept(player, ClanManager.getClan(clanName) ?: return)) {
            ClanManager.ClanResponse.SUCCESSFUL -> player.sendMSG("clan.accepted")
            ClanManager.ClanResponse.NOT_INVITED -> player.sendMSG("clan.notInvited")
            else -> player.sendMSG("error.internal")
        }
    }

    @CommandSubPath("leave")
    fun leave(player: Player) {
        val clan = PlayerManager.getClan(player) ?: run {
            player.sendMSG("clan.notInAClan")
            return
        }

        if (ClanManager.leave(player, clan) != ClanManager.ClanResponse.SUCCESSFUL) {
            player.sendMSG("error.internal")
            return
        }

        player.sendMSG("clan.leaved")
    }

    @CommandSubPath("kick <kickeeName>")
    fun kick(
        player: Player,
        @CommandArgument("kickeeName", PlayerCommandSuggestionProvider::class) kickeeName: String
    ) {
        val clan = PlayerManager.getClan(player) ?: run {
            player.sendMSG("clan.notInAClan")
            return
        }

        val kickee = Bukkit.getPlayer(kickeeName) ?: run {
            player.sendMSG("clan.playerNotFound")
            return
        }

        when (ClanManager.kick(player, kickee, clan)) {
            ClanManager.ClanResponse.NO_PERMISSION -> player.sendMSG("clan.notPermission")
            ClanManager.ClanResponse.SUCCESSFUL -> player.sendMSG("clan.kicked", kickee.name)
            ClanManager.ClanResponse.NOT_IN_THE_CLAN -> player.sendMSG("clan.notInTheClan")
            else -> player.sendMSG("error.internal")
        }
    }

    @CommandSubPath("changeColor")
    fun changeColor(player: Player) {
        val clan = PlayerManager.getClan(player) ?: run {
            player.sendMSG("clan.notInAClan")
            return
        }

        val member = ClanManager.getClanMember(clan, player.uniqueId) ?: run {
            player.sendMSG("error.internal")
            return
        }


        val callback = { color: String ->
            run {
                when (ClanManager.changeColor(clan, color, member)) {
                    ClanManager.ClanResponse.NO_PERMISSION -> player.sendMSG("clan.notPermission")
                    ClanManager.ClanResponse.SUCCESSFUL -> player.sendMSG("clan.colorChanged")
                    else -> player.sendMSG("error.internal")
                }
            }
        }

        ColorPickerUI(player, callback)
    }

    @CommandSubPath("promote <memberName>")
    fun promote(
        player: Player,
        @CommandArgument("memberName", PlayerCommandSuggestionProvider::class) memberName: String
    ) {
        val clan = PlayerManager.getClan(player) ?: run {
            player.sendMSG("clan.notInAClan")
            return
        }

        val target = Bukkit.getPlayer(memberName) ?: run {
            player.sendMSG("clan.playerNotFound")
            return
        }

        val member = ClanManager.getClanMember(clan, target.uniqueId) ?: run {
            player.sendMSG("clan.notInTheClan")
            return
        }


        val newRank = when (member.rank) {
            ClanRank.OWNER -> {
                player.sendMSG("clan.canNotPromote")
                return
            }

            ClanRank.ADMIN -> {
                player.sendMSG("clan.canNotPromote")
                return
            }

            ClanRank.MOD -> ClanRank.ADMIN
            ClanRank.DEFAULT -> ClanRank.MOD
        }

        when (ClanManager.changeRank(player, target, newRank, clan)) {
            ClanManager.ClanResponse.NO_PERMISSION -> player.sendMSG("clan.notPermission")
            ClanManager.ClanResponse.SUCCESSFUL -> player.sendMSG("clan.promoted", target.name)
            else -> player.sendMSG("error.internal")
        }
    }

    @CommandSubPath("demote <memberName>")
    fun demote(
        player: Player,
        @CommandArgument("memberName", PlayerCommandSuggestionProvider::class) memberName: String
    ) {
        val clan = PlayerManager.getClan(player) ?: run {
            player.sendMSG("clan.notInAClan")
            return
        }

        val target = Bukkit.getPlayer(memberName) ?: run {
            player.sendMSG("clan.playerNotFound")
            return
        }

        val member = ClanManager.getClanMember(clan, target.uniqueId) ?: run {
            player.sendMSG("clan.notInTheClan")
            return
        }


        val newRank = when (member.rank) {
            ClanRank.OWNER -> {
                player.sendMSG("clan.canNotDemote")
                return
            }

            ClanRank.ADMIN -> ClanRank.MOD
            ClanRank.MOD -> ClanRank.DEFAULT
            ClanRank.DEFAULT -> {
                player.sendMSG("clan.canNotDemote")
                return
            }
        }

        when (ClanManager.changeRank(player, target, newRank, clan)) {
            ClanManager.ClanResponse.NO_PERMISSION -> player.sendMSG("clan.notPermission")
            ClanManager.ClanResponse.SUCCESSFUL -> player.sendMSG("clan.demoted", target.name)
            else -> player.sendMSG("error.internal")
        }
    }
}