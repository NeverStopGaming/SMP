package net.derfarmer.discord

import net.derfarmer.discord.listener.JoinListener
import net.derfarmer.moduleloader.modules.Module

object DiscordModule : Module() {

    override fun onEnable() {
        DiscordManager

        register(JoinListener)
    }

    override fun onDisable() {
    }

    override fun onReload() {
    }
}