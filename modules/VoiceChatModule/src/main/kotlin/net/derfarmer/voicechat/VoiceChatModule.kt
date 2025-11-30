package net.derfarmer.voicechat

import de.maxhenkel.voicechat.api.BukkitVoicechatService
import net.derfarmer.moduleloader.modules.Module
import net.derfarmer.voicechat.command.DisableVoiceCommand
import net.derfarmer.voicechat.listener.VoiceChatListener


object VoiceChatModule : Module(){

    val service: BukkitVoicechatService? = plugin.server.servicesManager.load(BukkitVoicechatService::class.java)

    override fun onEnable() {
        service?.registerPlugin (VoiceChatListener)
        logger.info("Registered VoiceChat Service")

        register(DisableVoiceCommand)
    }

    override fun onDisable() {
        plugin.server.servicesManager.unregister(VoiceChatListener);
    }

    override fun onReload() {
    }
}