package net.derfarmer.modulemanager

import net.derfarmer.moduleloader.commands.Command
import net.derfarmer.moduleloader.commands.annotations.CommandArgument
import net.derfarmer.moduleloader.commands.annotations.CommandSubPath
import net.derfarmer.moduleloader.commands.provider.CommandSuggestionProvider
import net.derfarmer.moduleloader.modules.ModuleManager
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import java.io.File

object ModuleCommand : Command("modules") {

    @CommandSubPath("list", permission = "module.list")
    fun handleList(player: Player) {
        player.sendMessage(Component.text("[Modules] Modules list:"))
        ModuleManager.modules.forEach { module ->
            player.sendMessage(Component.text("   - ${module.name}"))
        }
    }

    @CommandSubPath("reload", permission = "module.reload")
    fun handleReload(player: Player) {
        ModuleManager.reloadAllModules()
        player.sendMessage(Component.text("[Modules] All Modules were reloaded."))
    }

    @CommandSubPath("reload <moduleName>", permission = "module.reload")
    fun handleReload(
        player: Player,
        @CommandArgument("moduleName", ModuleSuggestionProvider::class) moduleName: String
    ) {
        val module = ModuleManager.modules.find { it.name == moduleName }

        if (module == null) {
            player.sendMessage(Component.text("[Modules] Module with the name \"$moduleName\" not found"))
            return
        }

        ModuleManager.reloadModule(module)
        player.sendMessage(Component.text("[Modules] Module \"$moduleName\" was reloaded."))
    }


    @CommandSubPath("disable <moduleName>", permission = "module.disable")
    fun handleDisable(
        player: Player,
        @CommandArgument("moduleName", ModuleSuggestionProvider::class) moduleName: String
    ) {
        val module = ModuleManager.modules.find { it.name == moduleName }

        if (module == null) {
            player.sendMessage(Component.text("[Modules] Module with the name \"$moduleName\" not found"))
            return
        }

        ModuleManager.disableModule(module)
        player.sendMessage(Component.text("[Modules] Module \"$moduleName\" was disabled."))
    }

    @CommandSubPath("load", permission = "module.load")
    fun handleLoad(player: Player) {
        ModuleManager.loadModulesFromFiles()
        player.sendMessage(Component.text("[Modules] All Modules were loaded."))
    }

    @CommandSubPath("load <fileName>", permission = "module.load")
    fun handleLoad(player: Player, @CommandArgument("fileName", ModuleFileSuggestionProvider::class) fileName: String) {
        val file = File(ModuleManager.modulesDir, fileName)

        if (!file.exists()) {
            player.sendMessage(Component.text("[Modules] File with the name \"$fileName\" not found"))
            return
        }

        ModuleManager.loadModuleFromFile(file)
        player.sendMessage(Component.text("[Modules] File \"$fileName\" was loaded."))
    }
}


class ModuleSuggestionProvider : CommandSuggestionProvider {
    override fun getSuggestions(sender: Player, fullCommand: String, lastArgument: String): List<String> {
        return ModuleManager.modules.map { it.name }
    }
}

class ModuleFileSuggestionProvider : CommandSuggestionProvider {
    override fun getSuggestions(sender: Player, fullCommand: String, lastArgument: String): List<String> {
        return ModuleManager.modulesDir.listFiles().map { it.name }
    }
}