package net.derfarmer.questsystem

import net.derfarmer.moduleloader.Redis.db
import net.derfarmer.moduleloader.gson
import net.derfarmer.questsystem.quest.*
import org.bukkit.entity.Player

object QuestManager {

    const val CATEGORIES_DB_KEY = "quest_categories"
    fun getCategories(player: Player): List<QuestCategory> {
        val categories = gson.fromJson<Array<DBQuestCategory>>(db[CATEGORIES_DB_KEY], arrayOf<DBQuestCategory>()::class.java)
        // change completion
        return categories.map { QuestCategory(it.id, it.title, 0) }
    }

    const val QUEST_TREE_DB_KEY = "quest_tree_"
    fun getTree(player: Player, questTreeId: Int): List<QuestNode> {
        val tree = gson.fromJson<Array<DBQuestNode>>(db[QUEST_TREE_DB_KEY + questTreeId], arrayOf<DBQuestNode>()::class.java)
        // change completed status
        return tree.map { QuestNode(it.questID, it.itemID, it.title, it.x, it.y, true,it.connectionsTo) }
    }

    const val QUEST_DB_KEY = "quest_"
    fun getQuest(player: Player, questId: Int): Quest {

        val quest = gson.fromJson(db[QUEST_DB_KEY + questId], DBQuest::class.java)

        // change current amount
        return Quest(quest.id, quest.title, quest.description, quest.description2, quest.rewards,
            quest.conditions.map { QuestCondition(it.type, it.id, it.amount, 0, it.tooltip) })
    }
}