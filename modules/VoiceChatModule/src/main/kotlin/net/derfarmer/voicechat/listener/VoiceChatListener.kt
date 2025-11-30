package net.derfarmer.voicechat.listener

import de.maxhenkel.voicechat.api.Group
import de.maxhenkel.voicechat.api.VoicechatPlugin
import de.maxhenkel.voicechat.api.events.EventRegistration
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent
import net.derfarmer.voicechat.VoiceChatModule
import net.derfarmer.voicechat.command.DisableVoiceCommand
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault


/*
MIT License

Copyright (c) 2022 Max Henkel

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

object VoiceChatListener : VoicechatPlugin {
    override fun getPluginId() = VoiceChatModule.name

    var BROADCAST_PERMISSION: Permission = Permission("voice.broadcast", PermissionDefault.OP)


    override fun registerEvents(registration: EventRegistration) {
        registration.registerEvent(
            MicrophonePacketEvent::class.java
        ) { event: MicrophonePacketEvent -> this.onMicrophone(event) }
    }


    private fun onMicrophone(event: MicrophonePacketEvent) {
        if (event.senderConnection == null) return
        if (event.senderConnection!!.player.player !is Player) return

        val player = event.senderConnection!!.player.player as Player

        if (!player.hasPermission(BROADCAST_PERMISSION)) {
            if (DisableVoiceCommand.isDisabled) event.cancel()
            return
        }

        val group: Group = event.senderConnection!!.group ?: return

        if (!group.name.equals("broadcast", true)) {
            return
        }

        event.cancel()

        val api = event.voicechat

        for (onlinePlayer in Bukkit.getServer().onlinePlayers) {
            if (onlinePlayer.uniqueId == player.uniqueId) continue

            val connection = api.getConnectionOf(onlinePlayer.uniqueId) ?: continue
            api.sendStaticSoundPacketTo(connection, event.packet.staticSoundPacketBuilder().build())
        }
    }

}