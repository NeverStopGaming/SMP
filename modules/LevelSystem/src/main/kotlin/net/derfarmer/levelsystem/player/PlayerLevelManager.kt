package net.derfarmer.levelsystem.player

import net.derfarmer.playersystem.PlayerManager
import org.bukkit.OfflinePlayer

object PlayerLevelManager {

    var OfflinePlayer.playerExp: Int
        set(exp) {
            PlayerManager.setPlayerValue(this, "exp", exp.toString())
        }
        get() {
            return (PlayerManager.getPlayerValue(this, "exp") ?: "0").toInt()
        }

    var OfflinePlayer.playerLevel: Int
        set(exp) {
            PlayerManager.setPlayerValue(this, "expLevel", exp.toString())
        }
        get() {
            return (PlayerManager.getPlayerValue(this, "expLevel") ?: "0").toInt()
        }
}