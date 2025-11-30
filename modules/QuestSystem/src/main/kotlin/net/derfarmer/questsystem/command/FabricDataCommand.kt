package net.derfarmer.questsystem.command

import net.derfarmer.moduleloader.commands.Command
import net.derfarmer.questsystem.FabricManager
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object FabricDataCommand : Command("fabricdata") {

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        if (sender !is Player) return false

        FabricManager.parseMessage(sender, args.joinToString(" "))
        return true
    }
}