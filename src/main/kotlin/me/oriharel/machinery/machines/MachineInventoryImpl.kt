package me.oriharel.machinery.machines

import com.google.common.collect.Sets
import com.mojang.datafixers.util.Pair
import me.oriharel.machinery.Machinery
import me.oriharel.machinery.fuel.Fuel
import me.oriharel.machinery.inventory.*
import me.oriharel.machinery.machines.machine.PlayerMachine
import me.oriharel.machinery.message.Message
import me.oriharel.machinery.message.Placeholder
import me.oriharel.machinery.upgrades.AbstractUpgrade
import me.oriharel.machinery.utilities.Utils
import me.swanis.mobcoins.MobCoinsAPI
import me.swanis.mobcoins.profile.Profile
import org.apache.commons.lang.StringUtils
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.text.DecimalFormat
import java.util.*
import java.util.function.BiPredicate
import java.util.stream.Collectors
import java.util.stream.IntStream
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

class MachineInventoryImpl(private val machine: PlayerMachine, private val p: Player, private val machinery: Machinery) {
    private val decimalFormat: DecimalFormat
    private val defaultFillment: InventoryItem
    private val parentInventory: Inventory
    private val routes: HashMap<String?, InventoryPage>
    fun openInventory() {
        p.openInventory(parentInventory.inventory)
    }

    private fun craftInventory() {

        // filter list to remove 0 amounts
        val resources: List<ItemStack?> = machine.resourcesGained?.values?.filter { itemStack ->  itemStack?.amount != 0 }?.toList()!!
        routes["start"] = craftMainMenuPage(resources)
        routes["resources"] = craftResourcesPage(resources)
        routes["fuels"] = craftFuelsPage()
        routes["deconstructConfirm"] = craftDeconstructConfirmationPage()
        routes["upgrades"] = craftUpgradesPage()
        routes["upgradeVerification"] = craftUpgradeVerificationPage()
    }

    private fun craftMainMenuPage(resources: List<ItemStack?>): InventoryPage {
        return InventoryPage(54, machine.name?.replace('_', ' ')?.capitalize(), defaultFillment, Sets.newHashSet(
                InventoryItem(
                        20,
                        Material.GHAST_TEAR,
                        1,
                        "Bank",
                        "§9Click to open the withdrawal menu",
                        "§bThere are currently §e" + decimalFormat.format(machine.zenCoinsGained) + " §bZen Coins stored in the machine", "§aClick to withdraw")
                        .setOnClick { machinery.createSignInput(p, BiPredicate { p: Player, lines: Array<String> -> handleZenCoinWithdrawalSign(p, lines) }, "§e§lAmount: §9", "§dWITHDRAW", "", "") },
                InventoryNavigationItem(
                        "resources",
                        parentInventory,
                        24,
                        Material.PAPER,
                        1,
                        "Storage",
                        "§9Click to open open the storage of the machine",
                        "§bThere are currently §e" + decimalFormat.format(resources.stream().mapToInt { itemStack: ItemStack? -> itemStack!!.amount }.sum().toLong()) + " §bresources stored in the " +
                                "machine"),
                InventoryItem(
                        45,
                        Material.REPEATER,
                        1,
                        "Statistics",
                        "§bTotal resources gained: §e" + decimalFormat.format(machine.totalResourcesGained),
                        "§bTotal Zen Coins gained: §e" + decimalFormat.format(machine.totalZenCoinsGained)),
                InventoryNavigationItem(
                        "fuels",
                        parentInventory,
                        48,
                        Material.OBSIDIAN,
                        1,
                        "Fuel",
                        "§9Fuel up your machine.",
                        decimalFormat.format(machine.energyInMachine.toLong()) + "/" + decimalFormat.format(machine.maxFuel.toLong())),
                InventoryNavigationItem(
                        "upgrades",
                        parentInventory,
                        50,
                        Material.HOPPER,
                        1,
                        "Upgrades",
                        "§9Upgrade your machine"),
                InventoryItem(
                        53,
                        Material.BARRIER,
                        64,
                        "DECONSTRUCT MACHINE",
                        "§cDeconstruct your machine to block form.")
                        .setOnClick {
                            p.inventory.addItem(machine.deconstruct().itemStackWithAppliedPlaceholders)
                            p.closeInventory()
                        }
        ), machine)
    }

