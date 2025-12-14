package net.derfarmer.spawn.commands

import net.derfarmer.moduleloader.Message
import net.derfarmer.moduleloader.commands.Command
import net.derfarmer.moduleloader.commands.annotations.CommandSubPath
import net.derfarmer.moduleloader.sendMSG
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object PVPCommand : Command("togglePVP"){

    var pvpEnabled = true

    @CommandSubPath
    fun handle(player: Player) {
        pvpEnabled = !pvpEnabled
        togglePVP(pvpEnabled)

        player.sendMSG("pvp.toggled." + if (pvpEnabled) "on" else "off")

        val msg = if (pvpEnabled) "§aAn" else "§caus"

        val title = Title.title(Component.text("PVP"), Component.text(msg))
        val newMSG = Message["prefix"].append(Component.text("ist nun $msg"))

        for (onlinePlayer in Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendMessage(newMSG)
            onlinePlayer.showTitle(title)
        }
    }


    private fun togglePVP(state : Boolean) {
        Bukkit.getWorld("world")?.pvp = state
        Bukkit.getWorld("world_nether")?.pvp = state
        Bukkit.getWorld("world_the_end")?.pvp = state
    }
}