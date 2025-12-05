package net.derfarmer.questsystem.command

import net.derfarmer.moduleloader.commands.Command
import net.derfarmer.questsystem.FabricManager
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

object FabricTTestCommand : Command("fttest") {

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {

        val target = Bukkit.getPlayer(args[0])

        if (target == null) {
            sender.sendMessage("player not found")
            return true
        }

        if (!FabricManager.isFabricPlayer(target)) {
            sender.sendMessage("mod.notinstalled")
            return true
        }

        val list = args.toMutableList().subList(1, args.size - 1)

        FabricManager.sendRaw(target, list.joinToString(" "))

        return true
    }
}