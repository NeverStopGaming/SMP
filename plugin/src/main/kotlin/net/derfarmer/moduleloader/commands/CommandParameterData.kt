package net.derfarmer.moduleloader.commands

import net.derfarmer.moduleloader.commands.provider.CommandSuggestionProvider

class CommandParameterData(
    val type: Class<*>,
    val provider: CommandSuggestionProvider,
    val name: String
) {
}