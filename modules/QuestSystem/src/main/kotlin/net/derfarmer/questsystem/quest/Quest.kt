package net.derfarmer.questsystem.quest

data class Quest(
    val id: Int, val title: String, val description: String, val description2: String,
    val rewards: List<QuestReward>, val conditions: List<QuestCondition>
)

data class QuestCondition(
    val type: QuestConditionType,
    val id: String,
    val amount: Int,
    val currentAmount: Int,
    val tooltip: String
)


enum class QuestConditionType {
    SUBMIT_ITEM,
    HAVE_ITEM,
    KILL_MOB,
    BREAK_BLOCK,
    CRAFT_ITEM
}

data class QuestReward(val type: QuestRewardType, val id: String, val iconID: String, val tooltip: String)

enum class QuestRewardType {
    RECIPES_UNLOCK,
    DIMENSION_UNLOCK,
    INTERACTION_UNLOCK,
    EFFECT_UNLOCK
}

data class DBQuestCondition(
    val type: QuestConditionType,
    val id: String,
    val amount: Int,
    val tooltip: String
)

data class DBQuest(
    val id: Int, val title: String, val description: String, val description2: String,
    val rewards: List<QuestReward>, val conditions: List<DBQuestCondition>,
    val isServerQuest: Boolean
)