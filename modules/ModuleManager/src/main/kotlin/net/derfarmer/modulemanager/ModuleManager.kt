package net.derfarmer.modulemanager

import net.derfarmer.moduleloader.commands.CommandManager
import net.derfarmer.moduleloader.modules.Module

object ModuleManager : Module() {
    override fun onEnable() {
        CommandManager.registerCommand(this.plugin, ModuleCommand)
    }

    override fun onDisable() {
        CommandManager.unregisterCommand(this.plugin, ModuleCommand)
    }

    override fun onReload() {
        onDisable()
    }
}