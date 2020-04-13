package me.oriharel.machinery;

import me.oriharel.customrecipes.CustomRecipes;
import me.oriharel.machinery.config.FileManager;
import me.oriharel.machinery.inventory.Listeners;
import me.oriharel.machinery.listeners.Block;
import me.oriharel.machinery.listeners.Interact;
import me.oriharel.machinery.machine.MachineManager;
import me.oriharel.machinery.structure.StructureManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.logging.Level;

public final class Machinery extends JavaPlugin {

    public final static String STRUCTURE_EXTENSION = ".schem";
    private static Machinery INSTANCE;
    private FileManager fileManager;
    private MachineManager machineManager;
    private StructureManager structureManager;

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
        getPlugin(CustomRecipes.class).getRecipesManager().registerRecipesDoneCallback(() -> Bukkit.getScheduler().runTask(this, () -> {
            structureManager = new StructureManager(this);
            structureManager.registerOnDoneCallback(() -> {
                machineManager = new MachineManager(this);
                Bukkit.getServer().getPluginManager().registerEvents(new Block(this), this);
                Bukkit.getServer().getPluginManager().registerEvents(new Interact(this), this);
                Bukkit.getWorlds().forEach(this::loadMachineData);
            });
        }));
    }

    @Override
    public void onDisable() {
        Bukkit.getWorlds().forEach(this::saveMachineData);
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

    public void loadMachineData(World world) {
        Path file = world.getWorldFolder().toPath().resolve("machines.dat");
        getServer().getLogger().log(Level.INFO, "Loading machines from machine.dat for world " + world.getName());
        try (ByteChannel in = Files.newByteChannel(file, StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate(128);
            in.read(buffer);
            buffer.flip();
            LongBuffer longBuffer = buffer.asLongBuffer();
            for (int i = 0; i < longBuffer.capacity(); i++) {
                long position = buffer.getLong(i * 8);
                if (position == 0) continue;
                Location loc = Utils.longToLocation(position, world);
                org.bukkit.block.Block block = loc.getBlock();
                try {
                    Bukkit.getScheduler().runTask(this, () -> machineManager.getMachineCores().put(loc, machineManager.getPlayerMachineFromBlock(block)));
                } catch (ClassCastException ignored) {
                    getLogger().info("ClassCastException at long number " + i + ", byte " + i * 8);
                }
                Bukkit.getScheduler().runTask(this, () -> {
                    Location[] locations;
                    locations = machineManager.getPlayerMachineLocations(block);
                    Arrays.stream(locations).forEach(l -> l.setWorld(world));
                    machineManager.getMachinePartLocations().addAll(Arrays.asList(locations));
                });
            }

            getServer().getLogger().log(Level.INFO, "Loaded all machines from machine.dat for world " + world.getName());
        } catch (IOException ex) {
            if (ex instanceof NoSuchFileException) {
                getServer().getLogger().log(Level.INFO, "Couldn't find machines data 'machines.dat' for world " + world.getName() + ". creating...");
                try {
                    file.toFile().createNewFile();
                    getServer().getLogger().log(Level.INFO, "Created machines.dat for world " + world.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
            ex.printStackTrace();
        }
    }

    public void saveMachineData(World world) {
        Path file = world.getWorldFolder().toPath().resolve("machines.dat");
        if (!file.toFile().exists()) {
            try {
                file.toFile().createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (WritableByteChannel out = Files.newByteChannel(file, StandardOpenOption.WRITE)) {
            FileChannel.open(file);
            ByteBuffer buffer = ByteBuffer.allocate(machineManager.getMachineCores().size() * 8);
            for (Location key : machineManager.getMachineCores().keySet()) {
                if (!key.getWorld().equals(world)) return;
                buffer.put(ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(Utils.locationToLong(key)).array());
            }
            getServer().getLogger().log(Level.INFO, "Writing all machines to machine.dat file for world " + world.getName());
            out.write((ByteBuffer) buffer.flip());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
