package me.oriharel.machinery.inventory;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.fuel.FuelManager;
import me.oriharel.machinery.fuel.PlayerFuel;
import me.oriharel.machinery.items.MachineBlock;
import me.oriharel.machinery.machine.PlayerMachine;
import me.oriharel.machinery.upgrades.AbstractUpgrade;
import me.oriharel.machinery.utilities.NMS;
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
        routes.put("upgradeVerification", craftUpgradeVerificationPage());
    }

    private InventoryPage craftMainMenuPage(List<ItemStack> resources) {
        System.out.println(machine.getFuels());
        return new InventoryPage(54, machine.getType().toTitle(), defaultFillment, Sets.newHashSet(

                new InventoryItem(
                        20,
                        Material.GHAST_TEAR,
                        1,
                        "Bank",
                        "§9Click to open the withdrawal menu",
                        "§bThere are currently §e" + decimalFormat.format(machine.getZenCoinsGained()) + " §bZen Coins stored in the machine", "§aClick to withdraw")
                        .setOnClick(() -> machinery.createSignInput(p, this::handleZenCoinWithdrawalSign, "§e§lAmount: §9", "§dWITHDRAW", "", "")),

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
                            "§e§lAmount: §9", "§dWITHDRAW", "", ""));
                }).collect(Collectors.toSet()), machine);
    }

    private InventoryPage craftFuelsPage() {


        return new InventoryPage(9, "Fuels", defaultFillment, Sets.newHashSet(
                new InventoryItem(2, Material.PAPER, 1, "§aWITHDRAW FUEL", "§bEnter the amount of energy to withdraw")
                        .setOnClick(() -> machinery.createSignInput(p, this::handleFuelWithdrawSign, "§e§lAmount: §9", "§dWITHDRAW", "", "")),
                new InventoryItem(6, Material.BEACON, 1, "§aDEPOSIT FUEL", "§bEnter the amount of energy to deposit")
                        .setOnClick(() -> machinery.createSignInput(p, this::handleFuelDepositSign, "§e§lAmount: §9", "§dDEPOSIT", "", ""))
        ), machine);
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
        List<AbstractUpgrade> upgrades = machine.getUpgrades();
        Set<InventoryItem> upgradeItems = new HashSet<>();
        InventoryItem upgradeFillment = new InventoryFillmentItem(Material.RED_STAINED_GLASS_PANE, 1, ChatColor.RED + "BLOCKED", "§cPurchase previous upgrade(s).");

        for (int upgradeNumber = 0; upgradeNumber < upgrades.size(); upgradeNumber++) {
            AbstractUpgrade upgrade = upgrades.get(upgradeNumber);

            for (int upgradeLevel = 1; upgradeLevel < upgrade.getLevel(); upgradeLevel++) {
                ItemStack upgradeItem = new ItemStack(Material.LIME_STAINED_GLASS_PANE, upgradeLevel);
                ItemMeta meta = upgradeItem.getItemMeta();
                meta.setDisplayName(ChatColor.DARK_AQUA + upgrade.getUpgradeName() + " " + ChatColor.GREEN + "(" + upgrade.getLevel() + ")");
                meta.setLore(Collections.singletonList("§aPurchased"));
                upgradeItem.setItemMeta(meta);
                upgradeItems.add(new InventoryItem((upgradeNumber * 9) + upgradeLevel - 1, upgradeItem));
            }
            ItemStack nextUpgradeItem = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, upgrade.getLevel() + 1);
            ItemMeta meta = nextUpgradeItem.getItemMeta();
            meta.setDisplayName(ChatColor.DARK_AQUA + upgrade.getUpgradeName() + " " + ChatColor.GREEN + "(" + upgrade.getLevel() + ")");
            meta.setLore(Collections.singletonList("§aClick to upgrade!"));
            nextUpgradeItem.setItemMeta(meta);
            upgradeItems.add(new InventoryNavigationItemData<>("upgradeVerification", parentInventory, upgrade.getLevel(), nextUpgradeItem, upgrade));
            ++upgradeNumber;
        }

        return new InventoryPage(machine.getUpgrades().size() * 9, "Upgrades", upgradeFillment, upgradeItems, machine);
    }

    private InventoryPage craftUpgradeVerificationPage() {
        DatableInventoryPage<AbstractUpgrade> inventoryPage = new DatableInventoryPage<>(
                9,
                "Upgrade!",
                defaultFillment,
                null,
                machine
        );
        AbstractUpgrade upgrade = inventoryPage.getStoredData();
        int upgradeCost = upgrade.getCosts().get(upgrade.getLevel() + 1);
        int downgradeCost = upgrade.getCosts().get(upgrade.getLevel() - 1);
        inventoryPage.setInventoryItems(Sets.newHashSet(
                new InventoryItem(2, Material.RED_STAINED_GLASS_PANE, 1, "§9DOWNGRADE", "§bDowngrade to level §e" + (upgrade.getLevel() - 1),
                        "§bCost: §e" + decimalFormat.format(downgradeCost) + " Zen Coins").setOnClick(() -> {
                    depositZenCoins(downgradeCost);
                    upgrade.downgrade();
                    p.closeInventory();
                    machinery.getMachineManager().setPlayerMachineBlock(machine.getMachineCore().getBlock(), machine);
                }),
                new InventoryItem(4, Material.RED_STAINED_GLASS_PANE, 1, "§cCANCEL").setOnClick(() -> p.closeInventory()),
                new InventoryItem(6, Material.LIME_STAINED_GLASS_PANE, 1, "§6UPGRADE", "§bUpgrade to level §e" + (upgrade.getLevel() + 1),
                        "§bCost: §e" + decimalFormat.format(upgradeCost) + " Zen Coins").setOnClick(() -> {
                    withdrawZenCoins(upgradeCost);
                    upgrade.upgrade();
                    p.closeInventory();
                    machinery.getMachineManager().setPlayerMachineBlock(machine.getMachineCore().getBlock(), machine);
                })
        ));
        return inventoryPage;
    }

    private boolean handleFuelWithdrawSign(Player p, String[] lines) {
        List<PlayerFuel> fuels = machine.getFuels();
        int energyInMachine = fuels.stream().mapToInt(PlayerFuel::getEnergy).sum();
        int num = validateAndGetNumber(lines, energyInMachine, "§cYou cannot withdraw more than §e" + decimalFormat.format(energyInMachine) + " §csince it will " +
                "§csurpass the energy in the machine.");

        if (num == -1) return false;

        List<PlayerFuel> fuelsInMachine =
                fuels.stream().sorted(Comparator.comparingInt(PlayerFuel::getEnergy).reversed()).collect(Collectors.toList());

        int energyLeftToWithdraw = num;
        for (PlayerFuel playerFuel : fuelsInMachine) {
            int fuelEnergy = playerFuel.getEnergy();
            int maxEnergyCanWithdrawFromFuel = Math.min(fuelEnergy, energyLeftToWithdraw);
            int fuelsToWithdraw = maxEnergyCanWithdrawFromFuel / playerFuel.getAmount();

            PlayerFuel cloneForPlayer = handlePlayerFuelIntake(playerFuel, fuelsToWithdraw, maxEnergyCanWithdrawFromFuel);

            Utils.giveItemOrDrop(p, cloneForPlayer, false);
            energyLeftToWithdraw -= maxEnergyCanWithdrawFromFuel;
            if (energyLeftToWithdraw == 0) break;
        }
        machinery.updateMachineBlock(machine);
        return true;
    }

    private boolean handleFuelDepositSign(Player p, String[] lines) {
        List<PlayerFuel> fuels = machine.getFuels();
        int energyInMachine = fuels.stream().mapToInt(PlayerFuel::getEnergy).sum();
        int maxDeposit = machine.getMaxFuel() - energyInMachine;
        int num = validateAndGetNumber(lines, maxDeposit, "§cYou cannot deposit more than §e" + decimalFormat.format(maxDeposit) + " §csince it will go" +
                " over the max fuel limitation of this machine");

        if (num == -1) return false;

        org.bukkit.inventory.Inventory inventory = p.getInventory();

        List<Pair<Integer, PlayerFuel>> fuelsOnPlayer =
                getPlayerFuelsInInventory(p.getInventory()).stream().sorted(Comparator.comparingInt((Pair<Integer, PlayerFuel> pair) -> pair.getSecond().getEnergy())
                        .reversed()).collect(Collectors.toList());

        int energyLeftToDeposit = num;
        for (Pair<Integer, PlayerFuel> playerFuelPair : fuelsOnPlayer) {
            PlayerFuel playerFuel = playerFuelPair.getSecond();
            int index = playerFuelPair.getFirst();
            int fuelEnergy = playerFuel.getEnergy();
            int maxEnergyCanDespositFromFuel = Math.min(fuelEnergy, energyLeftToDeposit);
            int fuelsToDeposit = maxEnergyCanDespositFromFuel / playerFuel.getAmount();

            PlayerFuel cloneForMachine = handlePlayerFuelIntake(playerFuel, fuelsToDeposit, maxEnergyCanDespositFromFuel);

            inventory.setItem(index, playerFuel);
            fuels.add(cloneForMachine);
            energyLeftToDeposit -= maxEnergyCanDespositFromFuel;
            if (energyLeftToDeposit == 0) break;
        }
        machinery.updateMachineBlock(machine);
        return true;
    }

    /**
     * Handles the intake of fuel. Mutates original fuel to disperse it into clone
     * @param original original fuel to mutate and disperse according to calculations
     * @param fuelsToTake used to know how much to subtract from original and how much to add to clone
     * @param maxEnergyCanTakeFromFuel used to know how much energy to subtract from original and place into clone
     * @return clone of fuel
     */
    public PlayerFuel handlePlayerFuelIntake(PlayerFuel original, int fuelsToTake, int maxEnergyCanTakeFromFuel) {
        int originalFuelEnergyAmount = original.getEnergy();
        PlayerFuel clone = original.clone();

        clone.setAmount(fuelsToTake);
        clone.setEnergySloppy(maxEnergyCanTakeFromFuel);

        original.setAmount(original.getAmount() - fuelsToTake);
        original.setEnergySloppy(originalFuelEnergyAmount - maxEnergyCanTakeFromFuel);

        return clone;
    }

    private boolean handleZenCoinWithdrawalSign(Player p, String[] lines) {
        p.sendMessage("§e§lYou opened the withdrawal menu of a machine. Please enter an amount to withdraw.");
        try {
            int num = validateAndGetNumber(lines, (int) machine.getZenCoinsGained(), "§c§lYou don't have that amount of Zen Coins deposited in your machine.");
            if (num == -1) return false;

            withdrawZenCoins(num);
            machinery.updateMachineBlock(machine);
        } catch (NumberFormatException e) {
            p.sendMessage("§c§lInvalid number!");
            return false;
        }
        return true;
    }

    private boolean handleResourceWithdrawalSign(Player p, String[] lines, ItemStack resource) {
        String resourceName = StringUtils.capitalize(resource.getType().toString().toLowerCase().replaceAll("_", " "));
        p.sendMessage("§e§lYou are taking §b§l" + resourceName + " §e§lfrom a machine. Please enter an amount.");
        int num = validateAndGetNumber(lines, resource.getAmount(), "§c§lYou don't have that amount of resources of type " + resourceName + " in the machine.");
        if (num == -1) return false;

        resource.setAmount(resource.getAmount() - num);
        ItemStack givenItemStack = resource.clone();
        givenItemStack.setAmount(num);
        if (!Utils.inventoryHasSpaceForItemAdd(p.getInventory())) {
            p.sendMessage("§c§lYou don't have enough inventory space to store " + decimalFormat.format(num) + " " + resourceName + ".");
            return false;
        }

        p.getInventory().addItem(givenItemStack);
        machinery.updateMachineBlock(machine);
        return true;
    }

    private int validateAndGetNumber(String[] lines, int maxCanTakeAmount, String aboveMaxErrorMessage) {
        try {
            String[] split = lines[0].split("§9");
            if (split.length == 1) throw new NumberFormatException();
            int num = Integer.parseInt(split[1]);
            if (num <= 0) {
                p.sendMessage("§c§lYou must enter a number above zero!");
                return -1;
            }
            if (num > maxCanTakeAmount) {
                p.sendMessage(aboveMaxErrorMessage);
                return -1;
            }
            return num;
        } catch (NumberFormatException e) {
            p.sendMessage("§c§lInvalid number!");
            return -1;
        }
    }

    private List<Pair<Integer, PlayerFuel>> getPlayerFuelsInInventory(org.bukkit.inventory.Inventory inventory) {
        ItemStack[] contents = inventory.getContents();
        List<Pair<Integer, PlayerFuel>> fuels = new ArrayList<>();
        FuelManager fuelManager = machinery.getFuelManager();

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            String nbt = getFuelNBTId(item);
            if (nbt == null) continue;
            fuels.add(Pair.of(i, fuelManager.getPlayerFuelItemByNbtId(nbt, item.getAmount())));
        }

        return fuels;
    }

    private String getFuelNBTId(ItemStack item) {
        for (String key : NMS.getItemStackNBTTMapClone(item).keySet()) {
            if (machinery.getFuelManager().getNbtIds().containsKey(key)) {
                return key;
            }
        }
        return null;
    }

    private void depositZenCoins(int amount) {
        Profile profile = getMobCoinsProfile();
        machine.addZenCoinsGained(amount);
        profile.setMobCoins(profile.getMobCoins() + amount);
    }

    private void withdrawZenCoins(int amount) {
        depositZenCoins(-amount);
    }

    public Profile getMobCoinsProfile() {
        return MobCoinsAPI.getProfileManager().getProfile(this.p);
    }
}
