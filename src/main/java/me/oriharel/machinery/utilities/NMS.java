package me.oriharel.machinery.utilities;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

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

    public static void addNBTToItemStack(ItemStack itemStack, NBTTagCompound toAddCompound, boolean force) {
        if (toAddCompound == null) return;

        if (!itemStack.hasItemMeta()) itemStack.setItemMeta(new ItemStack(itemStack.getType(), itemStack.getAmount()).getItemMeta());

        Map<String, NBTBase> unhandled = getItemStackUnhandledNBT(itemStack);
        Map<String, NBTBase> nbtBaseMap = ReflectionUtils.Fields.getFieldValueOfObject(toAddCompound, "map");

        if (nbtBaseMap == null) return;

        for (Map.Entry<String, NBTBase> entry : nbtBaseMap.entrySet()) {
            if (force) {
                unhandled.put(entry.getKey(), entry.getValue());
            } else {
                unhandled.putIfAbsent(entry.getKey(), entry.getValue());
            }
        }
    }

    @Nullable
    public static Map<String, NBTBase> getItemStackUnhandledNBT(ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) itemStack.setItemMeta(new ItemStack(itemStack.getType(), itemStack.getAmount()).getItemMeta());

        ItemMeta metaReference = getItemStackMetaReference(itemStack);
        return ReflectionUtils.Fields.getFieldValueOfUnknownClass(metaReference, "org.bukkit.craftbukkit.v1_15_R1.inventory.CraftMetaItem", "unhandledTags");
    }

    @Nullable
    public static ItemMeta getItemStackMetaReference(ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) itemStack.setItemMeta(new ItemStack(itemStack.getType(), itemStack.getAmount()).getItemMeta());
        return ReflectionUtils.Fields.getFieldValueOfUnknownClass(itemStack, ItemStack.class, "meta");
    }

}
