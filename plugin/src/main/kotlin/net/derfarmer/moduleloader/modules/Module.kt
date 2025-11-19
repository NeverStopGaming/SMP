package net.derfarmer.moduleloader.modules

import net.derfarmer.moduleloader.commands.Command
import net.derfarmer.moduleloader.commands.CommandManager
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Suppress("MemberVisibilityCanBePrivate")
abstract class Module {

    /** This field is automatically assigned from plugin.json file. */
    val name = "Module"

    /** This field is automatically assigned from plugin.json file. */
    val authors = arrayOf("DerFarmer")

    /** This field is automatically assigned from the Commandmanager. */
    val plugin: JavaPlugin = Bukkit.getPluginManager().plugins.first { it.name == "ModuleLoader" } as JavaPlugin

    val logger: Logger = LoggerFactory.getLogger("Module")

    abstract fun onEnable()

    abstract fun onDisable()

    abstract fun onReload()

    private val listenerHandler = arrayListOf<Listener>()
    private val commandHandler = arrayListOf<Command>()

    fun register(command: Command) {
        CommandManager.registerCommand(plugin, command)
        commandHandler.add(command)
    }

    fun register(listener: Listener) {
        Bukkit.getPluginManager().registerEvents(listener, plugin)
        listenerHandler.add(listener)
    }

    fun unregisterAll() {
        commandHandler.forEach {
            CommandManager.unregisterCommand(plugin, it)
        }
        listenerHandler.forEach {
            HandlerList.unregisterAll(it)
        }
    }
}
