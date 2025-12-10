package net.derfarmer.utilmodule.commands

import net.derfarmer.moduleloader.commands.Command
import net.derfarmer.moduleloader.sendMSG
import net.derfarmer.playersystem.PlayerManager
import net.derfarmer.playersystem.PlayerManager.getBannedTime
import net.derfarmer.playersystem.events.PlayerBannedEvent
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.time.Duration
import kotlin.time.toJavaDuration

object BanCommand : Command("ban") {

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        if(sender !is Player) return false

        if (!sender.hasPermission("EV1System.ban") || !sender.isOp) {
            return false
        }

        if (args.size < 2) {
            sender.sendMSG("ban.use")
            return false
        }

        try {
            val target = Bukkit.getOfflinePlayer(args[0])

            val duration = Duration.parseOrNull(args.drop(1).joinToString(" ")) ?: kotlin.run {
                sender.sendMSG("ban.format")
                return false
            }

            PlayerManager.setBannedTime(target, LocalDateTime.now().plus(duration.toJavaDuration()).toEpochSecond(
                ZoneOffset.ofHours(1)))

            sender.sendMSG("ban.banned", target.name.toString())

            if(target !is Player) return true

            Bukkit.getPluginManager().callEvent(PlayerBannedEvent(target, getBannedTime(target)))

        } catch (e : NullPointerException) {
            sender.sendMSG("ban.notFound")
            return false
        }

        return true
    }


}