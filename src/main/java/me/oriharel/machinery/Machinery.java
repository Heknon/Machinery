package me.oriharel.machinery;

import co.aikar.commands.BukkitCommandManager;
import com.google.common.collect.Lists;
import me.oriharel.machinery.data.MachineDataManager;
import me.oriharel.machinery.fuel.FuelManager;
import me.oriharel.machinery.inventory.Listeners;
import me.oriharel.machinery.listeners.Block;
import me.oriharel.machinery.listeners.Interact;
import me.oriharel.machinery.machine.Machine;
import me.oriharel.machinery.machine.MachineManager;
import me.oriharel.machinery.machine.PlayerMachine;
import me.oriharel.machinery.structure.StructureManager;
import me.oriharel.machinery.utilities.SignMenuFactory;
import me.oriharel.machinery.utilities.Utils;
import me.wolfyscript.customcrafting.CustomCrafting;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

/**
 * Next time I am doing this....
 * It's gonna be in Kotlin....
 * The horror's I have seen...
 */
public final class Machinery extends JavaPlugin {

    public final static String STRUCTURE_EXTENSION = ".schem";
    private static Machinery INSTANCE;
    private FileManager fileManager;
    private MachineManager machineManager;
    private StructureManager structureManager;
    private FuelManager fuelManager;
    private MachineDataManager machineDataManager;
    private SignMenuFactory signMenuFactory;
    private BukkitCommandManager commandManager;

    public static Machinery getInstance() {
        return INSTANCE;
    }

    @Override
    public void onLoad() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {

        Utils.MACHINERY_INSTANCE = this;

        // save all configuration files
        fileManager = new FileManager(this);

        setupConfigs();

        Path file = new File(getDataFolder(), "structures").toPath();
        if (!Files.exists(file)) {
            try {
                Files.createDirectory(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.signMenuFactory = new SignMenuFactory(this);

        Bukkit.getPluginManager().registerEvents(new Listeners(this), this);

        fuelManager = new FuelManager(this);

        structureManager = new StructureManager(this);
        structureManager.registerOnDoneCallback(() -> {
            machineManager = new MachineManager(this);
            setupCommandManager();
            Bukkit.getServer().getPluginManager().registerEvents(new Block(this), this);
            Bukkit.getServer().getPluginManager().registerEvents(new Interact(this), this);
            machineDataManager = new MachineDataManager(this);
            Bukkit.getWorlds().forEach(machineDataManager::loadMachineData);
            machineDataManager.startMachineSaveDataProcess();
            getLogger().info("Finished loading plugin!");
        });

    }

    @Override
    public void onDisable() {
        machineDataManager.forceMachineDataSave();
    }

    /**
     * Helper function for creating a sign GUI for a user to input data in
     * @param target the player that the sign GUI will open for
     * @param response callback when users submits text
     * @param defaultLines  default text
     */
    public void createSignInput(Player target, BiPredicate<Player, String[]> response, String... defaultLines) {
        this.signMenuFactory
                .newMenu(Lists.newArrayList(defaultLines))
                .reopenIfFail()
                .response(response)
                .open(target);
    }

    /**
     * Helper function to update a player machine block with it's new value.
     * used to not go through machine manager and it's "obscure" naming
     * @param machine the new machine data
     * @param fromAsyncThread if it is from an asynchronous thread it will summon a bukkit task on the master thread since you cannot modify blocks on other threads
     */
    public void updateMachineBlock(PlayerMachine machine, boolean fromAsyncThread) {
        if (fromAsyncThread) Bukkit.getScheduler().runTask(this, () -> machineManager.setPlayerMachineBlock(machine.getMachineCore().getBlock(), machine));
        else machineManager.setPlayerMachineBlock(machine.getMachineCore().getBlock(), machine);
    }

    /**
     * helper function for setting up the command manager.
     */
    private void setupCommandManager() {
        this.commandManager = new BukkitCommandManager(this);
        commandManager.getCommandCompletions().registerCompletion("machines",
                c -> machineManager.getMachines().stream().map(Machine::getMachineName).collect(Collectors.toList()));
        commandManager.getCommandContexts().registerIssuerAwareContext(Machine.class, (c) -> {
            String machineName = c.getLastArg();
            if (machineName == null) return null;
            return machineManager.getMachines().stream().filter(machine -> machine.getMachineName().equalsIgnoreCase(machineName)).findFirst().get();
        });
        commandManager.registerCommand(new MachineCommand(this));
    }

    /**
     * helper function for initializing all the config files this plugins uses before it starts up.
     */
    private void setupConfigs() {
        fileManager.getConfig("config.yml").initialize();
        fileManager.getConfig("messages.yml").initialize();
        fileManager.getConfig("fuel.yml").initialize();
        fileManager.getConfig("upgrades.yml").initialize();
        fileManager.getConfig("machines.yml").initialize();
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public MachineManager getMachineManager() {
        return machineManager;
    }

    public StructureManager getStructureManager() {
        return structureManager;
    }

    public FuelManager getFuelManager() {
        return fuelManager;
    }

    public MachineDataManager getMachineDataManager() {
        return machineDataManager;
    }
}
