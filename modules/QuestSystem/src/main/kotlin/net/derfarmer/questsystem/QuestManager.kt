package net.derfarmer.questsystem

import net.derfarmer.questsystem.quest.*
import org.bukkit.entity.Player

object QuestManager {

    fun getCategories(): List<QuestCategory> {
        return mutableListOf(
            QuestCategory(1, "1. Basic Survival", 87),
            QuestCategory(2, "2. Culinary Delights", 18),
            QuestCategory(3, "3. Warum tut ich das hier", 69),
            QuestCategory(4, "4. Nice", 420),
            QuestCategory(5, "5. Last mich raus ...", -1),
            QuestCategory(6, "6. ich bin in einen", 43),
            QuestCategory(7, "7. Questbuch gefangen", 64),
        )
    }

    fun getTree(player: Player, questTreeId: Int): List<QuestNode> {
        return mutableListOf(
            QuestNode(10, "stone", "The Rock", 30, 30, true, listOf(Pair(100, 100))),
            QuestNode(12, "diamond_sword", "Kill Panda 100mal", 100, 100, false, listOf())
        )
    }

    fun getQuest(player: Player, questId: Int): Quest {

        val des =
            "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren,"

        return Quest(
            questId, "Das ist der Title", des, des,
            listOf(
                QuestReward(QuestRewardType.RECIPES_UNLOCK, "iron", "iron_ingot", "Unlock Iron Age"),
                QuestReward(QuestRewardType.RECIPES_UNLOCK, "iron", "diamond", "Unlock Iron Age"),
                QuestReward(QuestRewardType.RECIPES_UNLOCK, "iron", "diamond", "Unlock Iron Age"),
                QuestReward(QuestRewardType.RECIPES_UNLOCK, "iron", "diamond", "Unlock Iron Age"),
                QuestReward(QuestRewardType.RECIPES_UNLOCK, "iron", "diamond", "Unlock Iron Age")
            ), listOf(
                QuestCondition(QuestConditionType.SUBMIT_ITEM, "stone", 10, 0, "Stone abgeben"),
                QuestCondition(QuestConditionType.KILL_MOB, "chicken", 10, 0, "Hühner töten")
            )
        )
    }
}