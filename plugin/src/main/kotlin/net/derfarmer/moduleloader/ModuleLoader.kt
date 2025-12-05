package net.derfarmer.moduleloader

import com.google.gson.Gson
import net.derfarmer.moduleloader.commands.CommandManager
import net.derfarmer.moduleloader.modules.ModuleCommand
import net.derfarmer.moduleloader.modules.ModuleManager
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.plugin.java.JavaPlugin

class ModuleLoader : JavaPlugin() {

    override fun onEnable() {
        ModuleManager
        CommandManager.registerCommand(this, ModuleCommand)
    }

    override fun onDisable() {
        ModuleManager.disableAllModules()
    }
}

val gson = Gson()
val mm = MiniMessage.miniMessage()
