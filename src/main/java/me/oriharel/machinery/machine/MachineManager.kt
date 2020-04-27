package me.oriharel.machinery.machine

import me.oriharel.machinery.Machinery
import me.oriharel.machinery.api.events.PostMachineBuildEvent
import me.oriharel.machinery.api.events.PreMachineBuildEvent
import me.oriharel.machinery.data.PlayerMachinePersistentDataType
import me.oriharel.machinery.exceptions.*
import me.oriharel.machinery.structure.Structure.PrintResult
import me.oriharel.machinery.upgrades.LootBonusUpgrade
import me.oriharel.machinery.upgrades.SpeedUpgrade
import me.oriharel.machinery.utilities.Utils
import org.apache.commons.lang.ArrayUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.block.TileState
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.craftbukkit.libs.jline.internal.Nullable
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*
import java.util.function.Function

class MachineManager(private val machinery: Machinery) {
    val machineFactory: MachineFactory = MachineFactory(machinery)
    val machines: MutableList<Machine?> = MutableList()
    val machineCores: HashMap<Location?, PlayerMachine?>
    val machinePartLocations: HashSet<Location?>
    val temporaryPreRegisterMachineLocations: HashSet<Location?>
    private val machinePersistentDataType: PlayerMachinePersistentDataType
    private val machineNamespacedKey: NamespacedKey
    private val machineLocationsNamespacedKey: NamespacedKey
    private val machineResourceTrees: MutableMap<String?, ResourceMap?>

