package me.oriharel.machinery.machine

import me.oriharel.machinery.Machinery
import me.oriharel.machinery.data.ChancableList
import me.oriharel.machinery.data.ChanceableOperation
import me.oriharel.machinery.machine.MachineResourceGetProcess
import me.oriharel.machinery.message.Message
import me.oriharel.machinery.upgrades.AbstractUpgrade
import me.oriharel.machinery.utilities.RandomCollection
import me.oriharel.machinery.utilities.Utils
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import java.util.function.Consumer

class MachineResourceGetProcess(machine: PlayerMachine) {
    var itemsGained: MutableList<ItemStack?>
    private var zenCoinsGained: Long
    var machine: PlayerMachine
    var minePeriod: Int
    private var process: BukkitRunnable?
    var chanceables: RandomCollection<ChancableList<out ChanceableOperation<*, MachineResourceGetProcess?>?>?>?
        private set
    var lootAmplifier: Double

    /**
     * starts the asynchronous process of gaining resources
     */
    fun startProcess() {
        val upgrades = machine.upgrades
        applyUpgradeModifiers()
        process = object : BukkitRunnable() {
            override fun run() {
                if (machine.energyInMachine < machine.getFuelDeficiency()) {
                    cancel()
                    val player = Bukkit.getPlayer(machine.owner)
                    if (player == null || !player.isOnline) return
                    Message("messages.yml", "not_enough_fuel_to_operate", player, Utils.getLocationPlaceholders(machine.machineCore,
                            Utils.getMachinePlaceholders(machine)))
                    return
                }
                resources
                upgrades!!.forEach(Consumer { upgrade: AbstractUpgrade? -> if (!upgrade!!.isRunOnlyOnProcessStart) upgrade.applyUpgradeModifier(this@MachineResourceGetProcess) })
                insertResources()
                runFuelRemoval()
            }
        }
        process.runTaskTimerAsynchronously(Machinery.Companion.getInstance(), minePeriod.toLong(), minePeriod.toLong())
    }

    fun endProcess() {
        process!!.cancel()
    }

    val isRunning: Boolean
        get() = !process!!.isCancelled

    private val resources: Unit
        private get() {
            if (chanceables == null) {
                initializeMaterialChances()
            }
            val chance: ChanceableOperation<*, MachineResourceGetProcess>? = chanceables!!.next()
            chance!!.executeChanceOperation(this, lootAmplifier)
        }

    private fun runFuelRemoval() {
        machine.removeEnergy(machine.getFuelDeficiency())
    }

    /**
     * insert all the stuff gained from the process
     * checks if ItemStack resource type is already in machine, if it is, it adds x amount to it. If not create a new entry.
     */
    private fun insertResources() {
        val machineResourcesGained = machine.resourcesGained
        val totalAmount = AtomicReference(0.toDouble())
        itemsGained.forEach(
                Consumer { item: ItemStack? ->
                    totalAmount.updateAndGet { v: Double -> v + item!!.amount }
                    if (machineResourcesGained!!.containsKey(item!!.type)) {
                        val prev = machineResourcesGained[item.type]
                        prev!!.amount = prev.amount + item.amount
                        return@forEach
                    }
                    machineResourcesGained[item.type] = item.clone()
                }
        )
        machine.totalResourcesGained = machine.totalResourcesGained + totalAmount.get()
        machine.addZenCoinsGained(zenCoinsGained.toDouble())
        zenCoinsGained = 0
    }

    /**
     * Apply the upgrade modifiers on the mining process
     */
    fun applyUpgradeModifiers() {
        val upgrades = machine.upgrades
        upgrades!!.forEach(Consumer { upgrade: AbstractUpgrade? -> upgrade!!.applyUpgradeModifier(this) })
    }

    /**
     * gets the resourcemap of this machine and adds all it's values to a RandomCollection to allow to get random values from the collection based on weight
     */
    private fun initializeMaterialChances() {
        chanceables = RandomCollection()
        val resourceMap = machine.factory.machinery.machineManager.machineResourceTrees[machine.machineName]
        for ((weight, value) in resourceMap!!) {
            chanceables!!.add(weight.toDouble(), value)
        }
    }

    fun addZenCoinsGained(amount: Long) {
        zenCoinsGained += amount
    }

    init {
        itemsGained = ArrayList()
        this.machine = machine
        minePeriod = 20
        chanceables = null
        process = null
        lootAmplifier = 1.0
        zenCoinsGained = 0
        initializeMaterialChances()
    }
}