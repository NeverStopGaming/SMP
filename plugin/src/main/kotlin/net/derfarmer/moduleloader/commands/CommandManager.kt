package net.derfarmer.moduleloader.commands

import net.derfarmer.moduleloader.commands.annotations.CommandArgument
import net.derfarmer.moduleloader.commands.annotations.CommandSubPath
import net.derfarmer.moduleloader.gson
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandMap
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.lang.reflect.Field

object CommandManager {

    fun handleCommand(player: Player, command: Command, argument: String) {

        val args = argument.split(" ") //args speicher igendwann mal im command für performens

        val commandData = getMatchingCommandData(command, argument) ?: run {
            command.default(player)
            return
        }

        if (!player.hasPermission(commandData.permission) && commandData.permission.isNotEmpty()) {
            return
        }

        val list = arrayListOf<Any>()

        list.add(player)

        for (parameterData in commandData.parameterDataList) {

            val parameterName: String = parameterData.name
            val indexOfParameter = commandData.getIndexOfParameter(parameterName)
            val parameterValue = args[indexOfParameter]

            val obj = gson.fromJson(parameterValue, parameterData.type)

            if (obj == null) {
                player.sendMessage(Component.text("§cCan't parse parameter at index $indexOfParameter(\"$parameterValue\") to class ${parameterData.type.simpleName}"))
                if (parameterData.type.isEnum) {
                    val clazz = parameterData.type as Class<out Enum<*>>
                    val enumValues = clazz.enumConstants
                    player.sendMessage(Component.text("Allowed are: " + enumValues.joinToString(", ")))
                }

                return
            }
            list.add(obj)
        }

        try {
            commandData.method.invoke(commandData.source, *list.toTypedArray())
        } catch (_: Exception) {
        }

    }

    fun handleTabComplete(player: Player, command: Command, message: String): List<String> {
        val messageArray = message.split(" ").map { it.trim() }
        val suggestions = HashSet<String>()
        val dataList = getAvailableArgsMatchingCommandData(messageArray.drop(1).joinToString(" "), command)

        dataList.forEach {

            val path = it.path
            val pathArray = path.split(" ")

            if (pathArray.size == messageArray.lastIndex) {
                return@forEach
            }

            if (messageArray.isNotEmpty() && messageArray[0].isNotEmpty()) {
                for ((index, pathPart) in pathArray.withIndex()) {

                    if (pathPart.startsWith("<")) continue

                    if (messageArray[index] != pathPart) return@forEach
                }
            }

            val currentPathValue = pathArray[messageArray.lastIndex]

            val permission = it.permission
            if (permission.isEmpty() || player.hasPermission(permission)) {
                if (isParameter(currentPathValue)) {
                    val commandParameterData = it.getParameterDataByNameWithBraces(currentPathValue) ?: return@forEach
                    suggestions.addAll(
                        commandParameterData.provider.getSuggestions(
                            player,
                            message,
                            messageArray.last()
                        )
                    )
                } else {
                    suggestions.add(currentPathValue)
                }
            }
        }

        return suggestions.filter { it.lowercase().startsWith(messageArray.last().lowercase()) }
    }

    private fun getAvailableArgsMatchingCommandData(message: String, command: Command): List<CommandData> {
        val messageArray = message.split(" ")
        val dataList = getCommandDataByMinimumArgumentLength(messageArray.size, command)
        return dataList.filter { commandData ->
            commandData.getAllPathsWithAliases().any {
                val path = it.trim()
                val pathArray = path.split(" ")

                messageArray.withIndex().filter { it.index < pathArray.size }.all {
                    val pathValue = pathArray[it.index]
                    isParameter(pathValue) || it.value.equals(pathValue, ignoreCase = true)
                }
            }
        }
    }

    private fun getCommandDataByMinimumArgumentLength(length: Int, command: Command) =
        command.commands.filter { it.path.split(" ").size >= length }

    fun unregisterCommand(plugin: JavaPlugin, command: Command) {
        val commandField: Field = plugin.server::class.java.getDeclaredField("commandMap")
        commandField.isAccessible = true
        val commandMap: CommandMap = commandField.get(plugin.server) as CommandMap
        commandMap.knownCommands.remove(command.name, commandMap.getCommand(command.name))
        command.unregister(commandMap)
    }

    fun registerCommand(plugin: JavaPlugin, command: Command) {

        val commandClass = command::class.java

        val commandField: Field = plugin.server::class.java.getDeclaredField("commandMap")
        commandField.isAccessible = true
        val commandMap: CommandMap = commandField.get(plugin.server) as CommandMap
        commandMap.register(command.name, command)

        for (method in commandClass.declaredMethods) {

            val commandSubPath = method.getAnnotation(CommandSubPath::class.java) ?: continue
            val commandArgs = ArrayList<CommandParameterData>()

            for (parameter in method.parameters) {

                if (method.parameters.indexOf(parameter) != 0) {

                    val commandArgument = parameter.getAnnotation(CommandArgument::class.java)

                    if (commandArgument != null) {
                        commandArgs.add(
                            CommandParameterData(
                                parameter.type,
                                commandArgument.suggestionProvider.java.getConstructor().newInstance(),
                                commandArgument.name
                            )
                        )
                    }
                }
            }

            command.commands.add(
                CommandData(
                    commandSubPath.path,
                    method,
                    command,
                    commandSubPath.permission,
                    commandArgs
                )
            )

        }
    }

    private fun getCommandDataByArgLength(command: Command, length: Int): List<CommandData> =
        command.commands.filter { it.path.split(" ").size == length }

    private fun getMatchingCommandData(command: Command, message: String): CommandData? {

        val messageArray = message.split(" ")
        val commandDataList = getCommandDataByArgLength(command, messageArray.size)

        return commandDataList.firstOrNull { commandData ->

            val path = commandData.path
            val pathArray = path.split(" ")

            pathArray.withIndex()
                .all { isParameter(it.value) || it.value.equals(messageArray[it.index], ignoreCase = true) }
        }
    }

    private fun isParameter(s: String) = s.startsWith("<") && s.endsWith(">")

}