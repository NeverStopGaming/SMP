package net.derfarmer.utilmodule.commands

import net.derfarmer.moduleloader.commands.Command
import net.derfarmer.moduleloader.commands.annotations.CommandArgument
import net.derfarmer.moduleloader.commands.annotations.CommandSubPath
import net.derfarmer.moduleloader.sendMSG
import net.derfarmer.playersystem.PlayerManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object UnBanCommand : Command("unban") {

    @CommandSubPath("<target>")
    fun handle(player: Player,@CommandArgument("target") targetName : String) {

        val target = Bukkit.getOfflinePlayer(targetName)

        PlayerManager.setBannedTime(target, 0L)

        player.sendMSG("unban.success", target.name!!)
    }
}