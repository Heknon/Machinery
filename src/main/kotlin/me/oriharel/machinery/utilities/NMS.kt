package me.oriharel.machinery.utilities

import me.oriharel.machinery.utilities.ReflectionUtils.Fields
import net.minecraft.server.v1_15_R1.*
import org.bukkit.craftbukkit.libs.jline.internal.Nullable
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

/**
 * NMS is Minecraft internals.
 * utility class for simplifying this process
 */
object NMS {
    /**
     * Convert a map of String, Object to an NBTTagCompound
     *
     * @param map the map to convert
     * @return NBTTagCompound
     */
    fun nbtFromMap(map: Map<String?, Any>): NBTTagCompound {
        val nbtTagCompound = NBTTagCompound()
        for ((key, value) in map) {
            nbtTagCompound[key] = nbtFromMapHelper(value)
        }
        return nbtTagCompound
    }

    /**
     * finds type and convert to NBTBase and helps with applying recursive solution
     * @param value the object to find the type for
     * @return NBTBase
     */
    private fun nbtFromMapHelper(value: Any): NBTBase? {
        when (value) {
            is Map<*, *> -> {
                return nbtFromMap(value as Map<String?, Any>)
            }
            is List<*> -> {
                val nbtTagList = NBTTagList()

                for (o in value) {
                    if (o == null) continue
                    nbtTagList.add(nbtFromMapHelper(o))
                }
                return nbtTagList
            }
            is String -> {
                return NBTTagString.a(value.replace("Â§", "§"))
            }
            is Int -> {
                return NBTTagInt.a(value)
            }
            is Byte -> {
                return NBTTagByte.a(value)
            }
            is ByteArray -> {
                return NBTTagByteArray(value)
            }
            is Boolean -> {
                return NBTTagByte.a(value)
            }
            is Double -> {
                return NBTTagDouble.a(value)
            }
            is Float -> {
                return NBTTagFloat.a(value)
            }
            is IntArray -> {
                return NBTTagIntArray(value)
            }
            is Long -> {
                return NBTTagLong.a(value)
            }
            is Short -> {
                return NBTTagShort.a(value)
            }
            else -> return null
        }
    }

    /**
     * Used when extending an ItemStack and in need of changing that ItemStacks NBT since API methods provided by Bukkit clone the item
     * @param itemStack itemstack to get reference to unhandled NBT for
     * @return reference to unhandled nbt of itemstack
     */
    @Nullable
    fun getItemStackUnhandledNBT(itemStack: ItemStack): MutableMap<String?, NBTBase?> {
        val metaReference = getItemStackMetaReference(itemStack)
        return Fields.getFieldValueOfUnknownClass(metaReference, "org.bukkit.craftbukkit.v1_15_R1.inventory.CraftMetaItem", "unhandledTags")!!
    }

    /**
     * get a clone of the NBT map of an item
     * @param itemStack itemstack to get clone of nbt map for
     * @return clone of nbt map of itemstack
     */
    @Nullable
    fun getItemStackNBTTMapClone(itemStack: ItemStack?): Map<String, NBTBase> {
        val compound = CraftItemStack.asNMSCopy(itemStack).tag
        return Fields.getFieldValueOfUnknownClass(compound, NBTTagCompound::class.java, "map")!!
    }

    /**
     * used for directly changing item meta of an itemstack
     * ItemStack#getItemMeta returns a clone of the items's itemmeta
     * @param itemStack the itemstack to get the reference for
     * @return reference to itemstack meta
     */
    @Nullable
    fun getItemStackMetaReference(itemStack: ItemStack): ItemMeta? {
        if (!itemStack.hasItemMeta()) itemStack.itemMeta = ItemStack(itemStack.type, itemStack.amount).itemMeta
        return Fields.getFieldValueOfUnknownClass(itemStack, ItemStack::class.java, "meta")
    }
}