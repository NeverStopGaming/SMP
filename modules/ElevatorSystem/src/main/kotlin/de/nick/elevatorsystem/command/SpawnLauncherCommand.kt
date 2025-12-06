package de.nick.elevatorsystem.command

import net.derfarmer.moduleloader.commands.Command
import net.derfarmer.moduleloader.sendMSG
import net.derfarmer.playersystem.utils.ItemBuilder
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object SpawnLauncherCommand : Command("spawnlauncher") {

    override fun execute(player: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        if (player !is Player) return false


        if (!player.hasPermission("EV1System.Command.SpawnLauncher")) {
            player.sendMSG("launcher.notPermission.SpawnLauncher")
            return false
        }

        if (args.isNotEmpty()) {
            player.sendMSG("launcher.usage")
            return false
        }


        val spawnLauncher = ItemBuilder(Material.DISPENSER).setDisplayName("§l§aElytra Launcher §8(§aSpawn§8)")
            .setLore("§3Platziere den Elytra Launcher mit dem Loch nach oben.")
            .setData("ev1", "elytra_launcher", 100).build()


        player.inventory.addItem(spawnLauncher)
        player.sendMSG("launcher.SpawnLauncher.GiveItem")

        return true
    }


}