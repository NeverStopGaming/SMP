package net.derfarmer.webmodule

import io.javalin.Javalin
import io.javalin.http.staticfiles.Location
import net.derfarmer.moduleloader.Redis
import net.derfarmer.moduleloader.gson
import net.derfarmer.moduleloader.modules.Module
import net.derfarmer.questsystem.FabricManager
import net.derfarmer.questsystem.QuestManager
import net.derfarmer.questsystem.quest.DBQuest
import net.derfarmer.questsystem.quest.DBQuestCategory
import net.derfarmer.questsystem.quest.DBQuestCondition
import net.derfarmer.questsystem.quest.DBQuestNode
import net.derfarmer.questsystem.quest.Quest
import net.derfarmer.questsystem.quest.QuestCategory
import net.derfarmer.questsystem.quest.QuestCondition
import net.derfarmer.questsystem.quest.QuestNode
import org.bukkit.Bukkit


object WebModule : Module() {

    val app: Javalin = Javalin.create {cfg ->
        cfg.staticFiles.add { staticFileConfig ->
            staticFileConfig.hostedPath = "/"
            staticFileConfig.directory = "/public"
            staticFileConfig.location = Location.CLASSPATH
        };
    }.start(50008)

    override fun onEnable() {

        app.put("/category") { ctx ->

            val categories = gson.fromJson<Array<DBQuestCategory>>(ctx.body(), arrayOf<DBQuestCategory>()::class.java)

            for (player in Bukkit.getOnlinePlayers()) {
                if (!FabricManager.isFabricPlayer(player)) continue

                // check completed
                FabricManager.sendCategories(player, categories.map { (id, title) -> QuestCategory(id, title, 0) })
            }

            ctx.status(200)
        }

        app.post("/category") { ctx ->

            gson.fromJson<Array<DBQuestCategory>>(ctx.body(), arrayOf<DBQuestCategory>()::class.java) ?: return@post

            Redis.db[QuestManager.CATEGORIES_DB_KEY] = ctx.body()

            ctx.status(200)
        }

        app.get("category") { ctx ->
            ctx.json(Redis.db[QuestManager.CATEGORIES_DB_KEY])
        }

        app.put("/tree/{id}") { ctx ->

            val nodes = gson.fromJson<Array<ReqQuestNode>>(ctx.body(), arrayOf<ReqQuestNode>()::class.java)

            val data = nodes.map { reqNodeToDBNode(it) }.map { QuestNode(it.questID, it.itemID, it.title,
                it.x, it.y,true, it.connectionsTo) }


            for (player in Bukkit.getOnlinePlayers()) {
                if (!FabricManager.isFabricPlayer(player)) continue
                // check completed status
                FabricManager.sendTree(player,data)
            }

            ctx.status(200)
        }

        app.post("/tree/{id}") { ctx ->

            val json = gson.fromJson<Array<ReqQuestNode>>(ctx.body(), arrayOf<ReqQuestNode>()::class.java)
            val data = gson.toJson(json.map { reqNodeToDBNode(it) }) ?: ""

            Redis.db[QuestManager.QUEST_TREE_DB_KEY + ctx.pathParam("id")] = data

            ctx.status(200)
        }

        app.get("/tree/{id}") { ctx ->
            val json = Redis.db[QuestManager.QUEST_TREE_DB_KEY + ctx.pathParam("id")]
            val data = gson.fromJson<Array<DBQuestNode>>(json, arrayOf<DBQuestNode>()::class.java)
            ctx.json(data.map { dbNodeToReqNode(it) })
        }

        app.put("/quest/{id}") { ctx ->

            val quest = gson.fromJson(ctx.body(), DBQuest::class.java)
            val data = Quest(quest.id, quest.title, quest.description, quest.description2, quest.rewards, quest.conditions.map {
                QuestCondition(it.type, it.id, it.amount, 0, it.tooltip)
            })

            for (player in Bukkit.getOnlinePlayers()) {
                if (!FabricManager.isFabricPlayer(player)) continue
                // check completed status
                FabricManager.sendQuest(player,data)
            }

            ctx.status(200)
        }

        app.post("/quest/{id}") { ctx ->
            gson.fromJson(ctx.body(), Quest::class.java) ?: return@post

            Redis.db[QuestManager.QUEST_DB_KEY + ctx.pathParam("id")] = ctx.body()

            ctx.status(200)
        }

        app.get("/quest/{id}") { ctx ->
            val json = Redis.db[QuestManager.QUEST_DB_KEY + ctx.pathParam("id")]
            ctx.json( gson.fromJson(json, DBQuest::class.java))
        }
    }

    override fun onDisable() {
        app.stop()
    }

    override fun onReload() {
        app.stop()
    }

    fun reqNodeToDBNode(node : ReqQuestNode) : DBQuestNode {

        val connections = node.connectionsTo.split(" ").filter { it.isNotBlank() }.map { it.toInt() }
        val array = mutableListOf<Int>()
        array.addAll(connections)

        val id = if (node.questID.isNotBlank()) node.questID.toInt() else -1

        return DBQuestNode(id, node.itemID, node.title, node.x, node.y,
            array,node.isServerQuest.toBoolean())
    }

    fun dbNodeToReqNode(db : DBQuestNode): ReqQuestNode {
        return ReqQuestNode(db.questID.toString(), db.itemID, db.title, db.x, db.y,
            db.connectionsTo.joinToString(" "),db.isServerQuest.toString())
    }

    data class ReqQuestNode(val questID: String, val itemID : String, val title: String,
                           val x : Int, val y : Int, val connectionsTo: String,
                           val isServerQuest : String = false.toString())
}