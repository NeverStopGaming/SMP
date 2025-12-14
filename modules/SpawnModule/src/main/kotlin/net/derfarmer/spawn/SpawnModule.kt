package net.derfarmer.spawn

import net.derfarmer.moduleloader.modules.Module
import net.derfarmer.spawn.commands.PVPCommand
import net.derfarmer.spawn.commands.StartCommand
import net.derfarmer.spawn.listener.PlayerFlyStart
import net.derfarmer.spawn.listener.SpawnListener
import net.derfarmer.spawn.listener.StartListener

object SpawnModule : Module(){

    override fun onEnable() {
        register(PlayerFlyStart)
        register(SpawnListener)

        register(PVPCommand)
        register(StartCommand)
    }

    override fun onDisable() {
    }

    override fun onReload() {
    }
}