    private fun craftResourcesPage(resources: List<ItemStack?>): InventoryPage {
        val closestMultipleTo9 = max((ceil(resources.size / 9.0) * 9).toInt(), 9)
        return InventoryPage(min(closestMultipleTo9, 54), "Resources", null,
                IntStream.range(0, resources.size).mapToObj { i: Int ->
                    val resource = resources[i]
                    val itemStack = resource!!.clone()
                    itemStack.amount = 1
                    val meta = itemStack.itemMeta
                    meta!!.lore = Arrays.asList("§d§lThere are currently", "§e§l" + decimalFormat.format(resource.amount.toLong()) + " §d§lof this item stored.")
                    itemStack.itemMeta = meta
                    InventoryItem(i, itemStack).setOnClick {
                        machinery.createSignInput(p, BiPredicate { p: Player, lines: Array<String> -> handleResourceWithdrawalSign(p, lines, resource) },
                                "§e§lAmount: §9", "§dWITHDRAW", "", "")
                    }
                }.collect(Collectors.toSet()), machine)
    }

    private fun craftFuelsPage(): InventoryPage {
        return InventoryPage(9, "Fuels", defaultFillment, Sets.newHashSet(
                InventoryItem(2, Material.PAPER, 1, "§aWITHDRAW FUEL", "§bEnter the amount of energy to withdraw")
                        .setOnClick { machinery.createSignInput(p, BiPredicate { p: Player, lines: Array<String> -> handleFuelWithdrawSign(p, lines) }, "§e§lAmount: §9", "§dWITHDRAW", "", "") },
                InventoryItem(6, Material.BEACON, 1, "§aDEPOSIT FUEL", "§bEnter the amount of energy to deposit")
                        .setOnClick { machinery.createSignInput(p, BiPredicate { p: Player, lines: Array<String> -> handleFuelDepositSign(p, lines) }, "§e§lAmount: §9", "§dDEPOSIT", "", "") }
        ), machine)
    }

    private fun craftDeconstructConfirmationPage(): InventoryPage {
        return InventoryPage(9, "Confirm Deconstruction", defaultFillment, Sets.newHashSet(
                InventoryItem(2, Material.RED_STAINED_GLASS_PANE, 1, "§4§lCANCEL"),
                InventoryItem(6, Material.GREEN_STAINED_GLASS_PANE, 1, "§a§lAGREE", "§eThis will deconstruct your machine", "§eYou will be given your machine in " +
                        "the form of a block").setOnClick {
                    p.closeInventory()
                    val machineItem = machine.deconstruct()
                    p.inventory.addItem(machineItem.itemStackWithAppliedPlaceholders)
                }
        ), machine)
    }

    private fun craftUpgradesPage(): InventoryPage {
        val upgrades = machine.upgrades
        val upgradeItems: MutableSet<InventoryItem?> = HashSet()
        val upgradeFillment: InventoryItem = InventoryFillmentItem(Material.RED_STAINED_GLASS_PANE, 1, ChatColor.RED.toString() + "BLOCKED", "§cPurchase previous upgrade(s).")
        for (upgradeNumber in upgrades!!.indices) {
            val upgrade = upgrades[upgradeNumber]
            for (upgradeLevel in 1..upgrade!!.level) {
                val upgradeItem = ItemStack(Material.LIME_STAINED_GLASS_PANE, upgradeLevel)
                val meta = upgradeItem.itemMeta
                meta!!.setDisplayName(ChatColor.DARK_AQUA.toString() + upgrade.upgradeName + " " + ChatColor.GREEN + "(" + upgrade.level + ")")
                meta.lore = listOf("§aPurchased")
                upgradeItem.itemMeta = meta
                upgradeItems.add(InventoryItem(upgradeNumber * 9 + (upgradeLevel - 1), upgradeItem))
            }

            val nextUpgradeItem = ItemStack(Material.GREEN_STAINED_GLASS_PANE, upgrade.level + 1)
            val meta = nextUpgradeItem.itemMeta
            meta!!.setDisplayName(ChatColor.DARK_AQUA.toString() + upgrade.upgradeName + " " + ChatColor.GREEN + "(" + (upgrade.level + 1) + ")")
            meta.lore = listOf("§aClick to upgrade!")
            nextUpgradeItem.itemMeta = meta
            upgradeItems.add(InventoryNavigationItemData(craftUpgradeVerificationPage(), parentInventory, upgradeNumber * 9 + upgrade.level, nextUpgradeItem,
                    upgrade))
        }
        return InventoryPage((machine.upgrades?.size
                ?: 1) * 9, "Upgrades", upgradeFillment, upgradeItems, machine)
    }

