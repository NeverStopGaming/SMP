package net.derfarmer.questsystem

import com.github.shynixn.mccoroutine.folia.launch
import net.derfarmer.moduleloader.Redis.db
import net.derfarmer.moduleloader.gson
import net.derfarmer.questsystem.QuestManager.CATEGORIES_DB_KEY
import net.derfarmer.questsystem.QuestManager.QUEST_DB_KEY
import net.derfarmer.questsystem.QuestModule.plugin
import net.derfarmer.questsystem.quest.*
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import kotlin.math.min

object QuestDataManager : Listener {
    val haveItemMap = hashMapOf<Player, HashMap<Material, MutableList<QuestTracker>>>()
    val craftItemMap = hashMapOf<Player, HashMap<Material, MutableList<QuestTracker>>>()
    val breakBlockMap = hashMapOf<Player, HashMap<Material, MutableList<QuestTracker>>>()
    val killMobMap = hashMapOf<Player, HashMap<EntityType, MutableList<QuestTracker>>>()

    fun onClose(player: Player) {
        synchronized(haveItemMap) {

            val map = haveItemMap[player] ?: return
            val itemMap = hashMapOf<Material, Int>()

            for (item in player.inventory) {
                if (item == null) continue

                if (!map.contains(item.type)) continue

                val value = itemMap[item.type]

                if (value == null) {
                    itemMap[item.type] = 0
                } else {
                    itemMap[item.type] = value + item.amount
                }
            }

            for ((material, amount) in itemMap) {
                for (tracker in map[material]!!) {
                    if (!player.inventory.contains(material, tracker.amount)) {
                        db.hset(
                            "$QUEST_DATA_DB_KEY${player.uniqueId}_${tracker.questID}",
                            tracker.conditionID.toString(),
                            amount.toString()
                        )
                    } else {
                        db.hset(
                            "$QUEST_DATA_DB_KEY${player.uniqueId}_${tracker.questID}",
                            tracker.conditionID.toString(),
                            tracker.amount.toString()
                        )
                    }
                    haveItemMap[player]?.get(material)?.remove(tracker)
                    completeTracker(player, tracker)
                }
            }
        }
    }

    fun haveItem(player: Player, item: ItemStack) {
        synchronized(haveItemMap) {
            val map = haveItemMap.getOrDefault(player, null) ?: return

            val trackers = map.getOrDefault(item.type, null) ?: return

            synchronized(map) {
                synchronized(trackers) {
                    for (tracker in trackers) {
                        val amount =
                            player.inventory.sumOf { if (it != null && it.type == item.type) it.amount else 0 }

                        val name = if (tracker.isServer) SERVER_QUEST_NAME else player.uniqueId.toString()

                        db.hset(
                            "$QUEST_DATA_DB_KEY${name}_${tracker.questID}", tracker.conditionID.toString(),
                            amount.toString()
                        )

                        if (!player.inventory.contains(item.type, tracker.amount)) {
                            db.hset(
                                "$QUEST_DATA_DB_KEY${name}_${tracker.questID}",
                                tracker.conditionID.toString(),
                                amount.toString()
                            )
                            continue
                        } else {
                            db.hset(
                                "$QUEST_DATA_DB_KEY${name}_${tracker.questID}",
                                tracker.conditionID.toString(),
                                tracker.amount.toString()
                            )
                        }
                        haveItemMap[player]?.get(item.type)?.remove(tracker)
                        completeTracker(player, tracker)
                    }
                }
            }
        }
    }

    fun killMob(player: Player, entity: Entity) {
        synchronized(killMobMap) {
            val map = killMobMap.getOrDefault(player, null) ?: return
            synchronized(map) {

                val trackers = map.getOrDefault(entity.type, null) ?: return

                synchronized(trackers) {
                    for (tracker in trackers) {
                        synchronized(tracker) {
                            val name = if (tracker.isServer) SERVER_QUEST_NAME else player.uniqueId.toString()

                            val newValue = db.hincrBy(
                                "$QUEST_DATA_DB_KEY${name}_${tracker.questID}", tracker.conditionID.toString(),
                                1
                            )

                            if (newValue < tracker.amount) continue

                            killMobMap[player]?.get(entity.type)?.remove(tracker)
                            completeTracker(player, tracker)
                        }
                    }
                }
            }
        }
    }

    fun breakBlock(player: Player, material: Material) {
        synchronized(breakBlockMap) {
            val map = breakBlockMap.getOrDefault(player, null) ?: return
            val trackers = map.getOrDefault(material, null) ?: return

            for (tracker in trackers) {
                val name = if (tracker.isServer) SERVER_QUEST_NAME else player.uniqueId.toString()

                val newValue = db.hincrBy(
                    "$QUEST_DATA_DB_KEY${name}_${tracker.questID}", tracker.conditionID.toString(),
                    1
                )

                if (newValue < tracker.amount) continue

                breakBlockMap[player]?.get(material)?.remove(tracker)
                completeTracker(player, tracker)
            }
        }
    }

