package net.derfarmer.levelsystem.command

import net.derfarmer.levelsystem.player.PlayerExpGui
import net.derfarmer.levelsystem.player.PlayerLevelManager.addPlayerXP
import net.derfarmer.levelsystem.player.PlayerLevelManager.playerXP
import net.derfarmer.levelsystem.player.PlayerLevelManager.playerLevel
import net.derfarmer.moduleloader.commands.Command
import net.derfarmer.moduleloader.commands.annotations.CommandArgument
import net.derfarmer.moduleloader.commands.annotations.CommandSubPath
import org.bukkit.entity.Player

object PlayerExpCommand : Command("playerexp"){

    @CommandSubPath("menu")
    fun openMenu(player: Player) {
        player.openInventory(PlayerExpGui(player).inventory)
        player.sendMessage("open inv 2")
    }

    @CommandSubPath("xp <number>")
    fun setExp(player: Player, @CommandArgument("number") number: String) {
        player.playerXP = number.toInt()
        player.sendMessage("set xp $number")
    }

    @CommandSubPath("add <number>")
    fun addXP(player: Player, @CommandArgument("number") number: String) {
        player.addPlayerXP(number.toInt())
        player.sendMessage("add $number")
    }

    @CommandSubPath("level <number>")
    fun setLevel(player: Player, @CommandArgument("number") number: String) {
        player.playerLevel = number.toInt()
        player.sendMessage("set level $number")
    }
}