    private fun craftUpgradeVerificationPage(): NavigableDataInventoryPage<AbstractUpgrade?> {
        return NavigableDataInventoryPage(
                9,
                "Upgrade!",
                defaultFillment,
                null,
                machine,
                this::upgradeInventory
        )
    }

    private fun handleFuelWithdrawSign(p: Player, lines: Array<String>): Boolean {
        val energyInMachine = machine.energyInMachine
        val amountToWithdraw = validateAndGetNumber(lines, energyInMachine, "Fuel", Message("messages.yml", "sign_input.entered_above_max_fuel_withdraw_limit", p,
                Utils.getLocationPlaceholders(machine.core,
                        Utils.getMachinePlaceholders(machine, Placeholder("%amount%", energyInMachine)))), false)
        if (amountToWithdraw == -1) return false
        if (amountToWithdraw == 0) {
            for (f in getPlayerFuelsInInventory(p.inventory)) {
                if (f.second!!.energy != 0) continue
                f.second!!.amount = 0
                p.inventory.setItem(f.first!!, f.second)
            }
            return true
        }
        machine.removeEnergy(amountToWithdraw)
        Utils.giveItemOrDrop(p, machinery.fuelManager?.getFuel(1, amountToWithdraw), false)
        machinery.updateMachineBlock(machine, true)
        return true
    }

    private fun handleFuelDepositSign(p: Player, lines: Array<String>): Boolean {
        val energyInMachine = machine.energyInMachine
        val maxDeposit = machine.maxFuel - energyInMachine
        val amountToDeposit = validateAndGetNumber(lines, maxDeposit, "Fuel", Message("messages.yml", "sign_input.entered_above_max_fuel_deposit_limit", p,
                Utils.getLocationPlaceholders(machine.core,
                        Utils.getMachinePlaceholders(machine, Placeholder("%amount%", maxDeposit)))), false)

        if (amountToDeposit == -1) return false
        if (amountToDeposit == 0) {
            for (f in getPlayerFuelsInInventory(p.inventory)) {
                if (f.second!!.energy != 0) continue
                f.second!!.amount = 0
                p.inventory.setItem(f.first!!, f.second)
            }
            return true
        }
        val inventory: org.bukkit.inventory.Inventory = p.inventory
        val fuelsOnPlayer: List<Pair<Int?, Fuel?>> = getPlayerFuelsInInventory(p.inventory).sortedByDescending { t -> t.second!!.energy }
        val fuelEnergyInInventory = fuelsOnPlayer.stream().mapToInt { pair -> pair.second?.energy!! }.sum()
        if (fuelEnergyInInventory < amountToDeposit) {
            Message("messages.yml", "sign_input.not_enough_fuel_in_inventory", p, Utils.getLocationPlaceholders(machine.core,
                    Utils.getMachinePlaceholders(machine, Utils.getAmountThingPlaceholder(amountToDeposit, "Fuel Energy")))).send()
            return false
        }
        var energyLeftToDeposit = amountToDeposit
        for (playerFuelPair in fuelsOnPlayer) {
            val fuel: Fuel = playerFuelPair.second!!
            val indexInInventory: Int = playerFuelPair.first!!
            val fuelEnergy = fuel.energy
            val maxEnergyCanDespositFromFuel = min(fuelEnergy, energyLeftToDeposit)
            val fuelsToDeposit = maxEnergyCanDespositFromFuel / fuel.baseEnergy

            fuel.amount = fuel.amount - fuelsToDeposit
            fuel.energy = fuelEnergy - maxEnergyCanDespositFromFuel
            inventory.setItem(indexInInventory, fuel)
            machine.addEnergy(maxEnergyCanDespositFromFuel)
            energyLeftToDeposit -= maxEnergyCanDespositFromFuel
            if (energyLeftToDeposit == 0) break
        }
        if (machine.energyInMachine > 0 && !machine.minerProcess.isRunning) machine.minerProcess.startProcess()
        machinery.updateMachineBlock(machine, true)
        return true
    }

