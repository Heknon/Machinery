package me.oriharel.machinery.inventory;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.fuel.Fuel;
import me.oriharel.machinery.fuel.FuelManager;
import me.oriharel.machinery.items.MachineItem;
import me.oriharel.machinery.machine.PlayerMachine;
import me.oriharel.machinery.message.Message;
import me.oriharel.machinery.message.Placeholder;
import me.oriharel.machinery.upgrades.AbstractUpgrade;
import me.oriharel.machinery.utilities.Utils;
import me.swanis.mobcoins.MobCoinsAPI;
import me.swanis.mobcoins.profile.Profile;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
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
                        decimalFormat.format(machine.getEnergyInMachine()) + "/" + decimalFormat.format(machine.getMaxFuel())),

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
                    MachineItem machineItem = machine.deconstruct();
                    p.getInventory().addItem(machineItem.getItemStackWithAppliedPlaceholders());
                })
        ), machine);
    }

    private InventoryPage craftUpgradesPage() {
        List<AbstractUpgrade> upgrades = machine.getUpgrades();
        Set<InventoryItem> upgradeItems = new HashSet<>();
        InventoryItem upgradeFillment = new InventoryFillmentItem(Material.RED_STAINED_GLASS_PANE, 1, ChatColor.RED + "BLOCKED", "§cPurchase previous upgrade(s).");

        for (int upgradeNumber = 0; upgradeNumber < upgrades.size(); upgradeNumber++) {
            AbstractUpgrade upgrade = upgrades.get(upgradeNumber);

            for (int upgradeLevel = 1; upgradeLevel <= upgrade.getLevel(); upgradeLevel++) {
                ItemStack upgradeItem = new ItemStack(Material.LIME_STAINED_GLASS_PANE, upgradeLevel);
                ItemMeta meta = upgradeItem.getItemMeta();
                meta.setDisplayName(ChatColor.DARK_AQUA + upgrade.getUpgradeName() + " " + ChatColor.GREEN + "(" + upgrade.getLevel() + ")");
                meta.setLore(Collections.singletonList("§aPurchased"));
                upgradeItem.setItemMeta(meta);
                upgradeItems.add(new InventoryItem((upgradeNumber * 9) + (upgradeLevel - 1), upgradeItem));
            }

            ItemStack nextUpgradeItem = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, upgrade.getLevel() + 1);
            ItemMeta meta = nextUpgradeItem.getItemMeta();
            meta.setDisplayName(ChatColor.DARK_AQUA + upgrade.getUpgradeName() + " " + ChatColor.GREEN + "(" + (upgrade.getLevel() + 1) + ")");
            meta.setLore(Collections.singletonList("§aClick to upgrade!"));
            nextUpgradeItem.setItemMeta(meta);
            upgradeItems.add(new InventoryNavigationItemData<>(craftUpgradeVerificationPage(), parentInventory, (upgradeNumber * 9) + upgrade.getLevel(), nextUpgradeItem,
                    upgrade));
        }

        return new InventoryPage(machine.getUpgrades().size() * 9, "Upgrades", upgradeFillment, upgradeItems, machine);
    }

    private NavigableDataInventoryPage<AbstractUpgrade> craftUpgradeVerificationPage() {
        return new NavigableDataInventoryPage<>(
                9,
                "Upgrade!",
                defaultFillment,
                null,
                machine,
                (upgrade, navigableDataPage) -> {
                    NavigableDataInventoryPage<AbstractUpgrade> inventoryPage = (NavigableDataInventoryPage<AbstractUpgrade>) navigableDataPage;
                    Map<Integer, Integer> costs = upgrade.getCosts();
                    int upgradeCost = costs.get(upgrade.getLevel() + 1);
                    int downgradeCost = upgrade.getCosts().getOrDefault(upgrade.getLevel() - 1, -1);
                    inventoryPage.setInventoryItems(Sets.newHashSet(
                            new InventoryItem(2, Material.LIME_STAINED_GLASS_PANE, 1, "§6DOWNGRADE",
                                    downgradeCost == -1 ? "§bCANNOT DOWNGRADE" : "§bDowngrade to level §e" + (upgrade.getLevel() - 1),
                                    downgradeCost == -1 ? "" : "§bCost: §e" + decimalFormat.format(downgradeCost) + " Zen Coins").setOnClick(() -> {
                                if (downgradeCost == -1) {
                                    new Message("messages.yml", "cannot_downgrade", p, Utils.getLocationPlaceholders(machine.getMachineCore(),
                                            Utils.getMachinePlaceholders(machine))).send();
                                    return;
                                }
                                depositZenCoinsIntoProfile(downgradeCost);
                                upgrade.downgrade();
                                machine.getMinerProcess().applyUpgradeModifiers();
                                p.closeInventory();
                                new Message("messages.yml", "on_downgrade", p, Utils.getLocationPlaceholders(machine.getMachineCore(),
                                        Utils.getMachinePlaceholders(machine, new Placeholder("%amount%", downgradeCost)))).send();
                                machinery.getMachineManager().setPlayerMachineBlock(machine.getMachineCore().getBlock(), machine);
                            }),
                            new InventoryItem(4, Material.RED_STAINED_GLASS_PANE, 1, "§cCANCEL").setOnClick(() -> p.closeInventory()),
                            new InventoryItem(6, Material.LIME_STAINED_GLASS_PANE, 1, "§6UPGRADE", "§bUpgrade to level §e" + (upgrade.getLevel() + 1),
                                    "§bCost: §e" + decimalFormat.format(upgradeCost) + " Zen Coins").setOnClick(() -> {
                                int mobCoinsOfUser = getMobCoinsProfile().getMobCoins();
                                if (upgrade.getLevel() == upgrade.getMaxLevel()) {
                                    new Message("messages.yml", "reached_max_upgrade_level", p, Utils.getLocationPlaceholders(machine.getMachineCore(),
                                            Utils.getMachinePlaceholders(machine))).send();
                                    return;
                                } else if (upgradeCost > mobCoinsOfUser) {
                                    new Message("messages.yml", "insufficient_funds_for_upgrade", p, Utils.getLocationPlaceholders(machine.getMachineCore(),
                                            Utils.getMachinePlaceholders(machine, Utils.getAmountThingPlaceholder(upgradeCost - mobCoinsOfUser, "Zen Coins")))).send();
                                    return;
                                }
                                withdrawZenCoinsFromProfile(upgradeCost);
                                upgrade.upgrade();
                                machine.getMinerProcess().applyUpgradeModifiers();
                                p.closeInventory();
                                new Message("messages.yml", "on_upgrade", p, Utils.getLocationPlaceholders(machine.getMachineCore(),
                                        Utils.getMachinePlaceholders(machine, new Placeholder("%amount%", upgradeCost)))).send();
                                machinery.updateMachineBlock(machine, true);
                            })
                    ));
                });
    }

    private boolean handleFuelWithdrawSign(Player p, String[] lines) {
        int energyInMachine = machine.getEnergyInMachine();
        int amountToWithdraw = validateAndGetNumber(lines, energyInMachine, "Fuel", new Message("messages.yml", "sign_input.entered_above_max_fuel_withdraw_limit", p,
                Utils.getLocationPlaceholders(machine.getMachineCore(),
                        Utils.getMachinePlaceholders(machine, new Placeholder("%amount%", energyInMachine)))), false);

        if (amountToWithdraw == -1) return false;
        if (amountToWithdraw == 0) {
            getPlayerFuelsInInventory(p.getInventory()).forEach(f -> {
                if (f.getSecond().getEnergy() != 0) return;
                f.getSecond().setAmount(0);
                p.getInventory().setItem(f.getFirst(), f.getSecond());
            });
            return true;
        }

        machine.removeEnergy(amountToWithdraw);
        Utils.giveItemOrDrop(p, machinery.getFuelManager().getFuel(1, amountToWithdraw), false);

        machinery.updateMachineBlock(machine, true);
        return true;
    }

    private boolean handleFuelDepositSign(Player p, String[] lines) {
        int energyInMachine = machine.getEnergyInMachine();
        int maxDeposit = machine.getMaxFuel() - energyInMachine;
        int amountToDeposit = validateAndGetNumber(lines, maxDeposit, "Fuel", new Message("messages.yml", "sign_input.entered_above_max_fuel_deposit_limit", p,
                Utils.getLocationPlaceholders(machine.getMachineCore(),
                        Utils.getMachinePlaceholders(machine, new Placeholder("%amount%", maxDeposit)))), false);

        if (amountToDeposit == -1) return false;
        if (amountToDeposit == 0) {
            getPlayerFuelsInInventory(p.getInventory()).forEach(f -> {
                if (f.getSecond().getEnergy() != 0) return;
                f.getSecond().setAmount(0);
                p.getInventory().setItem(f.getFirst(), f.getSecond());
            });
            return true;
        }

        org.bukkit.inventory.Inventory inventory = p.getInventory();

        List<Pair<Integer, Fuel>> fuelsOnPlayer =
                getPlayerFuelsInInventory(p.getInventory()).stream().sorted(Comparator.comparingInt((Pair<Integer, Fuel> pair) -> pair.getSecond().getEnergy())
                        .reversed()).collect(Collectors.toList());

        int fuelEnergyInInventory = fuelsOnPlayer.stream().mapToInt(pair -> pair.getSecond().getEnergy()).sum();
        if (fuelEnergyInInventory < amountToDeposit) {
            new Message("messages.yml", "sign_input.not_enough_fuel_in_inventory", p, Utils.getLocationPlaceholders(machine.getMachineCore(),
                    Utils.getMachinePlaceholders(machine, Utils.getAmountThingPlaceholder(amountToDeposit, "Fuel Energy")))).send();
            return false;
        }

        int energyLeftToDeposit = amountToDeposit;
        for (Pair<Integer, Fuel> playerFuelPair : fuelsOnPlayer) {
            Fuel fuel = playerFuelPair.getSecond();
            int indexInInventory = playerFuelPair.getFirst();
            int fuelEnergy = fuel.getEnergy();
            int maxEnergyCanDespositFromFuel = Math.min(fuelEnergy, energyLeftToDeposit);
            int fuelsToDeposit = maxEnergyCanDespositFromFuel / fuel.getBaseEnergy();

            fuel.setAmount(fuel.getAmount() - fuelsToDeposit);
            fuel.setEnergy(fuelEnergy - maxEnergyCanDespositFromFuel);
            inventory.setItem(indexInInventory, fuel);
            machine.addEnergy(maxEnergyCanDespositFromFuel);
            energyLeftToDeposit -= maxEnergyCanDespositFromFuel;
            if (energyLeftToDeposit == 0) break;
        }
        if (machine.getEnergyInMachine() > 0 && !machine.getMinerProcess().isRunning()) machine.getMinerProcess().startProcess();
        machinery.updateMachineBlock(machine, true);
        return true;
    }

    private boolean handleZenCoinWithdrawalSign(Player p, String[] lines) {
        new Message("messages.yml", "sign_input.withdraw", p, Utils.getLocationPlaceholders(machine.getMachineCore(),
                Utils.getMachinePlaceholders(machine, new Placeholder("%thing%", "Zen Coins")))).send();
        try {
            int num = validateAndGetNumber(lines, (int) machine.getZenCoinsGained(), "Zen Coins",
                    new Message("messages.yml", "sign_input.not_enough_resources_in_machine", p, Utils.getLocationPlaceholders(machine.getMachineCore(),
                            Utils.getMachinePlaceholders(machine, Utils.getAmountThingPlaceholder(-1, "Zen Coins")))));
            if (num == -1) return false;
            else if (num == 0) return true;

            depositZenCoinsIntoProfile(num);
            machine.removeZenCoinsGained(num);
            machinery.updateMachineBlock(machine, true);
        } catch (NumberFormatException e) {
            p.sendMessage("§c§lInvalid number!");
            return false;
        }
        return true;
    }

    private boolean handleResourceWithdrawalSign(Player p, String[] lines, ItemStack resource) {
        String resourceName = StringUtils.capitalize(resource.getType().toString().toLowerCase().replaceAll("_", " "));
        new Message("messages.yml", "sign_input.withdraw", p, Utils.getLocationPlaceholders(machine.getMachineCore(),
                Utils.getMachinePlaceholders(machine, new Placeholder("%thing%", "Resources")))).send();
        int resourceAmountToWithdraw = validateAndGetNumber(
                lines,
                resource.getAmount(),
                resourceName,
                new Message("messages.yml", "sign_input.not_enough_resources_in_machine", p, Utils.getLocationPlaceholders(machine.getMachineCore(),
                        Utils.getMachinePlaceholders(machine, Utils.getAmountThingPlaceholder(-1, resourceName)))));
        if (resourceAmountToWithdraw == -1) return false;
        else if (resourceAmountToWithdraw == 0) return true;

        resource.setAmount(resource.getAmount() - resourceAmountToWithdraw);
        ItemStack givenItemStack = resource.clone();
        givenItemStack.setAmount(resourceAmountToWithdraw);
        if (!Utils.inventoryHasSpaceForItemAdd(p.getInventory())) {
            new Message("messages.yml", "not_enough_inventory_space", p, Utils.getLocationPlaceholders(machine.getMachineCore(), Utils.getMachinePlaceholders(machine,
                    Utils.getAmountThingPlaceholder(resourceAmountToWithdraw, resourceName)))).send();
            return false;
        }

        p.getInventory().addItem(givenItemStack);
        machinery.updateMachineBlock(machine, true);
        return true;
    }

    private int validateAndGetNumber(String[] lines, int maxCanTakeAmount, String name, Message aboveMaxErrorMessage) {
        return validateAndGetNumber(lines, maxCanTakeAmount, name, aboveMaxErrorMessage, true);
    }

    private int validateAndGetNumber(String[] lines, int maxCanTakeAmount, String name, Message aboveMaxErrorMessage, boolean mutatePlaceholder) {
        try {
            String[] split = lines[0].split("§9");
            if (split.length == 1) throw new NumberFormatException();
            int num = Integer.parseInt(split[1]);
            if (num < 0) {
                new Message("messages.yml", "sign_input.entered_number_below_zero", p, Utils.getLocationPlaceholders(machine.getMachineCore(),
                        Utils.getMachinePlaceholders(machine, Utils.getAmountThingPlaceholder(num, name)))).send();
                return -1;
            } else if (num == 0) {
                return 0;
            }
            if (num > maxCanTakeAmount) {
                if (mutatePlaceholder) aboveMaxErrorMessage.replacePlaceholder("%amount%", new Placeholder("%amount%", num)).send();
                else aboveMaxErrorMessage.send();
                return -1;
            }
            return num;
        } catch (NumberFormatException e) {
            new Message("messages.yml", "sign_input.invalid_number", p, Utils.getLocationPlaceholders(machine.getMachineCore(), Utils.getMachinePlaceholders(machine))).send();
            return -1;
        }
    }

    private List<Pair<Integer, Fuel>> getPlayerFuelsInInventory(org.bukkit.inventory.Inventory inventory) {
        ItemStack[] contents = inventory.getContents();
        List<Pair<Integer, Fuel>> fuels = new ArrayList<>();
        FuelManager fuelManager = machinery.getFuelManager();

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            NBTTagCompound compound = CraftItemStack.asNMSCopy(item).getTag();
            if (compound == null || !compound.hasKey("machine_fuel") || !compound.hasKey("fuel_energy")) continue;
            fuels.add(Pair.of(i, fuelManager.getFuel(item)));
        }

        return fuels;
    }

    private void depositZenCoinsIntoProfile(int amount) {
        Profile profile = getMobCoinsProfile();
        profile.setMobCoins(profile.getMobCoins() + amount);
    }

    private void withdrawZenCoinsFromProfile(int amount) {
        depositZenCoinsIntoProfile(-amount);
    }

    public Profile getMobCoinsProfile() {
        return MobCoinsAPI.getProfileManager().getProfile(this.p);
    }
}
