package net.derfarmer.questsystem

import net.derfarmer.moduleloader.Redis.db
import net.derfarmer.moduleloader.gson
import net.derfarmer.questsystem.quest.*
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object QuestManager {

    val playerHaveItemQuests = mutableMapOf<Player, Quest>()

    const val CATEGORIES_DB_KEY = "quest_categories"
    fun getCategories(player: Player): List<QuestCategory> {
        val categories = gson.fromJson<Array<DBQuestCategory>>(db[CATEGORIES_DB_KEY], arrayOf<DBQuestCategory>()::class.java)
        val map = getPlayerCategories(player)
        return categories.map { QuestCategory(it.id, it.title, map.getOrDefault(it.id, 0)) }
    }

    const val QUEST_TREE_DB_KEY = "quest_tree_"
    fun getTree(player: Player, questTreeId: Int): List<QuestNode> {
        val tree = gson.fromJson<Array<DBQuestNode>>(db[QUEST_TREE_DB_KEY + questTreeId], arrayOf<DBQuestNode>()::class.java)

        val map = getPlayerTree(player, questTreeId)
        return tree.map { QuestNode(it.questID, it.itemID, it.title, it.x, it.y,
            map.getOrDefault(it.questID, false),it.connectionsTo) }
    }

    const val QUEST_DB_KEY = "quest_"
    fun getQuest(player: Player, questId: Int): Quest {
        val quest = gson.fromJson(db[QUEST_DB_KEY + questId], DBQuest::class.java)

        return Quest(quest.id, quest.title, quest.description, quest.description2, quest.rewards,
            quest.conditions.map { QuestCondition(it.type, it.id, it.amount, getConditionAmount(player, quest, it.id), it.tooltip) })
    }

    fun getConditionAmount(player: Player, quest: DBQuest, condition: String) : Int{

        return 0
    }

    fun getPlayerTree(player: Player, questTreeId: Int) : Map<Int, Boolean>{
        val map = mutableMapOf<Int, Boolean>()
        return map
    }

    fun getPlayerCategories(player: Player) : Map<Int, Int>{
        val map = mutableMapOf<Int, Int>()
        return map
    }

    fun haveItem(player: Player, item : ItemStack) {
        //1. check if there is a open quest with have Item
        //2. check if the picked up item is in the list
        //3. check the current amount in the inventory
        //4. if currentAmount is >= then quest amount call quest finish event
        player.inventory.contains(item.type, 10)
        player.sendMessage(item.type.toString() + " ${item.amount}")
    }

    fun craftItem(player: Player, item: ItemStack) {
        player.sendMessage("craft: " + item.type.toString() + " ${item.amount}")
    }

    fun killMob(player: Player, entity: Entity) {
        player.sendMessage(entity.type.name)
    }

    fun breakBlock(player: Player, block: Block) {
        player.sendMessage(block.type.name)
    }

    fun submit(player: Player, questId: Int) {

    }

    fun onQuestComplete(player: Player, quest: Quest) {
        FabricManager.sendToast(player, "Quest Abgeschlossen", quest.title)
    }
}