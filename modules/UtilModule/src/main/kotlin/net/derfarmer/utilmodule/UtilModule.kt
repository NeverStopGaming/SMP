package net.derfarmer.utilmodule

import net.derfarmer.moduleloader.modules.Module
import net.derfarmer.utilmodule.commands.*
import net.derfarmer.utilmodule.listener.JoinListener
import net.derfarmer.utilmodule.listener.TeleportListener

object UtilModule : Module() {

    override fun onEnable() {
        register(BanCommand)
        register(ClearCommand)
        register(DisableEndCommand)
        register(EcSeeCommand)
        register(InvSeeCommand)
        register(PingCommand)
        register(SpecCommand)
        register(UnBanCommand)

        register(JoinListener)
        register(TeleportListener)
    }

    override fun onDisable() {
    }

    override fun onReload() {
    }
}