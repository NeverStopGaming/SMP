package net.derfarmer.utilmodule.commands

import net.derfarmer.moduleloader.commands.Command
import net.derfarmer.moduleloader.commands.annotations.CommandArgument
import net.derfarmer.moduleloader.commands.annotations.CommandSubPath
import net.derfarmer.moduleloader.sendMSG
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object PingCommand : Command("ping") {

    fun sendPingMessage(player: Player, target: Player) {
        val ping = target.ping

        if (ping == 0) {
            player.sendMSG("utils.noPing")
        } else {
            player.sendMSG("utils.ping", target.name, ping.toString())
        }
    }

    @CommandSubPath
    fun ping(player: Player) {
        sendPingMessage(player, player)
    }

    @CommandSubPath("<target>")
    fun ping(player: Player, @CommandArgument("target") targetName: String) {
        val targetPlayer = Bukkit.getPlayer(targetName)

        if (targetPlayer == null) {
            player.sendMSG("utils.playerNotFound")
            return
        }

        sendPingMessage(player, player)
    }
}