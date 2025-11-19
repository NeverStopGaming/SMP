package net.derfarmer.moduleloader.commands.provider

import org.bukkit.entity.Player

interface CommandSuggestionProvider {

    /**
     * Returns the suggestions for an argument
     * @param sender the sender og the tab request
     * @param fullCommand the full command so far including the last argument
     * @param lastArgument the last argument of the [fullCommand]
     * @return a list with arguments to suggest
     */
    fun getSuggestions(sender: Player, fullCommand: String, lastArgument: String): List<String>

}