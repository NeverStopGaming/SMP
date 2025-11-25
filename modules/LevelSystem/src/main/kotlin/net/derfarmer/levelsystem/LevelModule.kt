package net.derfarmer.levelsystem

import net.derfarmer.levelsystem.command.FabricDataCommand
import net.derfarmer.levelsystem.command.FabricTestCommand
import net.derfarmer.levelsystem.command.PlayerExpCommand
import net.derfarmer.levelsystem.listener.FabricListener
import net.derfarmer.levelsystem.listener.InvListener
import net.derfarmer.moduleloader.modules.Module

object LevelModule : Module() {
    override fun onEnable() {
        register(InvListener)
        register(FabricListener)

        register(PlayerExpCommand)
        register(FabricTestCommand)
        register(FabricDataCommand)
    }

    override fun onDisable() {
    }

    override fun onReload() {
    }
}