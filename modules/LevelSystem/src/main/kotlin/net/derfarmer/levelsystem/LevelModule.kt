package net.derfarmer.levelsystem

import net.derfarmer.levelsystem.command.PlayerExpCommand
import net.derfarmer.levelsystem.listener.InvListener
import net.derfarmer.moduleloader.modules.Module

object LevelModule : Module() {
    override fun onEnable() {
        register(InvListener)
        register(PlayerExpCommand)
    }

    override fun onDisable() {
    }

    override fun onReload() {
    }
}