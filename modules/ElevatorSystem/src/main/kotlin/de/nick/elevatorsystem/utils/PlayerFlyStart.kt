package de.nick.elevatorsystem.utils

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityToggleGlideEvent

object PlayerFlyStart : Listener {
    val fly: java.util.ArrayList<Player> = ArrayList()

    @EventHandler
    fun onGlide(event: EntityToggleGlideEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player

            if (fly.contains(player)) {
                event.isCancelled = true
            }
        }
    }
}
