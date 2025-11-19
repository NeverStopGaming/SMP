package net.derfarmer.moduleloader.commands

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.command.Command as BukkitCommand


open class Command(name: String, alias: Array<String> = emptyArray<String>()) :
    BukkitCommand(name, "", "", alias.toMutableList()) {


    val commands = arrayListOf<CommandData>()

    init {
        aliases.add(name)
    }

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {

        val player = sender as Player

        CommandManager.handleCommand(player, this, args.joinToString(" "))

        return true
    }

    /**
     * Provides tab complete suggestions for the specified invocation.
     *
     * @return the tab complete suggestions
     */
    override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>): List<String> =
        CommandManager.handleTabComplete(sender as Player, this, args.joinToString(" "))

    open fun default(player: Player) {

    }
}