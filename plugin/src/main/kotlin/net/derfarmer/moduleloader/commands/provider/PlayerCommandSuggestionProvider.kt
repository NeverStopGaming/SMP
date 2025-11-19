package net.derfarmer.moduleloader.commands.provider

import org.bukkit.Bukkit
import org.bukkit.entity.Player

class PlayerCommandSuggestionProvider : CommandSuggestionProvider {

    override fun getSuggestions(sender: Player, fullCommand: String, lastArgument: String): List<String> {
        return Bukkit.getOnlinePlayers().map { it.name }
    }

}