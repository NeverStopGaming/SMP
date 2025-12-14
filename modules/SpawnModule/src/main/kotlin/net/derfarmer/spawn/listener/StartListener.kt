package net.derfarmer.spawn.listener

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.weather.WeatherChangeEvent

object StartListener : Listener {
    @EventHandler
    fun onWeatherChange(event: WeatherChangeEvent) {
        if (event.toWeatherState()) {
            event.isCancelled = true
        }
    }
    private val spawnLocation = Bukkit.getWorld("world")!!.spawnLocation

    @EventHandler
    fun onMove(e : PlayerMoveEvent) {
        if (e.player.location.y <= spawnLocation.y - 5 || spawnLocation.distance(e.player.location) >= 60) {
            if (!e.player.isOp) {
                e.player.teleportAsync(spawnLocation.toCenterLocation())
            }
        }
    }

    @EventHandler
    fun onFoodLevelChange(event: FoodLevelChangeEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        event.isCancelled = true
    }
}