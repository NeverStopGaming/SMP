package net.derfarmer.spawn.commands

import net.derfarmer.moduleloader.commands.Command
import net.derfarmer.moduleloader.commands.annotations.CommandSubPath
import net.derfarmer.spawn.SpawnModule
import net.derfarmer.spawn.listener.StartListener
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList

object StartCommand : Command("startServer"){

    @CommandSubPath("register", permission = "server.start")
    fun handle(player: Player) {
        SpawnModule.register(StartListener)
    }

    @CommandSubPath(permission = "server.start")
    fun handleStart(player: Player) {
        HandlerList.unregisterAll(StartListener)

        val title = Title.title(Component.text("§9§lSMP"), Component.text("§a§lEröffnet"))

        for (onlinePlayer in Bukkit.getOnlinePlayers()) {
            onlinePlayer.showTitle(title)
        }
    }
}