package net.derfarmer.voicechat.command

import net.derfarmer.moduleloader.commands.Command
import net.derfarmer.moduleloader.commands.annotations.CommandSubPath
import net.derfarmer.moduleloader.sendMSG
import org.bukkit.entity.Player

object DisableVoiceCommand : Command("disableVoice") {

    var isDisabled = false

    @CommandSubPath(permission = "voice.disable")
    fun handle(player: Player) {
        isDisabled = !isDisabled
        player.sendMSG("voice.disable.${if (isDisabled) "on" else "off"}")
    }
}