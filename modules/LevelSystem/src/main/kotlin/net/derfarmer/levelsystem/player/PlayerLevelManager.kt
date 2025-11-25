package net.derfarmer.levelsystem.player

import net.derfarmer.playersystem.PlayerManager
import org.bukkit.OfflinePlayer
import kotlin.math.pow
import kotlin.math.roundToInt

object PlayerLevelManager {

    const val SCALER = 40.0
    const val POWER = 2.5
    fun calcXPRequiredForLevel(level: Int) = (SCALER * level.toDouble().pow(POWER)).roundToInt()

    fun OfflinePlayer.addPlayerXP(xp: Int) {
        playerXP += xp

        while (playerXP >= calcXPRequiredForLevel(playerLevel + 1)) {
            playerXP -= calcXPRequiredForLevel(playerLevel + 1)
            playerLevel += 1
        }
    }

    var OfflinePlayer.playerXP: Int
        set(xp) {
            PlayerManager.setPlayerValue(this, "xp", xp.toString())
        }
        get() {
            return (PlayerManager.getPlayerValue(this, "xp") ?: "0").toInt()
        }

    var OfflinePlayer.playerLevel: Int
        set(xpLevel) {
            PlayerManager.setPlayerValue(this, "xpLevel", xpLevel.toString())
        }
        get() {
            return (PlayerManager.getPlayerValue(this, "xpLevel") ?: "0").toInt()
        }
}