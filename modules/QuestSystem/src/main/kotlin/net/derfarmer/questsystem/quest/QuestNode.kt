package net.derfarmer.questsystem.quest

open class QuestNode(
    open val questID: Int, open val itemID: String, open val title: String, open val x: Int, open val y: Int,
    open val completed: Boolean, val connectionsTo: List<Pair<Int, Int>>
)

