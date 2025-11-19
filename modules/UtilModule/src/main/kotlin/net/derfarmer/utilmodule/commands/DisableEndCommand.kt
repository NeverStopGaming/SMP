package net.derfarmer.utilmodule.commands

import net.derfarmer.moduleloader.commands.Command
import net.derfarmer.moduleloader.commands.annotations.CommandSubPath
import net.derfarmer.moduleloader.sendMSG
import org.bukkit.entity.Player

object DisableEndCommand : Command("disableEnd") {

    var isEndEnabled = true

    @CommandSubPath(permission = "utils.end")
    fun handle(player: Player) {
        isEndEnabled = !isEndEnabled
        player.sendMSG("utils.end.${if (isEndEnabled) "on" else "off"}")
    }
}