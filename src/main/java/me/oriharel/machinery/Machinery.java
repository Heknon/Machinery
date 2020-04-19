package me.oriharel.machinery;

import me.oriharel.customrecipes.CustomRecipes;
import me.oriharel.machinery.config.FileManager;
import me.oriharel.machinery.data.MachineDataManager;
import me.oriharel.machinery.fuel.FuelManager;
import me.oriharel.machinery.inventory.Listeners;
import me.oriharel.machinery.listeners.Block;
import me.oriharel.machinery.listeners.Interact;
import me.oriharel.machinery.machine.MachineManager;
import me.oriharel.machinery.structure.StructureManager;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Machinery extends JavaPlugin {

    public final static String STRUCTURE_EXTENSION = ".schem";
    private static Machinery INSTANCE;
    private FileManager fileManager;
    private MachineManager machineManager;
    private StructureManager structureManager;
    private FuelManager fuelManager;
    private MachineDataManager machineDataManager;

    public static Machinery getInstance() {
        return INSTANCE;
    }

    @Override
    public void onLoad() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {

        ConsoleCommandSender console = Bukkit.getConsoleSender();
        console.sendMessage("Starting up Machinery");


        fileManager = new FileManager(this);
        fileManager.getConfig("config.yml").copyDefaults(true).save();
        fileManager.getConfig("fuels.yml").copyDefaults(true).save();
        fileManager.getConfig("machine_registry.yml").copyDefaults(true).save();
        fileManager.getConfig("machines.yml").copyDefaults(true).save();
        File file = new File(getDataFolder(), "structures");
        if (!file.exists()) file.mkdir();
        Bukkit.getPluginManager().registerEvents(new Listeners(), this);
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> fuelManager = new FuelManager(this));
        getPlugin(CustomRecipes.class).getRecipesManager().registerRecipesDoneCallback(() -> Bukkit.getScheduler().runTask(this, () -> {
            structureManager = new StructureManager(this);
            structureManager.registerOnDoneCallback(() -> {
                machineManager = new MachineManager(this);
                Bukkit.getServer().getPluginManager().registerEvents(new Block(this), this);
                Bukkit.getServer().getPluginManager().registerEvents(new Interact(this), this);
                machineDataManager = new MachineDataManager(this);
                Bukkit.getWorlds().forEach(machineDataManager::loadMachineData);
                machineDataManager.startMachineSaveDataProcess();
            });
        }));
    }

    @Override
    public void onDisable() {
        machineDataManager.forceMachineDataSave();
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
