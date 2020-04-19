package me.oriharel.machinery.data;

import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.machine.MachineManager;
import me.oriharel.machinery.machine.PlayerMachine;
import me.oriharel.machinery.utilities.Callback;
import me.oriharel.machinery.utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.channels.ByteChannel;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;

public class MachineDataManager {

    private Machinery machinery;

    public MachineDataManager(Machinery machinery) {
        this.machinery = machinery;
    }

    public void loadMachineData(World world) {
        Machinery machinery = this.machinery;
        MachineManager machineManager = machinery.getMachineManager();


        Path file = world.getWorldFolder().toPath().resolve("machines.dat");
        machinery.getServer().getLogger().log(Level.INFO, "Loading machines from machine.dat for world " + world.getName());
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
                    Bukkit.getScheduler().runTask(machinery, () -> {
                        PlayerMachine machine = machineManager.getPlayerMachineFromBlock(block);
                        machineManager.getMachineCores().put(loc, machine);
                        machine.run().startProcess();
                    });
                } catch (ClassCastException ignored) {
                    machinery.getLogger().info("ClassCastException at long number " + i + ", byte " + i * 8);
                }
                Bukkit.getScheduler().runTask(machinery, () -> {
                    Location[] locations;
                    locations = machineManager.getPlayerMachineLocations(block);
                    Arrays.stream(locations).forEach(l -> l.setWorld(world));
                    machineManager.getMachinePartLocations().addAll(Arrays.asList(locations));
                });
            }
            in.close();
            machinery.getServer().getLogger().log(Level.INFO, "Loaded all machines from machine.dat for world " + world.getName());
        } catch (IOException ex) {
            if (ex instanceof NoSuchFileException) {
                machinery.getServer().getLogger().log(Level.INFO, "Couldn't find machines data 'machines.dat' for world " + world.getName() + ". creating...");
                createFileIfNotExist(file, () -> machinery.getServer().getLogger().log(Level.INFO, "Created machines.dat for world " + world.getName()));
                return;
            }
            ex.printStackTrace();
        }
    }

    public void addMachineCoreLocation(Location location) {
        Bukkit.getScheduler().runTaskAsynchronously(machinery, () -> {
            Path file = location.getWorld().getWorldFolder().toPath().resolve("machines.dat");
            createFileIfNotExist(file);
            try {
                Files.write(file, ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(Utils.locationToLong(location)).array(), StandardOpenOption.APPEND);
                Files.copy(file, file.getParent().resolve("machines.bak.dat"), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    public void removeMachineCoreLocation(Location location) {
        Bukkit.getScheduler().runTaskAsynchronously(machinery, () -> {
            Path file = location.getWorld().getWorldFolder().toPath().resolve("machines.dat");

            long longToFind = Utils.locationToLong(location);
            try (ByteChannel in = Files.newByteChannel(file, StandardOpenOption.READ)) {
                ByteBuffer buffer = ByteBuffer.allocate(128);
                in.read(buffer);
                buffer.flip();
                LongBuffer longBuffer = buffer.asLongBuffer();
                for (int i = 0; i < longBuffer.capacity(); i++) {
                    long position = buffer.getLong(i * 8);
                    if (position != longToFind) continue;
                    longBuffer.put(i * 8, 0);
                    in.close();
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void startMachineSaveDataProcess() {
        BukkitRunnable process = new BukkitRunnable() {
            @Override
            public void run() {
                saveMachinesDataToBlocks();
            }
        };
        process.runTaskTimerAsynchronously(machinery, 0, 20 * 60 * 10);
    }

    public void forceMachineDataSave() {
        saveMachinesDataToBlocks();
    }

    private void saveMachinesDataToBlocks() {
        Machinery machinery = this.machinery;
        MachineManager machineManager = machinery.getMachineManager();
        for (Map.Entry<Location, PlayerMachine> machineEntry : machineManager.getMachineCores().entrySet()) {
            Bukkit.getScheduler().runTask(machinery, () -> machineManager.setPlayerMachineBlock(machineEntry.getKey().getBlock(), machineEntry.getValue()));
        }
    }


    private void createFileIfNotExist(Path file) {
        if (!file.toFile().exists()) {
            try {
                file.toFile().createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createFileIfNotExist(Path file, Callback ifSuccess) {
        if (!file.toFile().exists()) {
            try {
                file.toFile().createNewFile();
                ifSuccess.apply();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
