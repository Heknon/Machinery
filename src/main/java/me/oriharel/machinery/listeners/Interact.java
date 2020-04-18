package me.oriharel.machinery.listeners;

import com.google.common.collect.Sets;
import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.fuel.PlayerFuel;
import me.oriharel.machinery.inventory.*;
import me.oriharel.machinery.items.MachineBlock;
import me.oriharel.machinery.machine.PlayerMachine;
import me.oriharel.machinery.upgrades.AbstractUpgrade;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Interact implements Listener {

    private Machinery machinery;
    private DecimalFormat decimalFormat;

    public Interact(Machinery machinery) {
        this.machinery = machinery;
        decimalFormat = new DecimalFormat("#.##");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getClickedBlock() == null) return;
        if (!(e.getClickedBlock().getState() instanceof TileState)) return;

        if (machinery.getMachineManager().getMachinePartLocations().contains(e.getClickedBlock().getLocation())) {
            e.setUseInteractedBlock(Event.Result.DENY);
        }

        PlayerMachine machine = machinery.getMachineManager().getMachineCores().get(e.getClickedBlock().getLocation());
        if (machine != null) {
            e.setUseInteractedBlock(Event.Result.DENY);
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    machinery.getFileManager().getConfig("config.yml").get().getString("open_machine_gui_message")));
            e.getPlayer().closeInventory();

            e.getPlayer().openInventory(craftInventory(machine, e.getPlayer()).getInventory());


        }
    }

    private Inventory craftInventory(PlayerMachine machine, Player player) {
        Map<String, InventoryPage> routes = new HashMap<>();
        InventoryItem fillment = new InventoryFillmentItem(Material.GRAY_STAINED_GLASS_PANE, 1, "");
        InventoryItem upgradeFillment = new InventoryFillmentItem(Material.GRAY_STAINED_GLASS_PANE, 1, ChatColor.RED + "BLOCKED", "§cPurchase previous upgrades.");
        Inventory inventory = new Inventory(routes, player);
        routes.put("start", new InventoryPage(54, machine.getType().toTitle(), fillment, Sets.newHashSet(
                new InventoryNavigationItem("bank", inventory, 20, Material.GHAST_TEAR, 1, "Bank", "§9Click to open the withdrawal menu",
                        "§bThere are currently §e" + decimalFormat.format(machine.getZenCoinsGained()) + " §bZen Coins stored in the machine"),
                new InventoryNavigationItem("resources", inventory, 24, Material.PAPER, 1, "Storage", "§9Click to open open the storage of the machine",
                        "§bThere are currently §e" + decimalFormat.format(machine.getResourcesGained().stream().mapToInt(ItemStack::getAmount).sum()) + " " +
                                "§bresources " +
                                "stored in the " +
                                "machine"),
                new InventoryItem(45, Material.REPEATER, 1, "Statistics", "§9Statistics:",
                        "§bTotal resources gained: §e" + decimalFormat.format(machine.getTotalResourcesGained()),
                        "§bTotal Zen Coins gained: §e" + decimalFormat.format(machine.getTotalZenCoinsGained())),
                new InventoryNavigationItem("fuels", inventory, 48, Material.OBSIDIAN, 1, "Fuel", "§9Fuel up your machine.",
                        decimalFormat.format(machine.getFuels().stream().mapToInt(PlayerFuel::getEnergy).sum()) + "/" + decimalFormat.format(machine.getMaxFuel())),
                new InventoryNavigationItem("upgrades", inventory, 50, Material.HOPPER, 1, "Upgrades", "§9Upgrade your machine"),
                new InventoryNavigationItem("deconstructConfirm", inventory, 53, Material.BARRIER, 1, "DECONSTRUCT MACHINE", "§cDeconstruct your machine to block form.")
        )));

        List<ItemStack> resourcesGained = machine.getResourcesGained();
        List<PlayerFuel> fuels = machine.getFuels();
        HashMap<Material, ItemStack> resources = new HashMap<>();
        for (ItemStack is : resourcesGained) {
            if (!resources.containsKey(is.getType())) resources.put(is.getType(), new ItemStack(is.getType(), 0));
            ItemStack itemStack = resources.get(is.getType());
            itemStack.setAmount(itemStack.getAmount() + is.getAmount());
        }
        List<ItemStack> resourcesGainedMerged = new ArrayList<>(resources.values());
        routes.put("resources", new InventoryPage(9, "Resources", null,
                IntStream.range(0, Math.min(resourcesGainedMerged.size(), 9)).mapToObj(i -> new InventoryItem(i, resourcesGainedMerged.get(i)).setCancelOnClick(false)).collect(Collectors.toSet())));
        routes.put("fuels", new InventoryPage(9, "Fuels", null,
                IntStream.range(0, fuels.size()).mapToObj(i -> {
                    PlayerFuel fuel = fuels.get(i);
                    return new InventoryItem(i, fuel.getItem(fuel.getAmount()));
                }).collect(Collectors.toSet())));
        routes.put("bank", new InventoryPage(9, "Withdrawal", fillment, Sets.newHashSet(
                new InventoryItem(4, Material.MAGMA_CREAM, 1, "§6" + decimalFormat.format(machine.getZenCoinsGained()) + " Zen Coins")
        )));
        routes.put("deconstructConfirm", new InventoryPage(9, "Confirm Deconstruction", fillment, Sets.newHashSet(
                new InventoryItem(2, Material.RED_STAINED_GLASS_PANE, 1, "§4§lCANCEL"),
                new InventoryItem(6, Material.RED_STAINED_GLASS_PANE, 1, "§a§lAGREE", "§eThis will deconstruct your machine", "§eYou will be given your machine in the " +
                        "form of a block").setOnClick(() -> {
                    player.closeInventory();
                    MachineBlock machineBlock = machine.deconstruct();
                    player.getInventory().addItem(machineBlock.getItemStackWithAppliedPlaceholders());
                })
        )));
        Set<InventoryItem> upgradeItems = new HashSet<>();
        int upgradeNumber = 0;
        for (AbstractUpgrade upgrade : machine.getUpgrades()) {
            for (int i = 0; i < upgrade.getLevel(); i++) {
                upgradeItems.add(new InventoryItem(upgradeNumber * 9, Material.LIME_STAINED_GLASS_PANE, i + 1, ChatColor.DARK_AQUA + upgrade.getUpgradeName()));
            }
            ++upgradeNumber;
        }
        routes.put("upgrades", new InventoryPage(machine.getUpgrades().size() * 9, "Upgrades", upgradeFillment, upgradeItems));
        return inventory;
    }
}
