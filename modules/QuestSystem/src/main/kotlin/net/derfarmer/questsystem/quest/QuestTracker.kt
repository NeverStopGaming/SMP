package net.derfarmer.questsystem.quest

data class QuestTracker(val questID: Int, val conditionID: Int, val isServer: Boolean, val amount: Int)
