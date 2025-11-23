package net.derfarmer.levelsystem.listener

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent

object GoalListener : Listener {

    @EventHandler
    fun onEntityKill(e: EntityDeathEvent) {
        if (e.damageSource !is Player) return

    }
}