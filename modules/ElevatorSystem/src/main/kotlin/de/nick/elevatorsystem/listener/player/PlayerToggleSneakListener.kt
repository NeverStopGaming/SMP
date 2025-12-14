package de.nick.elevatorsystem.listener.player

import de.nick.elevatorsystem.utils.Elevator
import de.nick.elevatorsystem.utils.Launcher
import de.nick.elevatorsystem.utils.PlayerFlyStart
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.derfarmer.moduleloader.sendMSG
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.*
import org.bukkit.block.BlockFace
import org.bukkit.block.DaylightDetector
import org.bukkit.block.Dispenser
import org.bukkit.block.data.Directional
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.Vector
import java.time.Duration

object PlayerToggleSneakListener : Listener {

    @EventHandler
    fun onSneak(event: PlayerToggleSneakEvent) {
        val player = event.player
        val location = player.location.clone()

        if (location.block.type == Material.DAYLIGHT_DETECTOR && player.isSneaking) {
            val elevator = location.block.state as DaylightDetector
            val holder = Elevator(elevator)


            if (!holder.canAccess(player) && !player.isOp) {
                player.sendMSG("elevator.noPermission")
                return
            }

            for (y in -2 downTo -200) {
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

                    val elevatorLocation = block.location.clone()
                    elevatorLocation.yaw = player.yaw
                    elevatorLocation.pitch = player.pitch

                    player.teleportAsync(elevatorLocation.add(0.5, 0.5, 0.5))
                    player.showTitle(
                        Title.title(
                            Component.text("§c▼ Down ▼"), Component.text(""), Title.Times.times(
                                Duration.ofMillis(10), Duration.ofSeconds(1), Duration.ofMillis(10)
                            )
                        )
                    )
                    player.playSound(player.location, Sound.ENTITY_ENDERMAN_TELEPORT, 2f, 2f)
                    return
                }
            }
        }

        if (location.block.getRelative(BlockFace.DOWN).type == Material.DISPENSER && player.isSneaking) {
            handleElytraLauncher(player, location)
            return
        }
    }

    private fun handleElytraLauncher(player: Player, location: Location) {
        val dispenserLocation = location.add(BlockFace.DOWN.direction)
        val dispenserBlock = dispenserLocation.block
        val dispenser = dispenserBlock.state as Dispenser

        val launcherLvl = dispenser.persistentDataContainer.getOrDefault(
            NamespacedKey("ev1", "elytra_launcher"),
            PersistentDataType.INTEGER,
            0
        )

        val holder = Launcher(dispenser, launcherLvl)

        if (launcherLvl == 100) {
            triggerElytraBoost(player, 10.0)
            return
        }

        if (launcherLvl < 1) {
            return
        }

        if (!holder.canAccess(player) && !player.isOp) {
            player.sendMSG("elevator.noPermission")
            return
        }

        if ((player.inventory.chestplate?.type ?: Material.AIR) != Material.ELYTRA) {
            player.sendActionBar(Component.text("§cDazu brauchst du eine Elytra."))
            return
        }

        val dispenserInventory = dispenser.inventory
        if ((dispenserBlock.blockData as Directional).facing != BlockFace.UP) return

        var foundGunpowder = false
        for (i in 0 until dispenserInventory.size) {
            val itemStack = dispenserInventory.getItem(i)
            if (itemStack != null && itemStack.type == Material.GUNPOWDER) {
                foundGunpowder = true
                itemStack.subtract(1)
                break
            }
        }

        if (!foundGunpowder) {
            player.sendActionBar(Component.text("Denke daran, ohne Treibstoff funktioniert der Elytra Launcher nicht."))
            return
        }

        when (launcherLvl) {
            1 -> {
                triggerElytraBoost(player, 2.0)
            }

            2 -> {
                triggerElytraBoost(player, 4.0)
            }

            3 -> {
                triggerElytraBoost(player, 10.0)
            }
        }
    }

    private fun triggerElytraBoost(player: Player, finalVelocityMultiplier: Double) {

        GlobalScope.launch {
            player.world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, player.location, 50, 0.1, 0.1, 0.1, 0.1)
            player.world.spawnParticle(Particle.FLAME, player.location, 30, 0.1, 0.1, 0.1, 0.1)
            player.playSound(player.location, Sound.ENTITY_WITHER_SHOOT, 20F, 10f)
            PlayerFlyStart.fly.add(player)
            player.velocity = Vector(0, 10, 0)

            for (i in 3 downTo 0) {
                delay(500)
                player.showTitle(Title.title(Component.text("Boost in"), Component.text(i)))
            }
            player.showTitle(Title.title(Component.text(""), Component.text("")))

            player.world.spawnParticle(Particle.FIREWORK, player.location, 180, 0.1, 0.1, 0.1, 0.1)
            player.velocity = player.eyeLocation.direction.multiply(finalVelocityMultiplier)
            player.isGliding = true
        }
    }
}