    private fun handleZenCoinWithdrawalSign(p: Player, lines: Array<String>): Boolean {
        Message("messages.yml", "sign_input.withdraw", p, Utils.getLocationPlaceholders(machine.core,
                Utils.getMachinePlaceholders(machine, Placeholder("%thing%", "Zen Coins")))).send()
        try {
            val num = validateAndGetNumber(lines, machine.zenCoinsGained as Int, "Zen Coins",
                    Message("messages.yml", "sign_input.not_enough_resources_in_machine", p, Utils.getLocationPlaceholders(machine.core,
                            Utils.getMachinePlaceholders(machine, Utils.getAmountThingPlaceholder(-1, "Zen Coins")))))
            if (num == -1) return false else if (num == 0) return true
            depositZenCoinsIntoProfile(num)
            machine.removeZenCoinsGained(num.toDouble())
            machinery.updateMachineBlock(machine, true)
        } catch (e: NumberFormatException) {
            p.sendMessage("§c§lInvalid number!")
            return false
        }
        return true
    }

    private fun handleResourceWithdrawalSign(p: Player, lines: Array<String>, resource: ItemStack): Boolean {
        val resourceName = StringUtils.capitalize(resource.type.toString().toLowerCase().replace("_".toRegex(), " "))
        Message("messages.yml", "sign_input.withdraw", p, Utils.getLocationPlaceholders(machine.core,
                Utils.getMachinePlaceholders(machine, Placeholder("%thing%", "Resources")))).send()
        val resourceAmountToWithdraw = validateAndGetNumber(
                lines,
                resource.amount,
                resourceName,
                Message("messages.yml", "sign_input.not_enough_resources_in_machine", p, Utils.getLocationPlaceholders(machine.core,
                        Utils.getMachinePlaceholders(machine, Utils.getAmountThingPlaceholder(-1, resourceName)))))
        if (resourceAmountToWithdraw == -1) return false else if (resourceAmountToWithdraw == 0) return true
        resource.amount = resource.amount - resourceAmountToWithdraw
        val givenItemStack = resource.clone()
        givenItemStack.amount = resourceAmountToWithdraw
        if (!Utils.inventoryHasSpaceForItemAdd(p.inventory)) {
            Message("messages.yml", "not_enough_inventory_space", p, Utils.getLocationPlaceholders(machine.core, Utils.getMachinePlaceholders(machine,
                    Utils.getAmountThingPlaceholder(resourceAmountToWithdraw, resourceName)))).send()
            return false
        }
        p.inventory.addItem(givenItemStack)
        machinery.updateMachineBlock(machine, true)
        return true
    }

    private fun validateAndGetNumber(lines: Array<String>, maxCanTakeAmount: Int, name: String, aboveMaxErrorMessage: Message, mutatePlaceholder: Boolean = true): Int {
        return try {
            val split = lines[0].split("§9".toRegex()).toTypedArray()
            if (split.size == 1) throw NumberFormatException()
            val num = split[1].toInt()
            if (num < 0) {
                Message("messages.yml", "sign_input.entered_number_below_zero", p, Utils.getLocationPlaceholders(machine.core,
                        Utils.getMachinePlaceholders(machine, Utils.getAmountThingPlaceholder(num, name)))).send()
                return -1
            } else if (num == 0) {
                return 0
            }
            if (num > maxCanTakeAmount) {
                if (mutatePlaceholder) aboveMaxErrorMessage.replacePlaceholder("%amount%", Placeholder("%amount%", num)).send() else aboveMaxErrorMessage.send()
                return -1
            }
            num
        } catch (e: NumberFormatException) {
            Message("messages.yml", "sign_input.invalid_number", p, Utils.getLocationPlaceholders(machine.core, Utils.getMachinePlaceholders(machine))).send()
            -1
        }
    }

    private fun getPlayerFuelsInInventory(inventory: org.bukkit.inventory.Inventory): List<Pair<Int?, Fuel?>> {
        val contents = inventory.contents
        val fuels: MutableList<Pair<Int?, Fuel?>> = ArrayList()
        val fuelManager = machinery.fuelManager
        for (i in contents.indices) {
            val item = contents[i]
            val compound = CraftItemStack.asNMSCopy(item).tag
            if (compound == null || !compound.hasKey("machine_fuel") || !compound.hasKey("fuel_energy")) continue
            fuels.add(Pair.of(i, fuelManager!!.getFuel(item)))
        }
        return fuels
    }

