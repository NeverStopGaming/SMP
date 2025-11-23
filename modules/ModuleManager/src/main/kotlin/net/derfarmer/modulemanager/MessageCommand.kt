package net.derfarmer.modulemanager

import net.derfarmer.moduleloader.Message
import net.derfarmer.moduleloader.commands.Command
import net.derfarmer.moduleloader.commands.annotations.CommandSubPath
import net.derfarmer.moduleloader.sendMSG
import org.bukkit.entity.Player

object MessageCommand : Command("message") {

    @CommandSubPath
    fun flush(player: Player) {
        Message.flushCache()
        player.sendMSG("message.flushed")
    }
}