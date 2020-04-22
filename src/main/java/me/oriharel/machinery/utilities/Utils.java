package me.oriharel.machinery.utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.machine.Machine;
import me.oriharel.machinery.machine.MachineFactory;
import me.oriharel.machinery.machine.PlayerMachine;
import me.oriharel.machinery.serialization.AbstractUpgradeTypeAdapter;
import me.oriharel.machinery.serialization.LocationTypeAdapter;
import me.oriharel.machinery.serialization.MachineTypeAdapter;
import me.oriharel.machinery.serialization.PlayerMachineTypeAdapter;
import me.oriharel.machinery.upgrades.AbstractUpgrade;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

public final class Utils {

    public static Machinery MACHINERY_INSTANCE = null;
    public static MachineFactory MACHINE_FACTORY_INSTANCE = null;

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

    public static boolean inventoryHasSpaceForItemAdd(org.bukkit.inventory.Inventory inventory, ItemStack toAdd) {
        for (ItemStack itemStack : inventory) {
            if (itemStack == null || itemStack.getType() == Material.AIR) return true;
            if (itemStack.getType() == toAdd.getType() && itemStack.getAmount() + toAdd.getAmount() <= 64) return true;
        }
        return false;
    }

    public static <T extends Machine> Gson getGsonSerializationBuilderInstance(Class<T> machineType, MachineFactory factory) {
        return new GsonBuilder().registerTypeHierarchyAdapter(machineType, machineType == PlayerMachine.class ? new PlayerMachineTypeAdapter(factory) :
                new MachineTypeAdapter<>(factory)).registerTypeHierarchyAdapter(AbstractUpgrade.class,
                new AbstractUpgradeTypeAdapter()).registerTypeHierarchyAdapter(Location.class, new LocationTypeAdapter()).create();

    }
}
