package net.derfarmer.questsystem

import net.derfarmer.moduleloader.modules.Module
import net.derfarmer.questsystem.command.FabricDataCommand
import net.derfarmer.questsystem.command.ModsCommand
import net.derfarmer.questsystem.listener.FabricListener
import net.derfarmer.questsystem.listener.QuestListener
import net.derfarmer.questsystem.listener.UnlockListener
import org.bukkit.Bukkit

object QuestModule : Module() {
    override fun onEnable() {
        register(FabricListener)
        register(QuestListener)
        register(UnlockListener)

        register(FabricDataCommand)
        register(ModsCommand)

        Bukkit.getOnlinePlayers().forEach { player ->
            QuestDataManager.initTrackers(player)
        }
    }

    override fun onDisable() {
    }

    override fun onReload() {
    }
}