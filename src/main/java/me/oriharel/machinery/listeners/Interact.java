package me.oriharel.machinery.listeners;

import com.google.common.collect.Sets;
import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.fuel.PlayerFuel;
import me.oriharel.machinery.inventory.*;
import me.oriharel.machinery.items.MachineBlock;
import me.oriharel.machinery.machine.PlayerMachine;
import me.oriharel.machinery.upgrades.AbstractUpgrade;
import org.apache.commons.lang.StringUtils;
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
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.BiPredicate;
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

        HashMap<Material, ItemStack> resourcesGained = machine.getResourcesGained();
        List<ItemStack> resources = new ArrayList<>(resourcesGained.values()).stream().filter(is -> is.getAmount() != 0).collect(Collectors.toList());

        Map<String, InventoryPage> routes = new HashMap<>();
        InventoryItem fillment = new InventoryFillmentItem(Material.GRAY_STAINED_GLASS_PANE, 1, "");
        InventoryItem upgradeFillment = new InventoryFillmentItem(Material.GRAY_STAINED_GLASS_PANE, 1, ChatColor.RED + "BLOCKED", "§cPurchase previous upgrade(s).");
        Inventory inventory = new Inventory(routes, player);
        routes.put("start", new InventoryPage(54, machine.getType().toTitle(), fillment, Sets.newHashSet(
                new InventoryNavigationItem("bank", inventory, 20, Material.GHAST_TEAR, 1, "Bank", "§9Click to open the withdrawal menu",
                        "§bThere are currently §e" + decimalFormat.format(machine.getZenCoinsGained()) + " §bZen Coins stored in the machine"),
                new InventoryNavigationItem("resources", inventory, 24, Material.PAPER, 1, "Storage", "§9Click to open open the storage of the machine",
                        "§bThere are currently §e" + decimalFormat.format(resources.stream().mapToInt(ItemStack::getAmount).sum()) + " " +
                                "§bresources stored in the machine"),
                new InventoryItem(45, Material.REPEATER, 1, "Statistics",
                        "§bTotal resources gained: §e" + decimalFormat.format(machine.getTotalResourcesGained()),
                        "§bTotal Zen Coins gained: §e" + decimalFormat.format(machine.getTotalZenCoinsGained())),
                new InventoryNavigationItem("fuels", inventory, 48, Material.OBSIDIAN, 1, "Fuel", "§9Fuel up your machine.",
                        decimalFormat.format(machine.getFuels().stream().mapToInt(PlayerFuel::getEnergy).sum()) + "/" + decimalFormat.format(machine.getMaxFuel())),
                new InventoryNavigationItem("upgrades", inventory, 50, Material.HOPPER, 1, "Upgrades", "§9Upgrade your machine"),
                new InventoryItem(53, Material.BARRIER, 64, "DECONSTRUCT MACHINE", "§cDeconstruct your machine to block form.").setOnClick(() -> {
                    player.getInventory().addItem(machine.deconstruct().getItemStackWithAppliedPlaceholders());
                })
        ), machine));


        List<PlayerFuel> fuels = machine.getFuels();
        routes.put("resources", new InventoryPage(9, "Resources", null,
                IntStream.range(0, resources.size()).mapToObj(i -> {
                    ItemStack resource = resources.get(i);
                    ItemStack itemStack = resource.clone();
                    itemStack.setAmount(1);
                    ItemMeta meta = itemStack.getItemMeta();
                    meta.setLore(Arrays.asList("§d§lThere are currently", "§e§l" + decimalFormat.format(resource.getAmount()) + " §d§lof this item stored."));
                    itemStack.setItemMeta(meta);
                    return new InventoryItem(i, itemStack).setOnClick(() -> machinery.createSignInput(player, (p, lines) -> {
                        String resourceName = StringUtils.capitalize(itemStack.getType().toString().toLowerCase().replaceAll("_", " "));
                        p.sendMessage("§e§lYou are taking §b§l" + resourceName + " §e§lfrom a machine. Please enter an amount.");
                        try {
                            int num = Integer.parseInt(lines[0].replace("§9", "").replaceAll("\\D", ""));
                            if (num > resource.getAmount()) {
                                p.sendMessage("§c§lYou don't have that amount of resources of type " + resourceName + " in the machine.");
                                return false;
                            }
                            resource.setAmount(resource.getAmount() - num);
                            ItemStack givenItemStack = resource.clone();
                            givenItemStack.setAmount(num);
                            p.getInventory().addItem(givenItemStack);
                        } catch (NumberFormatException e) {
                            p.sendMessage("§c§lInvalid number!");
                            return false;
                        }
                        return true;
                    }, "§e§lAmount: §9", "", "", ""));
                }).collect(Collectors.toSet()), machine));
        routes.put("fuels", new InventoryPage(9, "Fuels", null,
                IntStream.range(0, fuels.size()).mapToObj(i -> {
                    PlayerFuel fuel = fuels.get(i);
                    return new InventoryItem(i, fuel.getItem(fuel.getAmount()));
                }).collect(Collectors.toSet()), machine).setCancelClick(false));
        routes.put("bank", new InventoryPage(9, "Withdrawal", fillment, Sets.newHashSet(
                new InventoryItem(4, Material.MAGMA_CREAM, 1, "§6" + decimalFormat.format(machine.getZenCoinsGained()) + " Zen Coins")
        ), machine));
        routes.put("deconstructConfirm", new InventoryPage(9, "Confirm Deconstruction", fillment, Sets.newHashSet(
                new InventoryItem(2, Material.RED_STAINED_GLASS_PANE, 1, "§4§lCANCEL"),
                new InventoryItem(6, Material.GREEN_STAINED_GLASS_PANE, 1, "§a§lAGREE", "§eThis will deconstruct your machine", "§eYou will be given your machine in " +
                        "the " +
                        "form of a block").setOnClick(() -> {
                    player.closeInventory();
                    MachineBlock<PlayerMachine> machineBlock = machine.deconstruct();
                    player.getInventory().addItem(machineBlock.getItemStackWithAppliedPlaceholders());
                })
        ), machine));
        Set<InventoryItem> upgradeItems = new HashSet<>();
        int upgradeNumber = 0;
        for (AbstractUpgrade upgrade : machine.getUpgrades()) {
            for (int i = 0; i < upgrade.getLevel(); i++) {
                upgradeItems.add(new InventoryItem(upgradeNumber * 9, Material.LIME_STAINED_GLASS_PANE, i + 1, ChatColor.DARK_AQUA + upgrade.getUpgradeName()));
            }
            ++upgradeNumber;
        }
        routes.put("upgrades", new InventoryPage(machine.getUpgrades().size() * 9, "Upgrades", upgradeFillment, upgradeItems, machine));
        return inventory;
    }
}
