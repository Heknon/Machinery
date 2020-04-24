package me.oriharel.machinery.utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.fuel.PlayerFuel;
import me.oriharel.machinery.machine.Machine;
import me.oriharel.machinery.machine.MachineFactory;
import me.oriharel.machinery.machine.PlayerMachine;
import me.oriharel.machinery.serialization.*;
import me.oriharel.machinery.upgrades.AbstractUpgrade;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public final class Utils {

    public static Machinery MACHINERY_INSTANCE = null;

    public static long locationToLong(Location location) {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        return ((long) x & 0x7FFFFFF) | (((long) z & 0x7FFFFFF) << 27) | ((long) y << 54);
    }

    public static Location longToLocation(long packed) {
        int x = (int) ((packed << 37) >> 37);
        int y = (int) (packed >>> 54);
        int z = (int) ((packed << 10) >> 37);
        return new Location(null, x, y, z);
    }

    public static Location longToLocation(long packed, World world) {
        Location loc = longToLocation(packed);
        loc.setWorld(world);
        return loc;
    }

    public static boolean inventoryHasSpaceForItemAdd(org.bukkit.inventory.Inventory inventory) {
        return inventory.firstEmpty() != -1;
    }

    public static <T extends ItemStack> void giveItemsOrDrop(HumanEntity player, Collection<T> items, boolean cloneDrop) {
        Inventory playerInventory = player.getInventory();
        Location playerLocation = player.getLocation();
        World playerWorld = player.getWorld();

        for (ItemStack item : items) {
            if (Utils.inventoryHasSpaceForItemAdd(playerInventory)) {
                int amount = item.getAmount();
                ItemStack toAdd = cloneDrop ? item.clone() : item;
                if (cloneDrop) toAdd.setAmount(amount);
                playerInventory.addItem(toAdd);

            } else {
                playerWorld.dropItemNaturally(playerLocation, cloneDrop ? item.clone() : item);
            }
        }
    }

    public static <T extends ItemStack> void giveItemOrDrop(HumanEntity player, T item, boolean cloneDrop) {
        Inventory playerInventory = player.getInventory();
        Location playerLocation = player.getLocation();
        World playerWorld = player.getWorld();

        if (Utils.inventoryHasSpaceForItemAdd(playerInventory)) {
            int amount = item.getAmount();
            ItemStack toAdd = cloneDrop ? item.clone() : item;
            if (cloneDrop) toAdd.setAmount(amount);
            playerInventory.addItem(toAdd);

        } else {
            playerWorld.dropItemNaturally(playerLocation, cloneDrop ? item.clone() : item);
        }
    }

    public static <T extends Machine> Gson getGsonSerializationBuilderInstance(Class<T> machineType, MachineFactory factory) {
        return new GsonBuilder().registerTypeHierarchyAdapter(machineType, machineType == PlayerMachine.class ? new PlayerMachineTypeAdapter(factory) :
                new MachineTypeAdapter<>(factory)).registerTypeHierarchyAdapter(AbstractUpgrade.class,
                new AbstractUpgradeTypeAdapter()).registerTypeHierarchyAdapter(Location.class, new LocationTypeAdapter())
                .registerTypeHierarchyAdapter(NBTTagCompound.class, new NBTTagCompoundTypeAdapter())
                .registerTypeHierarchyAdapter(PlayerFuel.class, new PlayerFuelTypeAdapter()).create();
    }
}
