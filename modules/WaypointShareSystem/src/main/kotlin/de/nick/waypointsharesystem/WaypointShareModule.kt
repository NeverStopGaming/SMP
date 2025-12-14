package de.nick.waypointsharesystem

import de.nick.waypointsharesystem.listener.ChatListener
import de.nick.waypointsharesystem.listener.InventoryClickListener
import net.derfarmer.moduleloader.modules.Module

object WaypointShareModule : Module() {
    override fun onEnable() {
        println("EV WaypointShare loading")
        register(ChatListener)
        register(InventoryClickListener)
        println("EV WaypointShare Enabled")
    }

    override fun onDisable() {
    }

    override fun onReload() {
    }
}