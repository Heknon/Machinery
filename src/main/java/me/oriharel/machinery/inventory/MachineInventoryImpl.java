package me.oriharel.machinery.inventory;

import com.google.common.collect.Sets;
import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.fuel.PlayerFuel;
import me.oriharel.machinery.items.MachineBlock;
import me.oriharel.machinery.machine.PlayerMachine;
import me.oriharel.machinery.upgrades.AbstractUpgrade;
import me.oriharel.machinery.utilities.Utils;
import me.swanis.mobcoins.MobCoinsAPI;
import me.swanis.mobcoins.profile.Profile;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MachineInventoryImpl {

    private DecimalFormat decimalFormat;
    private Player p;
    private PlayerMachine machine;
    private Machinery machinery;
    private InventoryItem defaultFillment;
    private Inventory parentInventory;
    private HashMap<String, InventoryPage> routes;

    public MachineInventoryImpl(PlayerMachine machine, Player p, Machinery machinery) {
        this.p = p;
        this.machine = machine;
        this.machinery = machinery;
        this.defaultFillment = new InventoryFillmentItem(Material.GRAY_STAINED_GLASS_PANE, 1, "");
        this.decimalFormat = new DecimalFormat("#,###");
        this.routes = new HashMap<>();
        this.parentInventory = new Inventory(routes, p);
        craftInventory();
    }

    public void openInventory() {
        p.openInventory(parentInventory.getInventory());
    }



    private void craftInventory() {

        // filter list to remove 0 amounts
        List<ItemStack> resources = new ArrayList<>(machine.getResourcesGained().values()).stream().filter(is -> is.getAmount() != 0).collect(Collectors.toList());

        routes.put("start", craftMainMenuPage(resources));
        routes.put("resources", craftResourcesPage(resources));
        routes.put("fuels", craftFuelsPage());
        routes.put("deconstructConfirm", craftDeconstructConfirmationPage());
        routes.put("upgrades", craftUpgradesPage());
    }

    private InventoryPage craftMainMenuPage(List<ItemStack> resources) {
        return new InventoryPage(54, machine.getType().toTitle(), defaultFillment, Sets.newHashSet(

                new InventoryItem(
                        20,
                        Material.GHAST_TEAR,
                        1,
                        "Bank",
                        "§9Click to open the withdrawal menu",
                        "§bThere are currently §e" + decimalFormat.format(machine.getZenCoinsGained()) + " §bZen Coins stored in the machine", "§aClick to withdraw")
                        .setOnClick(() -> machinery.createSignInput(p, this::handleZenCoinWithdrawalSign, "§e§lAmount: §9", "", "", "")),

                new InventoryNavigationItem(
                        "resources",
                        parentInventory,
                        24,
                        Material.PAPER,
                        1,
                        "Storage",
                        "§9Click to open open the storage of the machine",
                        "§bThere are currently §e" + decimalFormat.format(resources.stream().mapToInt(ItemStack::getAmount).sum()) + " §bresources stored in the " +
                                "machine"),

                new InventoryItem(
                        45,
                        Material.REPEATER,
                        1,
                        "Statistics",
                        "§bTotal resources gained: §e" + decimalFormat.format(machine.getTotalResourcesGained()),
                        "§bTotal Zen Coins gained: §e" + decimalFormat.format(machine.getTotalZenCoinsGained())),

                new InventoryNavigationItem(
                        "fuels",
                        parentInventory,
                        48,
                        Material.OBSIDIAN,
                        1,
                        "Fuel",
                        "§9Fuel up your machine.",
                        decimalFormat.format(machine.getFuels().stream().mapToInt(PlayerFuel::getEnergy).sum()) + "/" + decimalFormat.format(machine.getMaxFuel())),

                new InventoryNavigationItem(
                        "upgrades",
                        parentInventory,
                        50,
                        Material.HOPPER,
                        1,
                        "Upgrades",
                        "§9Upgrade your machine"),

                new InventoryItem(
                        53,
                        Material.BARRIER,
                        64,
                        "DECONSTRUCT MACHINE",
                        "§cDeconstruct your machine to block form.")
                        .setOnClick(() -> {
                            p.getInventory().addItem(machine.deconstruct().getItemStackWithAppliedPlaceholders());
                            p.closeInventory();
                        })
        ), machine);
    }

    private InventoryPage craftResourcesPage(List<ItemStack> resources) {
        int closestMultipleTo9 = Math.max((int) (Math.ceil(resources.size() / 9.0) * 9), 9);

        return new InventoryPage(Math.min(closestMultipleTo9, 54), "Resources", null,
                IntStream.range(0, resources.size()).mapToObj(i -> {
                    ItemStack resource = resources.get(i);
                    ItemStack itemStack = resource.clone();

                    itemStack.setAmount(1);
                    ItemMeta meta = itemStack.getItemMeta();
                    meta.setLore(Arrays.asList("§d§lThere are currently", "§e§l" + decimalFormat.format(resource.getAmount()) + " §d§lof this item stored."));
                    itemStack.setItemMeta(meta);

                    return new InventoryItem(i, itemStack).setOnClick(() -> machinery.createSignInput(p, (p, lines) -> handleResourceWithdrawalSign(p, lines, resource),
                            "§e§lAmount: §9", "", "", ""));
                }).collect(Collectors.toSet()), machine);
    }

    private InventoryPage craftFuelsPage() {
        List<PlayerFuel> fuels = machine.getFuels();

        return new InventoryPage(9, "Fuels", null,
                IntStream.range(0, fuels.size()).mapToObj(i -> {
                    PlayerFuel fuel = fuels.get(i);
                    return new InventoryItem(i, fuel);
                }).collect(Collectors.toSet()), machine).setCancelClick(false);
    }

    private InventoryPage craftDeconstructConfirmationPage() {
        return new InventoryPage(9, "Confirm Deconstruction", defaultFillment, Sets.newHashSet(
                new InventoryItem(2, Material.RED_STAINED_GLASS_PANE, 1, "§4§lCANCEL"),
                new InventoryItem(6, Material.GREEN_STAINED_GLASS_PANE, 1, "§a§lAGREE", "§eThis will deconstruct your machine", "§eYou will be given your machine in " +
                        "the form of a block").setOnClick(() -> {
                    p.closeInventory();
                    MachineBlock machineBlock = machine.deconstruct();
                    p.getInventory().addItem(machineBlock.getItemStackWithAppliedPlaceholders());
                })
        ), machine);
    }

    private InventoryPage craftUpgradesPage() {
        Set<InventoryItem> upgradeItems = new HashSet<>();
        InventoryItem upgradeFillment = new InventoryFillmentItem(Material.RED_STAINED_GLASS_PANE, 1, ChatColor.RED + "BLOCKED", "§cPurchase previous upgrade(s).");
        int upgradeNumber = 0;

        for (AbstractUpgrade upgrade : machine.getUpgrades()) {
            for (int i = 0; i < upgrade.getLevel() + 1; i++) {
                ItemStack upgradeItem = new ItemStack(i == upgrade.getLevel() ? Material.GREEN_STAINED_GLASS_PANE : Material.LIME_STAINED_GLASS_PANE, i);
                ItemMeta meta = upgradeItem.getItemMeta();
                meta.setDisplayName(ChatColor.DARK_AQUA + upgrade.getUpgradeName() + " " + ChatColor.GREEN + "(" + upgrade.getLevel() + ")");
                if (i == upgrade.getLevel()) {
                    meta.setLore(Collections.singletonList("§aClick to upgrade!"));
                } else {
                    meta.setLore(Collections.singletonList("§aPurchased"));
                }
                upgradeItem.setItemMeta(meta);
                upgradeItems.add(new InventoryItem(upgradeNumber * 9, upgradeItem));
            }
            ++upgradeNumber;
        }
        return new InventoryPage(machine.getUpgrades().size() * 9, "Upgrades", upgradeFillment, upgradeItems, machine);
    }

    private boolean handleZenCoinWithdrawalSign(Player p, String[] lines) {
        p.sendMessage("§e§lYou opened the withdrawal menu of a machine. Please enter an amount to withdraw.");
        try {
            String[] split = lines[0].split("§9");
            if (split.length == 1) throw new NumberFormatException();
            int num = Integer.parseInt(split[1]);
            if (num > machine.getZenCoinsGained()) {
                p.sendMessage("§c§lYou don't have that amount of Zen Coins deposited in your machine.");
                return false;
            }
            machine.removeZenCoinsGained(num);
            Profile profile = MobCoinsAPI.getProfileManager().getProfile(this.p);
            profile.setMobCoins(profile.getMobCoins() + num);
        } catch (NumberFormatException e) {
            p.sendMessage("§c§lInvalid number!");
            return false;
        }
        return true;
    }

    private boolean handleResourceWithdrawalSign(Player p, String[] lines, ItemStack resource) {
        String resourceName = StringUtils.capitalize(resource.getType().toString().toLowerCase().replaceAll("_", " "));
        p.sendMessage("§e§lYou are taking §b§l" + resourceName + " §e§lfrom a machine. Please enter an amount.");
        try {
            String[] split = lines[0].split("§9");
            if (split.length == 1) throw new NumberFormatException();
            int num = Integer.parseInt(split[1]);
            if (num > resource.getAmount()) {
                p.sendMessage("§c§lYou don't have that amount of resources of type " + resourceName + " in the machine.");
                return false;
            }
            resource.setAmount(resource.getAmount() - num);
            ItemStack givenItemStack = resource.clone();
            givenItemStack.setAmount(num);
            if (!Utils.inventoryHasSpaceForItemAdd(p.getInventory(), givenItemStack)) {
                p.sendMessage("§c§lYou don't have enough inventory space to store " + decimalFormat.format(num) + " " + resourceName + ".");
                return false;
            }
            p.getInventory().addItem(givenItemStack);
        } catch (NumberFormatException e) {
            p.sendMessage("§c§lInvalid number!");
            return false;
        }
        return true;
    }
}
