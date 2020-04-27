package me.oriharel.machinery.machines.machine

import me.oriharel.machinery.Machinery
import me.oriharel.machinery.message.ChanceableOperation
import me.oriharel.machinery.message.Message
import me.oriharel.machinery.resources.chance.ChancableList
import me.oriharel.machinery.upgrades.AbstractUpgrade
import me.oriharel.machinery.utilities.RandomCollection
import me.oriharel.machinery.utilities.Utils
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import java.util.function.Consumer

class MachineResourceGetProcess(var machine: PlayerMachine) {
    var itemsGained: MutableList<ItemStack?> = ArrayList()
    private var zenCoinsGained: Long
    var minePeriod: Int = 20
    private var process: BukkitRunnable?
    private var chanceables: RandomCollection<ChancableList<out ChanceableOperation<*, MachineResourceGetProcess?>?>?>?
    var lootAmplifier: Double

    /**
     * starts the asynchronous process of gaining resources
     */
    fun startProcess() {
        val upgrades = machine.upgrades
        applyUpgradeModifiers()
        process = object : BukkitRunnable() {
            override fun run() {
                if (machine.energyInMachine < machine.fuelDeficiency) {
                    cancel()
                    val player = machine.owner?.let { Bukkit.getPlayer(it) }
                    if (player == null || !player.isOnline) return
                    Message("messages.yml", "not_enough_fuel_to_operate", player, Utils.getLocationPlaceholders(machine.core,
                            Utils.getMachinePlaceholders(machine)))
                    return
                }
                runResourceGetProcess()
                upgrades!!.forEach(Consumer { upgrade: AbstractUpgrade? -> if (!upgrade!!.isRunOnlyOnProcessStart) upgrade.applyUpgradeModifier(this@MachineResourceGetProcess) })
                insertResources()
                runFuelRemoval()
            }
        }
        process?.runTaskTimerAsynchronously(Machinery.instance!!, minePeriod.toLong(), minePeriod.toLong())
    }

    fun endProcess() {
        process!!.cancel()
    }

    val isRunning: Boolean
        get() = !process!!.isCancelled


    private fun runResourceGetProcess() {
        if (chanceables == null) {
            initializeMaterialChances()
        }

        val chance: ChanceableOperation<*, MachineResourceGetProcess>? = chanceables!!.next()

        chance!!.executeChanceOperation(this, lootAmplifier)
    }

    private fun runFuelRemoval() {
        machine.removeEnergy(machine.fuelDeficiency)
    }

    /**
     * insert all the stuff gained from the process
     * checks if ItemStack resource type is already in machine, if it is, it adds x amount to it. If not create a new entry.
     */
    private fun insertResources() {
        val machineResourcesGained = machine.resourcesGained?.toMutableMap()
        val totalAmount = AtomicReference(0.toDouble())
        itemsGained.forEach(
                Consumer { item: ItemStack? ->
                    totalAmount.updateAndGet { v: Double -> v + item!!.amount }
                    if (machineResourcesGained!!.containsKey(item!!.type)) {
                        val prev = machineResourcesGained[item.type]
                        prev!!.amount = prev.amount + item.amount
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
        val resourceMap = machine.factory.machinery.machineManager?.machineResourceTrees?.get(machine.name)
        chanceables = RandomCollection()

        for ((weight, value) in resourceMap!!) {
            chanceables!!.add(weight?.toDouble(), value)
        }
    }

    fun addZenCoinsGained(amount: Long) {
        zenCoinsGained += amount
    }

    init {
        chanceables = null
        process = null
        lootAmplifier = 1.0
        zenCoinsGained = 0
        initializeMaterialChances()
    }
}