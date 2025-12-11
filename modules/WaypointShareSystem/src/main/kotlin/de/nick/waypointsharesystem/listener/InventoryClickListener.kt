package de.nick.waypointsharesystem.listener

object InventoryClickListener : Listener {

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {

        if (event.inventory.holder !is ShareInventory) return

        val player = event.whoClicked
        if(player !is Player) return

        event.isCancelled = true

        val waypointMessage = ChatListener.getLastWaypointMessage(player.uniqueId) ?: return

        when(event.slot) {
            10 -> {
                if (ChatListener.playerWaypointWaiting.contains(player)) {
                    player.sendMessage("${Config.prefix}§7Schreibe den Spielernamen in den Chat.")
                    player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 10.0F, 10.0F)
                    return
                }
                ChatListener.playerWaypointWaiting.add(player)
                player.sendMessage("${Config.prefix}§7Schreibe den Spielernamen in den Chat.")
                player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 10.0F, 10.0F)
            }
            13 -> {
                ChatListener.playerWaypointMessages.remove(player.uniqueId)
                Bukkit.broadcast(Component.text(""))
                Bukkit.broadcast(Component.text("${Config.prefix}§7Der Spieler §a" + player.name + " §7hat ein §aWaypoint §7geteilt"))
                Bukkit.broadcast(Component.text(waypointMessage))
                Bukkit.broadcast(Component.text(""))
                player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 10.0F, 10.0F)
            }
            16 -> {
                ChatListener.playerWaypointMessages.remove(player.uniqueId)
                if (player.getClan() == "") {

                    player.sendMessage("${Config.prefix}§7Du bist aktuell in §ckeinem §7Clan")
                    player.playSound(player.location, Sound.BLOCK_GLASS_BREAK, 10.0F, 10.0F)
                    return
                }

                ClanManager.sendMessage(player, "<green>" + player.name + " <gray>hat ein Waypoint geteilt");
                ClanManager.sendMessage(player, waypointMessage)

                Bukkit.getOnlinePlayers().forEach { onlinePlayer ->
                    if (onlinePlayer.getClan() == player.getClan()) {

                        onlinePlayer.playSound(onlinePlayer.location, Sound.BLOCK_NOTE_BLOCK_BASS, 10.0F, 10.0F)
                    }
                }

            }
        }

        WaypointShare.plugin.launch {
            delay(5)
            withContext(WaypointShare.plugin.entityDispatcher(player)) {
                player.closeInventory()
            }
        }
    }

}