    /**
     * initialize all base machine types
     */
    private fun initializeBaseMachines() {
        val configLoad: YamlConfiguration? = machinery.fileManager?.getConfig("machines.yml")?.get()
        val machineKeys: Set<String>? = configLoad?.getKeys(false)

        for (key in machineKeys!!) {
            try {
                val machine = machineFactory.createMachine(key)
                machineResourceTrees[key] = ResourceMap(key, configLoad)
                machines.add(machine)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: NotMachineTypeException) {
                e.printStackTrace()
            } catch (e: MachineNotFoundException) {
                e.printStackTrace()
            } catch (e: MaterialNotFoundException) {
                e.printStackTrace()
            } catch (e: RecipeNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * registers a new machine to core machine block and to plugin caches
     * @param playerMachine the machine to register
     * @param machineLocations the locations of the machine in the world
     */
    fun registerNewPlayerMachine(playerMachine: PlayerMachine?, machineLocations: Set<Location?>) {
        try {
            setPlayerMachineBlock(playerMachine.getMachineCore().block, playerMachine)
            val locations: Array<Any> = machineLocations.toTypedArray()!!
            setPlayerMachineLocations(playerMachine.getMachineCore().block, Arrays.copyOf(locations, locations.size, Array<Location>::class.java))
            machineCores[playerMachine.getMachineCore()] = playerMachine
            machinePartLocations.addAll(machineLocations)
            val machineLocation = playerMachine.getMachineCore()
            if (machineLocation == null) try {
                throw MachineException("Machine has no open gui block in it's schematic!")
            } catch (e: MachineException) {
                e.printStackTrace()
            }
            machinery.machineDataManager.addMachineCoreLocation(machineLocation)
            playerMachine.getMinerProcess().startProcess()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * removes player machine data from all plugin caches
     * @param machine the machine to remove
     */
    fun unregisterPlayerMachine(machine: PlayerMachine) {
        machineCores.remove(machine.machineCore)
        val playerMachinePartLocations = getPlayerMachineLocations(machine.machineCore.block)
        machinePartLocations.removeAll(Arrays.asList(*playerMachinePartLocations))
        machinery.machineDataManager.removeMachineCoreLocation(machine.machineCore)
    }

    /**
     * sets machine data to a TileState block
     * will throw an exception if block type doesn't extend TileState
     * @param block the machine core
     * @param playerMachine the machine data to set
     */
    fun setPlayerMachineBlock(block: Block, playerMachine: PlayerMachine?) {
        val tileState = block.state as TileState
        val persistentDataContainer = tileState.persistentDataContainer
        persistentDataContainer.set(machineNamespacedKey, machinePersistentDataType, playerMachine)
        tileState.update()
    }

    /**
     * gets a player machine from a block storing the machine data
     * @param block the machine core
     * @return the PlayerMachine if data found, otherwise, null
     */
    @Nullable
    fun getPlayerMachineFromBlock(block: Block): PlayerMachine? {
        return try {
            val tileState = block.state as TileState
            val persistentDataContainer = tileState.persistentDataContainer
            persistentDataContainer.get(machineNamespacedKey, machinePersistentDataType)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * sets the locations belonging to the machine's entir build
     * @param block the core block of the machine
     * @param locations the locations belonging to the machine
     */
    fun setPlayerMachineLocations(block: Block, locations: Array<Location?>?) {
        val tileState = block.state as TileState
        val persistentDataContainer = tileState.persistentDataContainer
        val arr = ArrayUtils.toPrimitive(Arrays.stream(locations).map<Long>(Function { obj: Location? -> Utils.locationToLong() }).toArray<Long> { _Dummy_.__Array__() })
        persistentDataContainer.set(machineLocationsNamespacedKey, PersistentDataType.LONG_ARRAY, arr)
        tileState.update()
    }

    /**
     * get from a machine block it's other parts in the world.
     * Ex. Used to stop players from breaking the machine
     * @param block the machine core block
     * @return all the locations belonging to the machine
     */
    fun getPlayerMachineLocations(block: Block): Array<Location> {
        val tileState = block.state as TileState
        val persistentDataContainer = tileState.persistentDataContainer
        return Arrays.stream(persistentDataContainer.get(machineLocationsNamespacedKey, PersistentDataType.LONG_ARRAY)).mapToObj { packed: Long -> Utils.longToLocation(packed, block.world) }.toArray { _Dummy_.__Array__() }
    }

    /**
     * Removes all plugin made data from a tilestate
     * Made since if a block is air and has TileState data bad stuff will happen
     * @param block the block to remove data from
     */
    fun clearMachineTileStateDataFromBlock(block: Block) {
        val tileState = block.state as TileState
        val persistentDataContainer = tileState.persistentDataContainer
        persistentDataContainer.remove(machineLocationsNamespacedKey)
        persistentDataContainer.remove(machineNamespacedKey)
    }

    /**
     * Further abstract the process of building and registering a new machine
     * @param playerUuid uuid of machine owner
     * @param machine the machine to create
     * @param buildLocation the location to build it in
     * @param <T> the type of the machine
     * @return whether the build was successful or not
    </T> */
    fun <T : Machine?> buildMachine(playerUuid: UUID, machine: T, buildLocation: Location): Boolean {
        return try {
            val preMachineBuildEvent = PreMachineBuildEvent(machine, buildLocation)
            Bukkit.getPluginManager().callEvent(preMachineBuildEvent)
            if (preMachineBuildEvent.isCancelled) return false
            val p = Bukkit.getPlayer(playerUuid)
            val locations = machine!!.structure!!.build(buildLocation, p, machine.machineCoreBlockType) { printResult: PrintResult? ->
                val machineToRegister: PlayerMachine?
                machineToRegister = if (machine is PlayerMachine) {
                    val pMachine = machine as PlayerMachine
                    machineFactory.createMachine(machine, printResult.getOpenGUIBlockLocation(), pMachine.totalResourcesGained,
                            pMachine.energyInMachine, pMachine.zenCoinsGained, pMachine.totalZenCoinsGained, pMachine.owner, pMachine.upgrades,
                            pMachine.resourcesGained, pMachine.playersWithAccessPermission)
                } else {
                    Machinery.Companion.getInstance().getMachineManager().getMachineFactory().createMachine(machine,
                            printResult.getOpenGUIBlockLocation(), 0.0, 0, 0.0, 0.0, playerUuid, Arrays.asList(
                            LootBonusUpgrade(1),
                            SpeedUpgrade(1)
                    ), HashMap<Material?, ItemStack?>(), setOf(playerUuid))
                }
                registerNewPlayerMachine(machineToRegister, HashSet(printResult.getPlacementLocations()))
                val postMachineBuildEvent = PostMachineBuildEvent(machineToRegister, buildLocation)
                Bukkit.getPluginManager().callEvent(postMachineBuildEvent)
                true
            }
                    ?: return false
            Machinery.Companion.getInstance().getMachineManager().addTemporaryPreRegisterMachinePartLocations(locations)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun addTemporaryPreRegisterMachinePartLocations(locations: List<Location?>?) {
        temporaryPreRegisterMachineLocations.addAll(locations!!)
    }

    fun getMachines(): List<Machine?> {
        return machines
    }

    fun getMachineResourceTrees(): Map<String?, ResourceMap?> {
        return machineResourceTrees
    }

    init {
        machines = ArrayList()
        machineCores = HashMap()
        machinePartLocations = HashSet()
        machineResourceTrees = HashMap()
        temporaryPreRegisterMachineLocations = HashSet()
        machinePersistentDataType = PlayerMachinePersistentDataType(machineFactory)
        machineNamespacedKey = NamespacedKey(machinery, "machine")
        machineLocationsNamespacedKey = NamespacedKey(machinery, "machine_locations")
        initializeBaseMachines()
    }
}