package net.derfarmer.utilmodule.commands

import net.derfarmer.moduleloader.commands.Command
import net.derfarmer.moduleloader.commands.annotations.CommandArgument
import net.derfarmer.moduleloader.commands.annotations.CommandSubPath
import net.derfarmer.moduleloader.commands.provider.PlayerCommandSuggestionProvider
import net.derfarmer.moduleloader.sendMSG
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object InvSeeCommand : Command("invsee") {

    @CommandSubPath("<target>", permission = "utils.invsee")
    fun invSee(player: Player, @CommandArgument("target", PlayerCommandSuggestionProvider::class) targetName: String) {
        val targetPlayer = Bukkit.getPlayer(targetName)

        if (targetPlayer == null) {
            player.sendMSG("utils.playerNotFound")
            return
        }

        player.openInventory(targetPlayer.inventory)
    }
}