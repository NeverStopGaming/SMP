package net.derfarmer.levelsystem.common

import org.bukkit.entity.Entity

interface Goal {
    fun isComplete(): Boolean

    // from 0% to 100%
    fun complicationPercentage(): Int
}

class MobGoal(val mob: Entity, val amount: Int) : Goal {
    var killedMobs = 0

    override fun isComplete() = killedMobs >= amount

    override fun complicationPercentage() = (killedMobs * 100) / amount
}