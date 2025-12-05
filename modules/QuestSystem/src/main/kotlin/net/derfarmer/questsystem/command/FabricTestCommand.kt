package net.derfarmer.questsystem.command

import net.derfarmer.moduleloader.commands.Command
import net.derfarmer.moduleloader.sendMSG
import net.derfarmer.questsystem.FabricManager
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object FabricTestCommand : Command("ftest") {

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        if (sender !is Player) return false

        if (!FabricManager.isFabricPlayer(sender)) {
            sender.sendMSG("mod.notinstalled")
            return true
        }

        FabricManager.sendRaw(sender, args.joinToString(" "))

        return true
    }
}