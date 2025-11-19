package net.derfarmer.utilmodule.commands

import net.derfarmer.moduleloader.commands.Command
import net.derfarmer.moduleloader.commands.annotations.CommandSubPath
import net.derfarmer.moduleloader.sendMSG
import net.derfarmer.utilmodule.UtilModule
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

object SpecCommand : Command("spec") {

    val returnLocation = mutableMapOf<UUID, Location>()

    @CommandSubPath(permission = "utils.spec")
    fun handle(player: Player) {
        if (!returnLocation.contains(player.uniqueId)) {
            Bukkit.getOnlinePlayers().forEach {
                if (!it.hasPermission("utils.spec.ignore")) {
                    it.hidePlayer(UtilModule.plugin, player)
                    it.unlistPlayer(player)
                }
            }

            player.gameMode = GameMode.CREATIVE
            returnLocation[player.uniqueId] = player.location

            player.sendMSG("utils.spec.on")
        } else {
            Bukkit.getOnlinePlayers().forEach {
                it.showPlayer(UtilModule.plugin, player)
                it.listPlayer(player)
            }

            player.gameMode = GameMode.SURVIVAL
            returnLocation[player.uniqueId]?.let { player.teleportAsync(it) }
            returnLocation.remove(player.uniqueId)

            player.sendMSG("utils.spec.off")
        }
    }
}