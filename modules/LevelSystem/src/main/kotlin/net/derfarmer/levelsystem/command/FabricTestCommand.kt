package net.derfarmer.levelsystem.command

import net.derfarmer.levelsystem.FabricManager
import net.derfarmer.moduleloader.commands.Command
import net.derfarmer.moduleloader.commands.annotations.CommandArgument
import net.derfarmer.moduleloader.commands.annotations.CommandSubPath
import net.derfarmer.moduleloader.sendMSG
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object FabricTestCommand : Command("ftest"){

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        if (sender !is Player) return false

        if (!FabricManager.fabricPlayers.contains(sender)) {
            sender.sendMSG("mod.notinstalled")
            return true
        }
        sender.sendMessage("fabricdata ${args.joinToString(" ")}")
        return true
    }
}