package me.oriharel.machinery;

import me.oriharel.customrecipes.api.CustomRecipesAPI;
import me.oriharel.machinery.config.FileManager;
import me.oriharel.machinery.listeners.Block;
import me.oriharel.machinery.machine.MachineManager;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class Machinery extends JavaPlugin {

    private static Machinery INSTANCE;
    private FileManager fileManager;
    private MachineManager machineManager;

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
        console.sendMessage("Starting up CustomRecipes");

        fileManager = new FileManager(this);
        fileManager.getConfig("config.yml").copyDefaults(true).save();
        fileManager.getConfig("fuels.yml").copyDefaults(true).save();
        fileManager.getConfig("machine_registry.yml").copyDefaults(true).save();
        fileManager.getConfig("machines.yml").copyDefaults(true).save();
        CustomRecipesAPI.getImplementation().getRecipesManager().registerRecipesDoneCallback(() -> Bukkit.getScheduler().runTask(this, () -> machineManager =
                new MachineManager(this)));
        Bukkit.getServer().getPluginManager().registerEvents(new Block(), this);
    }

    @Override
    public void onDisable() {
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public MachineManager getMachineManager() {
        return machineManager;
    }
}
