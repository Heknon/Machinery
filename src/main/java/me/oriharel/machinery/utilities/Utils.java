package me.oriharel.machinery.utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.machine.Machine;
import me.oriharel.machinery.machine.MachineFactory;
import me.oriharel.machinery.machine.PlayerMachine;
import me.oriharel.machinery.message.Placeholder;
import me.oriharel.machinery.serialization.*;
import me.oriharel.machinery.upgrades.AbstractUpgrade;
import me.oriharel.machinery.upgrades.UpgradeType;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public final class Utils {

    public static final DecimalFormat COMMA_NUMBER_FORMAT = new DecimalFormat("#,###");
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
        int amount = item.getAmount();
        ItemStack toAdd = cloneDrop ? item.clone() : item;
        if (cloneDrop) toAdd.setAmount(amount);

        if (Utils.inventoryHasSpaceForItemAdd(playerInventory)) {
            playerInventory.addItem(toAdd);
        } else {
            playerWorld.dropItemNaturally(playerLocation, toAdd);
        }
    }

    public static <T extends Machine> Gson getGsonSerializationBuilderInstance(Class<T> machineType, MachineFactory factory) {
        return new GsonBuilder().registerTypeHierarchyAdapter(machineType, machineType == PlayerMachine.class ? new PlayerMachineTypeAdapter(factory) :
                new MachineTypeAdapter<>(factory)).registerTypeHierarchyAdapter(AbstractUpgrade.class,
                new AbstractUpgradeTypeAdapter()).registerTypeHierarchyAdapter(Location.class, new LocationTypeAdapter())
                .registerTypeHierarchyAdapter(NBTTagCompound.class, new NBTTagCompoundTypeAdapter()).registerTypeHierarchyAdapter(UUID.class, new UUIDTypeAdapter()).create();
    }

    public static List<Placeholder> getLocationPlaceholders(Location location, Placeholder... extraPlaceholders) {
        List<Placeholder> placeholders = Arrays.asList(extraPlaceholders);
        placeholders.add(new Placeholder("%x%", location.getBlockX()));
        placeholders.add(new Placeholder("%y%", location.getBlockY()));
        placeholders.add(new Placeholder("%z%", location.getBlockZ()));
        placeholders.add(new Placeholder("%world%", location.getWorld().getName()));
        return placeholders;
    }

    public static List<Placeholder> getLocationPlaceholders(Location location, List<Placeholder> extraPlaceholders) {
        return getLocationPlaceholders(location, extraPlaceholders.toArray(new Placeholder[0]));
    }

    public static List<Placeholder> getMachinePlaceholders(PlayerMachine machine, List<Placeholder> extraPlaceholders) {
        return getMachinePlaceholders(machine, extraPlaceholders.toArray(new Placeholder[0]));
    }

    public static List<Placeholder> getAmountThingPlaceholder(int amount, String thing, List<Placeholder> extraPlaceholders) {
        return getAmountThingPlaceholder(amount, thing, extraPlaceholders.toArray(new Placeholder[0]));
    }

    public static List<Placeholder> getMachinePlaceholders(PlayerMachine machine, Placeholder... extraPlaceholders) {
        List<Placeholder> placeholders = Arrays.asList(extraPlaceholders);
        placeholders.add(new Placeholder("%machine_type%", machine.getType().toTitle()));
        placeholders.add(new Placeholder("%machine_energy%", machine.getEnergyInMachine()));
        placeholders.add(new Placeholder("%machine_max_fuel%", machine.getMaxFuel()));
        placeholders.add(new Placeholder("%machine_fuel_deficiency%", machine.getFuelDeficiency()));
        placeholders.add(new Placeholder("%machine_total_zen_coins_gained%", (int) machine.getTotalZenCoinsGained()));
        placeholders.add(new Placeholder("%machine_zen_coins_gained%", (int) machine.getZenCoinsGained()));
        placeholders.add(new Placeholder("%machine_total_resources_gained%", (int) machine.getTotalResourcesGained()));
        placeholders.add(new Placeholder("%machine_resources_gained%", machine.getResourcesGained().values().stream().mapToInt(ItemStack::getAmount).sum()));
        placeholders.add(new Placeholder("%upgrade_loot_bonus_name%",
                machine.getUpgrades().stream().filter(u -> u.getUpgradeType() == UpgradeType.LOOT_BONUS).findAny().get().getUpgradeName()));
        placeholders.add(new Placeholder("%upgrade_loot_bonus_level%",
                machine.getUpgrades().stream().filter(u -> u.getUpgradeType() == UpgradeType.LOOT_BONUS).findAny().get().getLevel()));
        placeholders.add(new Placeholder("%upgrade_speed_name%",
                machine.getUpgrades().stream().filter(u -> u.getUpgradeType() == UpgradeType.SPEED).findAny().get().getUpgradeName()));
        placeholders.add(new Placeholder("%upgrade_speed_level%",
                machine.getUpgrades().stream().filter(u -> u.getUpgradeType() == UpgradeType.SPEED).findAny().get().getLevel()));
        return placeholders;
    }

    public static List<Placeholder> getAmountThingPlaceholder(int amount, String thing, Placeholder... extraPlaceholders) {
        List<Placeholder> placeholders = Arrays.asList(extraPlaceholders);
        placeholders.add(new Placeholder("%amount%", amount));
        placeholders.add(new Placeholder("%thing%", thing));
        return placeholders;
    }
}
