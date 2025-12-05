package net.derfarmer.questsystem

import net.derfarmer.moduleloader.modules.Module
import net.derfarmer.questsystem.command.FabricDataCommand
import net.derfarmer.questsystem.command.FabricTTestCommand
import net.derfarmer.questsystem.command.FabricTestCommand
import net.derfarmer.questsystem.listener.FabricListener
import net.derfarmer.questsystem.listener.QuestListener

object QuestModule : Module() {
    override fun onEnable() {
        register(FabricListener)
        register(QuestListener)

        register(FabricTestCommand)
        register(FabricTTestCommand)
        register(FabricDataCommand)
    }

    override fun onDisable() {
    }

    override fun onReload() {
    }
}