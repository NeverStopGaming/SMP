package net.derfarmer.playersystem

import net.derfarmer.moduleloader.modules.Module
import net.derfarmer.playersystem.commands.CCCommand
import net.derfarmer.playersystem.commands.ClanCommand
import net.derfarmer.playersystem.utils.ColorPickerListener

object PlayerModule : Module() {

    override fun onEnable() {
        register(ClanCommand)
        register(CCCommand)

        register(PlayerManager.PlayerManagerListener)
        register(ColorPickerListener)
    }

    override fun onDisable() {
    }

    override fun onReload() {
    }
}