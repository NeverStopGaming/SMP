package net.derfarmer.utilmodule.commands

import net.derfarmer.moduleloader.commands.Command
import net.derfarmer.moduleloader.commands.annotations.CommandSubPath
import net.derfarmer.moduleloader.sendMSG
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object ClearCommand : Command("clearchat") {

    @CommandSubPath(permission = "utils.clearchat", description = "Clears the chat")
    fun handle(player: Player) {
        for (onlinePlayer in Bukkit.getOnlinePlayers()) {
            if (!onlinePlayer.hasPermission("utils.clearchat")) {
                for (i in 0..200) {
                    onlinePlayer.sendMessage(Component.empty())
                }
            } else {
                player.sendMSG("utils.chatCleared")
            }
        }
    }
}