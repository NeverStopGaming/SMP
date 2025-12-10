package net.derfarmer.questsystem

import net.derfarmer.moduleloader.Redis.db
import net.derfarmer.moduleloader.gson
import net.derfarmer.questsystem.QuestDataManager.SERVER_QUEST_NAME
import net.derfarmer.questsystem.QuestDataManager.getQuestData
import net.derfarmer.questsystem.quest.*
import org.bukkit.entity.Player

object QuestManager {

    const val CATEGORIES_DB_KEY = "quest_categories"
    fun getCategories(player: Player): List<QuestCategory> {
        val categories =
            gson.fromJson<Array<DBQuestCategory>>(db[CATEGORIES_DB_KEY], arrayOf<DBQuestCategory>()::class.java)
        val map = getPlayerCategories(player)
        return categories.map { QuestCategory(it.id, it.title, map.getOrDefault(it.id, 0)) }
    }

    const val QUEST_TREE_DB_KEY = "quest_tree_"
    fun getTree(player: Player, questTreeId: Int): List<QuestNode> {
        val tree =
            gson.fromJson<Array<DBQuestNode>>(db[QUEST_TREE_DB_KEY + questTreeId], arrayOf<DBQuestNode>()::class.java)

        val map = QuestDataManager.getPlayerTree(player.uniqueId.toString())
        // second map for server quests
        return tree.map {
            QuestNode(
                it.questID, it.itemID, it.title, it.x, it.y,
                map.getOrDefault(it.questID, false), it.connectionsTo
            )
        }
    }

    const val QUEST_DB_KEY = "quest_"
    fun getQuest(player: Player, questId: Int): Quest {
        val quest = gson.fromJson(db[QUEST_DB_KEY + questId], DBQuest::class.java)

        val name = if (quest.isServerQuest) SERVER_QUEST_NAME else player.uniqueId.toString()
        val conditionData = getQuestData(questId, name)

        return Quest(
            quest.id, quest.title, quest.description, quest.description2, quest.rewards,
            quest.conditions.withIndex().map { (i: Int, it: DBQuestCondition) ->
                QuestCondition(
                    it.type,
                    it.id,
                    it.amount,
                    conditionData.getOrDefault(i, 0),
                    it.tooltip
                )
            })
    }

    fun getPlayerCategories(player: Player): Map<Int, Int> {
        val map = mutableMapOf<Int, Int>()
        return map
    }

    fun completeQuest(player: Player, quest: DBQuest, name: String) {
        db.hset(QuestDataManager.QUEST_TREE_DATA_DB_KEY + name, quest.id.toString(), "true")
        FabricManager.sendToast(player, "Quest Abgeschlossen", quest.title)
        QuestDataManager.initTrackers(player)
    }
}