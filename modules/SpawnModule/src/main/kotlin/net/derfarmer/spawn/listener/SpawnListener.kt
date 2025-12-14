package net.derfarmer.spawn.listener

import net.derfarmer.moduleloader.mm
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent

object SpawnListener : Listener{

    val spawnLocation = Bukkit.getWorld("world")!!.spawnLocation

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        if (event.entity !is Player) return
        if (event.entity.world.name == "world" && event.entity.location.distance(spawnLocation) <= 60) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (e.player.world.name == "world" && e.player.location.distance(spawnLocation) <= 60) {
            if (e.player.isOp) return
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockBreak(e: BlockPlaceEvent) {
        if (e.player.world.name == "world" && e.player.location.distance(spawnLocation) <= 60) {
            if (e.player.isOp) return
            e.isCancelled = true
        }
    }

    private val flyMSG = mm.deserialize("<gray>Drücke <aqua><key:key.swapOffhand> <gray>um Los zu <aqua>fliegen<gray>!")

    @EventHandler
    fun onMove(e: PlayerMoveEvent) {
        if (e.player.world.name == "world" && e.player.location.distance(spawnLocation) <= 60) {
            e.player.sendActionBar(flyMSG)
        }
    }

    @EventHandler
    fun onInteract(e : PlayerInteractEvent) {
        if (e.player.world.name != "world") return
        if (e.player.location.distance(spawnLocation) > 60) return
        if (e.player.isOp) return

        e.isCancelled = true
    }
}