package me.oriharel.machinery;

import co.aikar.commands.BukkitCommandManager;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import me.oriharel.machinery.data.MachineDataManager;
import me.oriharel.machinery.fuel.FuelManager;
import me.oriharel.machinery.inventory.Listeners;
import me.oriharel.machinery.listeners.Block;
import me.oriharel.machinery.listeners.Interact;
import me.oriharel.machinery.machine.Machine;
import me.oriharel.machinery.machine.MachineManager;
import me.oriharel.machinery.structure.StructureManager;
import me.oriharel.machinery.utilities.SignMenuFactory;
import me.oriharel.machinery.utilities.Utils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

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

        ConsoleCommandSender console = Bukkit.getConsoleSender();
        console.sendMessage("Starting up Machinery");


        Utils.MACHINERY_INSTANCE = this;

        fileManager = new FileManager(this);
        fileManager.getConfig("config.yml").copyDefaults(true).save();
        fileManager.getConfig("fuels.yml").copyDefaults(true).save();
        fileManager.getConfig("upgrades.yml").copyDefaults(true).save();
        fileManager.getConfig("machines.yml").copyDefaults(true).save();
        File file = new File(getDataFolder(), "structures");
        if (!file.exists()) file.mkdir();

        this.signMenuFactory = new SignMenuFactory(this);

        Bukkit.getPluginManager().registerEvents(new Listeners(), this);
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            fuelManager = new FuelManager(this);
        });

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

    public void createSignInput(Player target, BiPredicate<Player, String[]> response, String... defaultLines) {
        this.signMenuFactory
                .newMenu(Lists.newArrayList(defaultLines))
                .reopenIfFail()
                .response(response)
                .open(target);
    }

    private void setupCommandManager() {
        this.commandManager = new BukkitCommandManager(this);
        commandManager.getCommandCompletions().registerCompletion("machines",
                c -> ImmutableList.of(machineManager.getMachines().stream().map(Machine::getMachineName).collect(Collectors.joining())));
        commandManager.getCommandContexts().registerIssuerAwareContext(Machine.class, (c) -> {
            List<String> machineNames = machineManager.getMachines().stream().map(Machine::getMachineName).collect(Collectors.toList());
            String machineName = c.getArgs().stream().filter(machineNames::contains).findFirst().orElse(null);
            if (machineName == null) return null;
            return machineManager.getMachines().stream().filter(machine -> machine.getMachineName().equalsIgnoreCase(machineName)).findFirst().get();
        });
        commandManager.registerCommand(new MachineCommand(this));
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
