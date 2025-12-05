package de.nick.elevatorsystem.listener

import de.nick.elevatorsystem.utils.Elevator
import com.destroystokyo.paper.event.player.PlayerJumpEvent
import net.derfarmer.moduleloader.sendMSG
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.block.DaylightDetector
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.persistence.PersistentDataType
import java.time.Duration

object PlayerJumpListener : Listener {

    @EventHandler
    fun onMove(event: PlayerJumpEvent) {
        val player = event.player
        val location = player.location.clone()

        if (location.block.type == Material.DAYLIGHT_DETECTOR) {
            val elevator = location.block.state as DaylightDetector
            val holder = Elevator(elevator)

            if (!holder.canAccess(player) && !player.isOp) {
                player.sendMSG("elevator.noPermission")
                return
            }

            if (location.block.type == Material.DAYLIGHT_DETECTOR) {
                for (y in 2..200) {
                    val checkLocation = location.clone().add(0.0, y.toDouble(), 0.0)
                    val block = checkLocation.block

                    if (block.type == Material.DAYLIGHT_DETECTOR) {
                        val daylightDetector = block.state as DaylightDetector
                        val holder = Elevator(daylightDetector)
                        val isElevator = daylightDetector.persistentDataContainer.getOrDefault(
                            NamespacedKey("ev1", "elevator"),
                            PersistentDataType.BOOLEAN,
                            false
                        )

                        if (!isElevator) continue

                        if (!holder.canAccess(player) && !player.isOp) {
                            player.sendMSG("elevator.noPermission")
                            return
                        }

                        val teleportLocation = block.location.clone()
                        teleportLocation.yaw = player.yaw
                        teleportLocation.pitch = player.pitch
                        player.teleportAsync(teleportLocation.add(0.5, 0.5, 0.5))
                        player.showTitle(Title.title(Component.text("§a▲ Up ▲"), Component.text(""), Title.Times.times(
                            Duration.ofMillis(10), Duration.ofSeconds(1), Duration.ofMillis(10))))
                        player.playSound(player.location, Sound.ENTITY_ENDERMAN_TELEPORT, 2f, 2f)



                        return
                    }
                }
            }
        }
    }
}