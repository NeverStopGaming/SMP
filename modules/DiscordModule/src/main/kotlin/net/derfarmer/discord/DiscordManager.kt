package net.derfarmer.discord

import net.derfarmer.discord.DiscordModule.logger
import net.derfarmer.discord.listener.DiscordListener
import net.derfarmer.discord.utils.DiscordConfig
import net.derfarmer.playersystem.PlayerManager
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.awt.Color

object DiscordManager {

    val jda: JDA
    private val guild: Guild

    init {
        val builder = JDABuilder.createDefault(DiscordConfig.token)
        builder.setStatus(OnlineStatus.ONLINE)
        builder.setActivity(Activity.playing("Minecraft"))
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS)
        builder.setMemberCachePolicy(MemberCachePolicy.ALL)
        builder.enableIntents(GatewayIntent.DIRECT_MESSAGES)
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT)
        builder.enableIntents(GatewayIntent.GUILD_MESSAGES)

        builder.addEventListeners(DiscordListener)

        jda = builder.build().awaitReady()

        guild = (jda.getGuildById(DiscordConfig.guildID) ?: run {
            logger.error("Did not find Discord Guild")
        }) as Guild
    }

    fun acceptRequest(minecraftName: String, member: Member) {
        val player = Bukkit.getOfflinePlayer(minecraftName)
        player.discordID = member.id

        val embed = EmbedBuilder()
            .setColor(Color.GREEN)
            .setTitle("Du wurdest erfolgreich freigegeben")
            .addField("IP", DiscordConfig.serverIP, false)
            .addField("Client Version", "1.21-1.21.10", false)
            .addField("Server Version", "1.21.8", false)
            .setFooter("Bitte immer die neuste Version benutzen und halte dich an unser Regelwerk.")

        member.user.openPrivateChannel()
            .queue { privateChannel -> privateChannel.sendMessageEmbeds(embed.build()).queue() }
    }

    fun denyRequest(member: Member) {
        val embed: EmbedBuilder = EmbedBuilder()
            .setColor(Color.RED)
            .setTitle("Dein Whitelist-Antrag wurde abgelehnt")
            .setDescription(
                "Leider wurde dein Antrag, der Whitelist beizutreten, abgelehnt." +
                        "Wenn du Fragen hast, wende dich bitte an einen Administrator."
            )
            .setFooter("Vielen Dank für dein Verständnis")

        member.user.openPrivateChannel()
            .queue { privateChannel -> privateChannel.sendMessageEmbeds(embed.build()).queue() }
    }

    fun minecraftAccountNotFound(message: Message) {
        val embed: EmbedBuilder = EmbedBuilder()
            .setColor(Color.RED)
            .setTitle("Minecraft Konto nicht gefunden")
            .setDescription("Stelle sicher das du deine Name richtig geschrieben hast.")
            .setFooter("Vielen Dank für dein Verständnis")

        message.delete().queue()
        message.member?.user?.openPrivateChannel()
            ?.queue { privateChannel -> privateChannel.sendMessageEmbeds(embed.build()).queue() }
    }

    var OfflinePlayer.discordID: String
        set(discordID) {
            PlayerManager.setPlayerValue(this, "discordID", discordID)
        }
        get() {
            return PlayerManager.getPlayerValue(this, "discordID") ?: ""
        }
}