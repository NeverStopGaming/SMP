package net.derfarmer.questsystem.command

import net.derfarmer.moduleloader.commands.Command
import net.derfarmer.moduleloader.commands.annotations.CommandArgument
import net.derfarmer.moduleloader.commands.annotations.CommandSubPath
import net.derfarmer.moduleloader.commands.provider.PlayerCommandSuggestionProvider
import net.derfarmer.moduleloader.sendMSG
import net.derfarmer.questsystem.FabricManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object ModsCommand : Command("mods") {

    @CommandSubPath("<targetName>", "fabric.mods")
    fun handle(
        player: Player,
        @CommandArgument("targetName", PlayerCommandSuggestionProvider::class) targetName: String
    ) {

        val target = Bukkit.getPlayer(targetName)

        if (target == null) {
            player.sendMSG("player.notfound")
            return
        }

        FabricManager.requestModes(player) { data ->
            val msg = data.split(";").joinToString { it.split("%%%")[0] }
            player.sendMSG("mods.player", target.name, msg)
        }
    }
}