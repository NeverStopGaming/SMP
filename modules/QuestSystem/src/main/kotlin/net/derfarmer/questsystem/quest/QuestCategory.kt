package net.derfarmer.questsystem.quest

data class QuestCategory(val id: Int, val title: String, val completed: Int)

data class DBQuestCategory(val id : Int, val title: String)
