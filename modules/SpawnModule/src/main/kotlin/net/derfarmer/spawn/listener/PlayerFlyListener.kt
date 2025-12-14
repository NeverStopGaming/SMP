package net.derfarmer.spawn.listener

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityToggleGlideEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.event.player.PlayerToggleSneakEvent

object PlayerFlyStart : Listener {

    @EventHandler
    fun onPlayerSwapHandItems(event: PlayerSwapHandItemsEvent) {
        val player = event.player

        if (fly.contains(player)) {
            return
        }

        if (player.world.name == "world") {
            if (player.location.distance(SpawnListener.spawnLocation) <= 60) {
                player.isGliding = true
                player.velocity.multiply(10)
                fly.add(player)
            }
        }
    }

    @EventHandler
    fun onGlide(event: EntityToggleGlideEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player

            if (fly.contains(player)) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onSneak(e : PlayerToggleSneakEvent) {
        if (fly.contains(e.player)) fly.remove(e.player)
    }

    val fly = mutableListOf<Player>()
}