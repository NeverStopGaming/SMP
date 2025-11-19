package net.derfarmer.moduleloader.commands.provider

import org.bukkit.entity.Player

class EmptyCommandSuggestionProvider : CommandSuggestionProvider {

    override fun getSuggestions(sender: Player, fullCommand: String, lastArgument: String): List<String> {
        return emptyList()
    }

}