package net.derfarmer.playersystem.utils

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionType
import java.util.*

class ItemBuilder(material: Material?) {
    private var stack: ItemStack
    private val meta: ItemMeta

    init {
        this.stack = ItemStack(material!!)
        this.meta = stack.itemMeta
    }

    val itemMeta: ItemMeta
        get() = stack.itemMeta

    fun setGlow(glow: Boolean): ItemBuilder {
        if (glow) {
            addEnchant(Enchantment.KNOCKBACK, 1)
            addItemFlag(ItemFlag.HIDE_ENCHANTS)
        } else {
            val meta = itemMeta
            for (enchantment in meta.enchants.keys) {
                meta.removeEnchant(enchantment!!)
            }
        }
        return this
    }

    fun setUnbreakable(unbreakable: Boolean): ItemBuilder {
        meta.isUnbreakable = unbreakable
        stack.setItemMeta(meta)
        return this
    }

    fun setAmount(amount: Int): ItemBuilder {
        stack.amount = amount
        return this
    }

    fun setItemMeta(meta: ItemMeta?): ItemBuilder {
        stack.setItemMeta(meta)
        return this
    }

    fun setHead(owner: String?): ItemBuilder {
        if (meta is SkullMeta) {
            val skullMeta = meta
            skullMeta.setOwner(owner)
            setItemMeta(skullMeta)
        }
        return this
    }

    @Deprecated("use Components displayName(displayName : Component)")
    fun setDisplayName(displayName: String?): ItemBuilder {
        meta.setDisplayName(displayName)
        setItemMeta(meta)
        return this
    }

    fun displayName(displayName: Component): ItemBuilder {
        meta.displayName(displayName)
        setItemMeta(meta)
        return this
    }

    fun setItemStack(stack: ItemStack): ItemBuilder {
        this.stack = stack
        return this
    }

    @Deprecated("use lore(vararg lores : Component) now")
    fun setLore(vararg lore: String?): ItemBuilder {
        meta.lore = Arrays.asList(*lore)
        setItemMeta(meta)
        return this
    }

    fun lore(vararg lores: Component): ItemBuilder {
        return lore(lores.toList())
    }

    fun lore(lores: List<Component>): ItemBuilder {
        meta.lore(lores)
        setItemMeta(meta)
        return this
    }

    @Deprecated("use lore(vararg lores : Component) now")
    fun setLore(lore: List<String?>?): ItemBuilder {
        meta.lore = lore
        setItemMeta(meta)
        return this
    }

    fun addEnchant(enchantment: Enchantment?, level: Int): ItemBuilder {
        meta.addEnchant(enchantment!!, level, true)
        setItemMeta(meta)
        return this
    }

    fun addItemFlag(flag: ItemFlag?): ItemBuilder {
        meta.addItemFlags(flag!!)
        setItemMeta(meta)
        return this
    }

    fun setData(namespace: String, key: String, z: Boolean): ItemBuilder {
        meta.persistentDataContainer.set(NamespacedKey(namespace, key), PersistentDataType.BOOLEAN, z)
        setItemMeta(meta)
        return this
    }

    fun setData(namespace: String, key: String, lvl: Int): ItemBuilder {
        meta.persistentDataContainer.set(NamespacedKey(namespace, key), PersistentDataType.INTEGER, lvl)
        setItemMeta(meta)
        return this
    }

    fun setPotion(potionType: PotionType): ItemBuilder {
        if (meta !is PotionMeta) {
            return this
        }
        meta.basePotionType = potionType
        setItemMeta(meta)
        return this
    }


    fun build(): ItemStack {
        return stack
    }
}
