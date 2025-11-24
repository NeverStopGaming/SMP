package net.derfarmer.levelsystem.command

import net.derfarmer.levelsystem.gui.PlayerExpGui
import net.derfarmer.levelsystem.player.PlayerLevelManager.playerExp
import net.derfarmer.levelsystem.player.PlayerLevelManager.playerLevel
import net.derfarmer.moduleloader.commands.Command
import net.derfarmer.moduleloader.commands.annotations.CommandArgument
import net.derfarmer.moduleloader.commands.annotations.CommandSubPath
import org.bukkit.entity.Player

object PlayerExpCommand : Command("playerexp"){

    @CommandSubPath("menu")
    fun openMenu(player: Player) {
        player.openInventory(PlayerExpGui(player).inventory)
        player.sendMessage("open inv 9")
    }

    @CommandSubPath("exp <number>")
    fun setExp(player: Player, @CommandArgument("number") number: String) {
        player.playerExp = number.toInt()
        player.sendMessage("set $number")
    }

    @CommandSubPath("level <number>")
    fun setLevel(player: Player, @CommandArgument("number") number: String) {
        player.playerLevel = number.toInt()
        player.sendMessage("set $number")
    }
}