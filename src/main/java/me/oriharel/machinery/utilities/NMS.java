package me.oriharel.machinery.utilities;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftMetaBlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

/**
 * NMS is Minecraft internals.
 * utility class for simplifying this process
 */
public final class NMS {
    /**
     * Convert a map of String, Object to an NBTTagCompound
     *
     * @param map the map to convert
     * @return NBTTagCompound
     */
    public static NBTTagCompound nbtFromMap(Map<String, Object> map) {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            nbtTagCompound.set(entry.getKey(), nbtFromMapHelper(entry.getValue()));
        }
        return nbtTagCompound;
    }

    /**
     * finds type and convert to NBTBase and helps with applying recursive solution
     * @param value the object to find the type for
     * @return NBTBase
     */
    private static NBTBase nbtFromMapHelper(Object value) {
        if (value instanceof Map) {
            return nbtFromMap((Map<String, Object>) value);
        } else if (value instanceof List) {
            NBTTagList nbtTagList = new NBTTagList();
            List list = ((List) value);
            for (Object o : list) {
                nbtTagList.add(nbtFromMapHelper(o));
            }
            return nbtTagList;
        } else if (value instanceof String) {
            return NBTTagString.a(((String) value).replace("Â§", "§"));
        } else if (value instanceof Integer) {
            return NBTTagInt.a((Integer) value);
        } else if (value instanceof Byte) {
            return NBTTagByte.a((byte) value);
        } else if (value instanceof byte[]) {
            return new NBTTagByteArray((byte[]) value);
        } else if (value instanceof Boolean) {
            return NBTTagByte.a((Boolean) value);
        } else if (value instanceof Double) {
            return NBTTagDouble.a((Double) value);
        } else if (value instanceof Float) {
            return NBTTagFloat.a((Float) value);
        } else if (value instanceof int[]) {
            return new NBTTagIntArray((int[]) value);
        } else if (value instanceof Long) {
            return NBTTagLong.a((Long) value);
        } else if (value instanceof Short) {
            return NBTTagShort.a((Short) value);
        }
        return null;
    }

    /**
     * Used when extending an ItemStack and in need of changing that ItemStacks NBT since API methods provided by Bukkit clone the item
     * @param itemStack itemstack to get reference to unhandled NBT for
     * @return reference to unhandled nbt of itemstack
     */
    @Nullable
    public static Map<String, NBTBase> getItemStackUnhandledNBT(ItemStack itemStack) {
        ItemMeta metaReference = getItemStackMetaReference(itemStack);
        return ReflectionUtils.Fields.getFieldValueOfUnknownClass(metaReference, "org.bukkit.craftbukkit.v1_15_R1.inventory.CraftMetaItem", "unhandledTags");
    }

    /**
     * get a clone of the NBT map of an item
     * @param itemStack itemstack to get clone of nbt map for
     * @return clone of nbt map of itemstack
     */
    @Nullable
    public static Map<String, NBTBase> getItemStackNBTTMapClone(ItemStack itemStack) {
        NBTTagCompound compound = CraftItemStack.asNMSCopy(itemStack).getTag();
        return ReflectionUtils.Fields.getFieldValueOfUnknownClass(compound, NBTTagCompound.class, "map");
    }

    /**
     * used for directly changing item meta of an itemstack
     * ItemStack#getItemMeta returns a clone of the items's itemmeta
     * @param itemStack the itemstack to get the reference for
     * @return reference to itemstack meta
     */
    @Nullable
    public static ItemMeta getItemStackMetaReference(ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) itemStack.setItemMeta(new ItemStack(itemStack.getType(), itemStack.getAmount()).getItemMeta());
        return ReflectionUtils.Fields.getFieldValueOfUnknownClass(itemStack, ItemStack.class, "meta");
    }

}
