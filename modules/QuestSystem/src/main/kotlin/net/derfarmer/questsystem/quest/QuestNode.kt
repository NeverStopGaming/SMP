package net.derfarmer.questsystem.quest

data class QuestNode(
    val questID: Int, val itemID: String, val title: String, val x: Int, val y: Int,
    val completed: Boolean, val connectionsTo: List<Int>
)

data class DBQuestNode(val questID: Int, val itemID : String, val title: String,
                        val x : Int, val y : Int, val connectionsTo: List<Int>,
                        val isServerQuest : Boolean)