    private fun upgradeInventory(upgrade: AbstractUpgrade?, navigableDataPage: NavigableDataPage<AbstractUpgrade?>) {
        val inventoryPage = navigableDataPage as NavigableDataInventoryPage<AbstractUpgrade>
        val costs = upgrade!!.costs
        val upgradeCost = costs!!.getValue(upgrade.level + 1)
        val downgradeCost = upgrade.costs?.getOrDefault(upgrade.level - 1, -1)!!
        inventoryPage.setInventoryItems(Sets.newHashSet(
                InventoryItem(2, Material.LIME_STAINED_GLASS_PANE, 1, "§6DOWNGRADE",
                        if (downgradeCost == -1) "§bCANNOT DOWNGRADE" else "§bDowngrade to level §e" + (upgrade.level - 1),
                        if (downgradeCost == -1) "" else "§bCost: §e" + decimalFormat.format(downgradeCost.toLong()) + " Zen Coins").setOnClick {
                    if (downgradeCost == -1) {
                        Message("messages.yml", "cannot_downgrade", p, Utils.getLocationPlaceholders(machine.core,
                                Utils.getMachinePlaceholders(machine))).send()
                        return@setOnClick
                    }
                    depositZenCoinsIntoProfile(downgradeCost)
                    upgrade.downgrade()
                    machine.minerProcess.applyUpgradeModifiers()
                    p.closeInventory()
                    Message("messages.yml", "on_downgrade", p, Utils.getLocationPlaceholders(machine.core,
                            Utils.getMachinePlaceholders(machine, Placeholder("%amount%", downgradeCost)))).send()
                    machinery.machineManager?.setPlayerMachineBlock(machine.core?.block!!, machine)
                },
                InventoryItem(4, Material.RED_STAINED_GLASS_PANE, 1, "§cCANCEL").setOnClick { p.closeInventory() },
                InventoryItem(6, Material.LIME_STAINED_GLASS_PANE, 1, "§6UPGRADE", "§bUpgrade to level §e" + (upgrade.level + 1),
                        "§bCost: §e" + decimalFormat.format(upgradeCost.toLong()) + " Zen Coins").setOnClick {
                    val mobCoinsOfUser = mobCoinsProfile.mobCoins
                    if (upgrade.level == upgrade.maxLevel) {
                        Message("messages.yml", "reached_max_upgrade_level", p, Utils.getLocationPlaceholders(machine.core,
                                Utils.getMachinePlaceholders(machine))).send()
                        return@setOnClick
                    } else if (upgradeCost > mobCoinsOfUser) {
                        Message("messages.yml", "insufficient_funds_for_upgrade", p, Utils.getLocationPlaceholders(machine.core,
                                Utils.getMachinePlaceholders(machine, Utils.getAmountThingPlaceholder(upgradeCost - mobCoinsOfUser, "Zen Coins")))).send()
                        return@setOnClick
                    }
                    withdrawZenCoinsFromProfile(upgradeCost)
                    upgrade.upgrade()
                    machine.minerProcess.applyUpgradeModifiers()
                    p.closeInventory()
                    Message("messages.yml", "on_upgrade", p, Utils.getLocationPlaceholders(machine.core,
                            Utils.getMachinePlaceholders(machine, Placeholder("%amount%", upgradeCost)))).send()
                    machinery.updateMachineBlock(machine, true)
                }
        ))
    }

    private fun depositZenCoinsIntoProfile(amount: Int) {
        val profile = mobCoinsProfile
        profile.mobCoins = profile.mobCoins + amount
    }

    private fun withdrawZenCoinsFromProfile(amount: Int) {
        depositZenCoinsIntoProfile(-amount)
    }

    val mobCoinsProfile: Profile
        get() = MobCoinsAPI.getProfileManager().getProfile(p)

    init {
        defaultFillment = InventoryFillmentItem(Material.GRAY_STAINED_GLASS_PANE, 1, "")
        decimalFormat = DecimalFormat("#,###")
        routes = HashMap()
        parentInventory = Inventory(routes, p)
        craftInventory()
    }
}