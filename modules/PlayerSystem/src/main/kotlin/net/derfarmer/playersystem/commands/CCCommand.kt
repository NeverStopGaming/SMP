package net.derfarmer.playersystem.commands

import net.derfarmer.moduleloader.commands.Command
import net.derfarmer.moduleloader.sendMSG
import net.derfarmer.playersystem.PlayerManager
import net.derfarmer.playersystem.clan.ClanManager
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object CCCommand : Command("cc", alias = arrayOf("clanchat")) {

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {

        val player = sender as Player

        if (args.isEmpty()) {
            player.sendMSG("clan.chat.noMessage")
            return false
        }

        val clan = PlayerManager.getClan(player) ?: run {
            player.sendMSG("clan.notInAClan")
            return false
        }

        ClanManager.sendClanMessage(sender, clan, Component.text(args.joinToString { " " }))

        return true
    }
}