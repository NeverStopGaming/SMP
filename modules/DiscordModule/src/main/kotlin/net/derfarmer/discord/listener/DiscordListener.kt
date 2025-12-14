package net.derfarmer.discord.listener

import net.derfarmer.discord.DiscordManager
import net.derfarmer.discord.utils.DiscordConfig
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

object DiscordListener : ListenerAdapter() {

    private const val APPROVE_EMOJI: String = "✅" // Emoji für Genehmigung
    private const val DENY_EMOJI: String = "❌" // Emoji für Ablehnung

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (!event.isFromGuild && event.author.isBot) return
        if (event.channel.id != DiscordConfig.whitelistChannelID) return

        val message = event.message
        //DiscordManager.minecraftAccountNotFound(message)

        message.addReaction(Emoji.fromUnicode(APPROVE_EMOJI)).queue()
        message.addReaction(Emoji.fromUnicode(DENY_EMOJI)).queue()
    }

    override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        if (event.channel.id != DiscordConfig.whitelistChannelID || event.user!!.isBot) return
        val member = event.retrieveMember().complete()

        if (!DiscordConfig.admins.contains(member.id)) return

        val emojiUnicode = event.reaction.emoji.asUnicode().name

        val msg = event.retrieveMessage().complete()

        if (emojiUnicode == APPROVE_EMOJI) {
            removeReaction(event, DENY_EMOJI)
            DiscordManager.acceptRequest(msg.contentRaw, msg.member!!)
        } else if (emojiUnicode == DENY_EMOJI) {
            removeReaction(event, APPROVE_EMOJI)
            DiscordManager.denyRequest(member)
        }
    }

    fun removeReaction(event: MessageReactionAddEvent, code: String) {
        event.retrieveMessage().queue { message: Message ->
            message.clearReactions(Emoji.fromUnicode(code)).queue()
        }
    }
}