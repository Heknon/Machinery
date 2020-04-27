package me.oriharel.machinery

import co.aikar.commands.BukkitCommandCompletionContext
import co.aikar.commands.BukkitCommandExecutionContext
import co.aikar.commands.BukkitCommandManager
import me.oriharel.machinery.commands.MachineCommand
import me.oriharel.machinery.fuel.FuelManager
import me.oriharel.machinery.inventory.Listeners
import me.oriharel.machinery.machines.MachineDataManager
import me.oriharel.machinery.machines.MachineManager
import me.oriharel.machinery.machines.listeners.Block
import me.oriharel.machinery.machines.listeners.Interact
import me.oriharel.machinery.machines.machine.Machine
import me.oriharel.machinery.machines.machine.PlayerMachine
import me.oriharel.machinery.structure.StructureManager
import me.oriharel.machinery.utilities.SignMenuFactory
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.function.BiPredicate
import java.util.function.Consumer
import java.util.stream.Collectors

class Machinery : JavaPlugin() {
    var fileManager: FileManager? = null
        private set
    var machineManager: MachineManager? = null
        private set
    var structureManager: StructureManager? = null
        private set
    var fuelManager: FuelManager? = null
        private set
    var machineDataManager: MachineDataManager? = null
        private set
    private var signMenuFactory: SignMenuFactory? = null
    private var commandManager: BukkitCommandManager? = null


    override fun onLoad() {
        instance = this
    }

    override fun onEnable() {

        // save all configuration files
        fileManager = FileManager(this)
        setupConfigs()

        logger.fine("Machinery - Made by Heknon - https://oriharel.me")

        // create structures folder if non existent
        val file = File(dataFolder, "structures").toPath()
        if (!Files.exists(file)) {
            try {
                Files.createDirectory(file)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        signMenuFactory = SignMenuFactory(this)
        Bukkit.getPluginManager().registerEvents(Listeners(this), this)
        fuelManager = FuelManager(this)
        structureManager = StructureManager(this)
        structureManager!!.registerOnDoneCallback {

            // Everything in here is dependant on StructureManager. StructureManager is loaded asynchronously.
            machineManager = MachineManager(this)
            setupCommandManager()
            Bukkit.getServer().pluginManager.registerEvents(Block(this), this)
            Bukkit.getServer().pluginManager.registerEvents(Interact(this), this)
            machineDataManager = MachineDataManager(this)
            Bukkit.getWorlds().forEach(Consumer { world: World -> machineDataManager!!.loadMachineData(world) })
            machineDataManager!!.startMachineSaveDataProcess()
            logger.info("Finished loading plugin!")
        }
    }

    override fun onDisable() {
        machineDataManager!!.forceMachineDataSave()
    }

    /**
     * Helper function for creating a sign GUI for a user to input data in
     * @param target the player that the sign GUI will open for
     * @param response callback when users submits text
     * @param defaultLines  default text
     */
    fun createSignInput(target: Player, response: BiPredicate<Player, Array<String>>?, vararg defaultLines: String) {
        signMenuFactory
                ?.newMenu(listOf(*defaultLines))
                ?.reopenIfFail()
                ?.response(response)
                ?.open(target)
    }

    /**
     * Helper function to update a player machine block with it's new value.
     * used to not go through machine manager and it's "obscure" naming
     * @param machine the new machine data
     * @param fromAsyncThread if it is from an asynchronous thread it will summon a bukkit task on the master thread since you cannot modify blocks on other threads
     */
    fun updateMachineBlock(machine: PlayerMachine?, fromAsyncThread: Boolean) {
        if (fromAsyncThread) Bukkit.getScheduler().runTask(
                this,
                Runnable { machineManager!!.setPlayerMachineBlock(machine?.core?.block!!, machine) }
        ) else machineManager!!.setPlayerMachineBlock(machine?.core?.block!!, machine)
    }

    /**
     * helper function for setting up the command manager.
     */
    private fun setupCommandManager() {
        commandManager = BukkitCommandManager(this)
        commandManager!!.commandCompletions.registerCompletion("machines"
        ) { c: BukkitCommandCompletionContext? -> machineManager!!.machines.stream().map { machine: Machine? -> machine?.name }.collect(Collectors.toList()) }
        commandManager!!.commandContexts.registerIssuerAwareContext(Machine::class.java) { c: BukkitCommandExecutionContext ->
            val machineName = c.lastArg ?: return@registerIssuerAwareContext null
            machineManager!!.machines.stream().filter { machine: Machine? -> machine?.name.equals(machineName, ignoreCase = true) }.findFirst().get()
        }
        commandManager!!.registerCommand(MachineCommand(this))
    }

    /**
     * helper function for initializing all the config files this plugins uses before it starts up.
     */
    private fun setupConfigs() {
        fileManager!!.getConfig("config.yml")!!.initialize()
        fileManager!!.getConfig("messages.yml")!!.initialize()
        fileManager!!.getConfig("fuel.yml")!!.initialize()
        fileManager!!.getConfig("upgrades.yml")!!.initialize()
        fileManager!!.getConfig("machines.yml")!!.initialize()
    }

    companion object {
        const val STRUCTURE_EXTENSION = ".schem"
        var instance: Machinery? = null
            private set
    }
}