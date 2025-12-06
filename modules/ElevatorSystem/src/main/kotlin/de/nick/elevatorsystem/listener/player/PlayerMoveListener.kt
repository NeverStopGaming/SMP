package de.nick.elevatorsystem.listener.player

import de.nick.elevatorsystem.utils.PlayerFlyStart
import org.bukkit.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent


object PlayerMoveListener: Listener {

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        val player = event.player

        if (PlayerFlyStart.fly.contains(player)) {
            val loc = player.getLocation()

            player.world.spawnParticle(
                Particle.FIREWORK,
                loc,
                2,
                0.02, 0.02, 0.02,
                0.0
            )
        }


        if (player.isOnGround && PlayerFlyStart.fly.contains(player)) {
            player.isGliding = false
            PlayerFlyStart.fly.remove(player)
        }
    }
}