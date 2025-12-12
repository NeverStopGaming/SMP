package de.nick.waypointsharesystem.utils

import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.FireworkEffectMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionType
import java.net.MalformedURLException
import java.net.URL
import java.util.*

class SkullBuilder {
    private var itemStack: ItemStack?
    private var amount = 1
    private var customModelData = 0
    private var potionDuration = 0
    private var potionAmplifier = 0
    private var name: String? = null
    private var skullOwner: String? = null
    private var uuid: UUID? = null
    private var lore: List<String> = ArrayList()
    private val enchantments = HashMap<Enchantment, Int>()
    private var color: Color? = null
    private var potionEffectType: PotionEffectType? = null
    private var potionType: PotionType? = null
    private var unbreakable = false
    private var hideFlags = false
    private var glow = false

    constructor() {
        this.itemStack = ItemStack(Material.PLAYER_HEAD)
    }

    constructor(itemStack: ItemStack) {
        this.itemStack = itemStack.clone()
    }

    fun setAmount(amount: Int): SkullBuilder {
        this.amount = amount
        return this
    }

    fun setCustomModelData(customModelData: Int): SkullBuilder {
        this.customModelData = customModelData
        return this
    }

    fun setDisplayName(name: String?): SkullBuilder {
        this.name = name
        return this
    }

    fun setSkullOwner(skullOwner: String?): SkullBuilder {
        this.skullOwner = skullOwner
        return this
    }

    fun setSkullOwner(uuid: UUID?): SkullBuilder {
        this.uuid = uuid
        return this
    }

    fun setLore(lore: List<String>?): SkullBuilder {
        this.lore = ArrayList(lore)
        return this
    }

    fun addEnchantment(enchantment: Enchantment, level: Int): SkullBuilder {
        enchantments[enchantment] = level
        return this
    }

    fun setColor(color: Color?): SkullBuilder {
        this.color = color
        return this
    }

    fun setPotionEffectType(potionEffectType: PotionEffectType?, duration: Int, amplifier: Int): SkullBuilder {
        this.potionEffectType = potionEffectType
        this.potionDuration = duration
        this.potionAmplifier = amplifier
        return this
    }

    fun setPotionType(potionType: PotionType?): SkullBuilder {
        this.potionType = potionType
        return this
    }

    fun setUnbreakable(unbreakable: Boolean): SkullBuilder {
        this.unbreakable = unbreakable
        return this
    }

    fun setHideFlags(hideFlags: Boolean): SkullBuilder {
        this.hideFlags = hideFlags
        return this
    }

    fun setGlow(glow: Boolean): SkullBuilder {
        this.glow = glow
        return this
    }

    fun build(): ItemStack? {
        checkNotNull(this.itemStack) { "ItemStack cannot be null" }

        itemStack!!.amount = this.amount
        val itemMeta = itemStack!!.itemMeta
            ?: throw IllegalStateException("ItemMeta cannot be null")

        itemMeta.isUnbreakable = unbreakable

        if (this.name != null) {
            itemMeta.setDisplayName(this.name)
        }
        if (!lore.isEmpty()) {
            itemMeta.lore = lore
        }

        if (!enchantments.isEmpty()) {
            for (enchantment in enchantments.keys) {
                itemMeta.addEnchant(enchantment, enchantments[enchantment]!!, true)
            }
        }

        if (this.customModelData > 0) {
            itemMeta.setCustomModelData(this.customModelData)
            this.hideFlags = true
        }

        if (this.hideFlags) {
            itemMeta.addItemFlags(*ItemFlag.entries.toTypedArray())
        }


        itemStack!!.setItemMeta(itemMeta)

        if (this.color != null) {
            if (itemStack!!.type == Material.FIREWORK_STAR) {
                val fireworkEffectMeta = itemStack!!.itemMeta as FireworkEffectMeta
                val fireworkEffect = FireworkEffect.builder().withColor(color!!).build()
                fireworkEffectMeta.effect = fireworkEffect
                itemStack!!.setItemMeta(fireworkEffectMeta)
            } else if (itemStack!!.itemMeta is LeatherArmorMeta) {
                val leatherArmorMeta = itemStack!!.itemMeta as LeatherArmorMeta
                leatherArmorMeta.setColor(this.color)
                itemStack!!.setItemMeta(leatherArmorMeta)
            }
        }

        if (this.uuid != null && itemStack!!.itemMeta is SkullMeta) {
            val skullMeta = itemStack!!.itemMeta as SkullMeta
            val playerProfile = Bukkit.createPlayerProfile(uuid!!)
            skullMeta.ownerProfile = playerProfile
            itemStack!!.setItemMeta(skullMeta)
        }

        if (this.skullOwner != null && itemStack!!.itemMeta is SkullMeta) {
            val skullMeta = itemStack!!.itemMeta as SkullMeta
            val playerProfile = Bukkit.createPlayerProfile(UUID.randomUUID())
            val playerTextures = playerProfile.textures
            try {
                playerTextures.skin = URL("http://textures.minecraft.net/texture/" + this.skullOwner)
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            }
            playerProfile.setTextures(playerTextures)
            skullMeta.ownerProfile = playerProfile
            itemStack!!.setItemMeta(skullMeta)
        }

        if (this.potionEffectType != null && itemStack!!.itemMeta is PotionMeta) {
            val potionMeta = itemStack!!.itemMeta as PotionMeta
            potionMeta.addCustomEffect(
                PotionEffect(potionEffectType!!, this.potionDuration, this.potionAmplifier),
                true
            )
            itemStack!!.setItemMeta(potionMeta)
        }


        return this.itemStack
    }
}