    fun submitItem(player: Player, questId: Int) {
        val quest = gson.fromJson(db[QUEST_DB_KEY + questId], DBQuest::class.java)

        val name = if (quest.isServerQuest) SERVER_QUEST_NAME else player.uniqueId.toString()
        val conditionData = getQuestData(questId, name)

        for ((i, condition) in quest.conditions.withIndex()) {
            if (condition.type != QuestConditionType.SUBMIT_ITEM) continue

            val material = Material.matchMaterial(condition.id) ?: return
            val currentAmount = player.inventory.sumOf { if (it != null && it.type == material) it.amount else 0 }

            if (currentAmount == 0) continue

            val neededAmount = condition.amount - conditionData.getOrDefault(i, 0)

            val newAmount = min(currentAmount, neededAmount)

            val newValue = db.hincrBy(
                "$QUEST_DATA_DB_KEY${name}_${questId}", i.toString(),
                newAmount.toLong()
            )

            player.inventory.remove(ItemStack(material, newAmount))

            if (newValue < condition.amount) continue

            val data = getQuestData(questId, name)

            for ((i: Int, condition: DBQuestCondition) in quest.conditions.withIndex()) {
                if (data.getOrDefault(i, 0) < condition.amount) return
            }

            if (quest.isServerQuest) {
                Bukkit.getOfflinePlayers().forEach {
                    QuestManager.completeQuest(it, quest)
                }
            } else {
                QuestManager.completeQuest(player, quest)
            }
        }

        FabricManager.sendQuest(player, QuestManager.getQuest(player, questId))
    }

    const val QUEST_DATA_DB_KEY = "qd_"
    const val SERVER_QUEST_NAME = "server"
    fun getQuestData(questId: Int, name: String): Map<Int, Int> {
        return db.hgetAll("$QUEST_DATA_DB_KEY${name}_$questId").mapNotNull { (k, v) ->
            val ki = k.toIntOrNull()
            val vi = v.toIntOrNull()
            if (ki != null && vi != null) ki to vi else null
        }.toMap()
    }

    const val QUEST_TREE_DATA_DB_KEY = "qtd_"
    fun getPlayerTree(name: String): Map<Int, Boolean> {
        return db.hgetAll("$QUEST_TREE_DATA_DB_KEY${name}").mapNotNull { (k, v) ->
            val ki = k.toIntOrNull()
            val vi = v.toBooleanStrictOrNull()
            if (ki != null && vi != null) ki to vi else null
        }.toMap()
    }

    fun completeTracker(player: Player, tracker: QuestTracker) {
        val quest = gson.fromJson(db[QUEST_DB_KEY + tracker.questID], DBQuest::class.java)

        val name = if (quest.isServerQuest) SERVER_QUEST_NAME else player.uniqueId.toString()

        val data = getQuestData(tracker.questID, name)

        for ((i: Int, condition: DBQuestCondition) in quest.conditions.withIndex()) {
            if (data.getOrDefault(i, 0) < condition.amount) return
        }

        if (tracker.isServer) {
            Bukkit.getOfflinePlayers().forEach {
                QuestManager.completeQuest(it, quest)
            }
        } else {
            QuestManager.completeQuest(player, quest)
        }
    }

    fun initTrackers(player: Player) {
        val categories =
            gson.fromJson<Array<DBQuestCategory>>(db[CATEGORIES_DB_KEY], arrayOf<DBQuestCategory>()::class.java)

        categories.forEach { category ->
            val tree = QuestManager.getTree(player, category.id)

            for (node in tree) {
                if (node.completed) continue

                val filter = tree.filter { it.connectionsTo.contains(node.questID) }
                if (filter.isNotEmpty() && filter.all { !it.completed }) continue

                val quest = gson.fromJson(db[QUEST_DB_KEY + node.questID], DBQuest::class.java) ?: return

                val name = if (quest.isServerQuest) SERVER_QUEST_NAME else player.uniqueId.toString()
                val conditionData = getQuestData(node.questID, name)

                for ((i, condition) in quest.conditions.withIndex()) {
                    if (conditionData.getOrDefault(i, 0) >= condition.amount) continue
                    if (condition.type == QuestConditionType.SUBMIT_ITEM) continue

                    createTracker(player, condition, quest.isServerQuest, quest.id, i)
                }
            }
        }
    }

    fun createTracker(
        player: Player,
        condition: DBQuestCondition,
        isServerQuest: Boolean,
        questId: Int,
        conditionID: Int
    ) {
        plugin.launch {
            val tracker = QuestTracker(questId, conditionID, isServerQuest, condition.amount)

            when (condition.type) {
                QuestConditionType.KILL_MOB -> {
                    val entityType = try {
                        EntityType.valueOf(condition.id.uppercase())
                    } catch (e: IllegalArgumentException) {
                        throw IllegalArgumentException("Invalid entity type id='${condition.id}'", e)
                    }

                    synchronized(killMobMap) {
                        val mapForPlayer = killMobMap.getOrPut(player) { HashMap() }
                        mapForPlayer.getOrPut(entityType) { mutableListOf() }.add(tracker)
                    }
                }

                QuestConditionType.BREAK_BLOCK,
                QuestConditionType.CRAFT_ITEM,
                QuestConditionType.HAVE_ITEM -> {
                    val material = Material.matchMaterial(condition.id)
                        ?: throw IllegalArgumentException("Invalid material id='${condition.id}'")

                    val map = when (condition.type) {
                        QuestConditionType.CRAFT_ITEM -> craftItemMap
                        QuestConditionType.BREAK_BLOCK -> breakBlockMap
                        QuestConditionType.HAVE_ITEM -> haveItemMap
                        else -> throw IllegalArgumentException("Unsupported player condition: ${condition.type}")
                    }

                    synchronized(map) {
                        val materialMap = map.getOrPut(player) { HashMap() }
                        materialMap.getOrPut(material) { mutableListOf() }.add(tracker)
                    }
                }

                else -> throw IllegalArgumentException("Unsupported player condition: ${condition.type}")
            }
        }
    }
}