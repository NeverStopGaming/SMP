package net.derfarmer.questsystem

import net.derfarmer.moduleloader.gson
import net.derfarmer.questsystem.quest.Quest
import net.derfarmer.questsystem.quest.QuestCategory
import net.derfarmer.questsystem.quest.QuestNode
import org.bukkit.entity.Player

object FabricManager {

    private val fabricPlayers = mutableListOf<Player>()
    private val waitOnMSG = mutableMapOf<Player, (String) -> Unit>()

    fun isFabricPlayer(player: Player) = fabricPlayers.contains(player)

    fun registerPlayer(player: Player) {
        if (!fabricPlayers.contains(player)) fabricPlayers.add(player)
    }

    fun unregisterPlayer(player: Player) = fabricPlayers.remove(player)

    fun parseMessage(player: Player, message: String) {
        val msgType = message[0]
        val data = message.substring(1)

        // TODO: perform input Validation
        when (msgType) {
            '0' -> registerPlayer(player)
            'c' -> sendCategories(player, QuestManager.getCategories(player))
            'l' -> sendTree(player, QuestManager.getTree(player, data.toInt()))
            'q' -> sendQuest(player, QuestManager.getQuest(player, data.toInt()))
            's' -> QuestDataManager.submitItem(player, data.toInt())
            'm' -> {
                val callback = waitOnMSG.getOrDefault(player, null) ?: return
                callback(data)
            }
        }
    }

    fun sendRaw(player: Player, rawData: String) {
        player.sendMessage("fabricdata $rawData")
    }

    fun sendToast(player: Player, title: String, description: String) {
        sendRaw(player, "t$title;$description")
    }

    fun sendCategories(player: Player, categories: List<QuestCategory>) {
        sendRaw(player, "c" + gson.toJson(categories))
    }

    fun sendTree(player: Player, tree: List<QuestNode>) {
        sendRaw(player, "l" + gson.toJson(tree))
    }

    fun sendQuest(player: Player, quest: Quest) {
        sendRaw(player, "q" + gson.toJson(quest))
    }

    fun openBook(player: Player) {
        sendRaw(player, "o")
    }

    fun requestModes(player: Player, callback: (String) -> Unit) {
        sendRaw(player, "m")
        waitOnMSG[player] = callback
    }
}