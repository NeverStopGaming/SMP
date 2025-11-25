package net.derfarmer.levelsystem.command

import net.derfarmer.levelsystem.FabricManager
import net.derfarmer.moduleloader.commands.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object FabricDataCommand : Command("fabricdata") {

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        if (sender !is Player) return false

        val parts = args[0].split(";")

        when (parts[0][0]){
            '0' -> {
                FabricManager.fabricPlayers.add(sender)
            }
        }

        return true
    }
}