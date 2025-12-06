package de.nick.elevatorsystem.utils

import net.derfarmer.playersystem.utils.ItemBuilder
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

enum class AccessLevel(val item: ItemStack) {
    PUBLIC(
        ItemBuilder(Material.GREEN_WOOL)
            .setDisplayName("§aÖffentlich")
            .setLore("§7Alle Spieler können nutzen.")
            .build()
    ),
    CLAN(
        ItemBuilder(Material.YELLOW_WOOL)
            .setDisplayName("§eClan-Zugriff")
            .setLore("§7Alle aus dem Clan", "§7des Besitzers können nutzen.")
            .build()
    ),
    OWNER(
        ItemBuilder(Material.RED_WOOL)
            .setDisplayName("§cPrivat")
            .setLore("§7Nur der Besitzer kann nutzen.")
            .build()
    );
}