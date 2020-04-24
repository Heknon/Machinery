package me.oriharel.machinery.data;

import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.machine.MachineManager;
import me.oriharel.machinery.machine.PlayerMachine;
import me.oriharel.machinery.utilities.Callback;
import me.oriharel.machinery.utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.TileState;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.channels.ByteChannel;
import java.nio.file.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class MachineDataManager {

    private Machinery machinery;

    public MachineDataManager(Machinery machinery) {
        this.machinery = machinery;
    }

    public void loadMachineData(World world) {
        Machinery machinery = this.machinery;
        MachineManager machineManager = machinery.getMachineManager();

        Path worldFolder = world.getWorldFolder().toPath();
        Path data = worldFolder.resolve("machines.dat");
        Path dataBackup = worldFolder.resolve("machines.bak.dat");
        if (Files.exists(dataBackup) && !Files.exists(data)) {
            makeBackup(data);
        }
        Set<Long> locationsToRemove = new HashSet<>();
        machinery.getServer().getLogger().log(Level.INFO, "Loading machines from machine.dat for world " + world.getName());
        try (ByteChannel in = Files.newByteChannel(data, StandardOpenOption.READ)) {
            byte[] dataLocations = Files.readAllBytes(data);
            ByteBuffer buffer = ByteBuffer.wrap(dataLocations);
            in.read(buffer);
            buffer.flip();
            LongBuffer longBuffer = buffer.asLongBuffer();
            for (int i = 0; i < longBuffer.capacity(); i++) {
                long position = buffer.getLong(i * 8);
                if (position == 0) continue;
                Location loc = Utils.longToLocation(position, world);
                org.bukkit.block.Block block = loc.getBlock();
                try {
                    if (!(block.getState() instanceof TileState)) throw new ClassCastException();
                    PlayerMachine machine = machineManager.getPlayerMachineFromBlock(block);
                    if (machine == null) throw new ClassCastException();
                    machineManager.getMachineCores().put(loc, machine);

                    Location[] locations;
                    locations = machineManager.getPlayerMachineLocations(block);
                    Arrays.stream(locations).forEach(l -> l.setWorld(world));
                    machineManager.getMachinePartLocations().addAll(Arrays.asList(locations));

                    Bukkit.getScheduler().runTaskLater(machinery, () -> machine.getMinerProcess().startProcess(), 40);

                } catch (ClassCastException e) {
                    locationsToRemove.add(position);
                    try {
                        in.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

            }
            if (locationsToRemove.size() != 0) {
                removeMachineCoreLocations(locationsToRemove, world);
                makeBackup(data);
                machinery.getLogger().severe("Removed " + locationsToRemove.size() + " locations from the machines.dat file since they were not machines");
            }
            in.close();
            machinery.getServer().getLogger().log(Level.INFO, "Loaded all machines from machine.dat for world " + world.getName());
        } catch (IOException ex) {
            if (ex instanceof NoSuchFileException) {
                machinery.getServer().getLogger().log(Level.INFO, "Couldn't find machines data 'machines.dat' for world " + world.getName() + ". creating...");
                createFileIfNotExist(data, () -> machinery.getServer().getLogger().log(Level.INFO, "Created machines.dat for world " + world.getName()));
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

        Path file = location.getWorld().getWorldFolder().toPath().resolve("machines.dat");

        long longToFind = Utils.locationToLong(location);
        try {
            byte[] bytes = Files.readAllBytes(file);
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            LongBuffer longBuffer = buffer.asLongBuffer();
            for (int i = 0; i < longBuffer.capacity(); i++) {
                long position = buffer.getLong(i * 8);
                if (position != longToFind) continue;
                buffer.putLong(i * 8, 0);
                Files.write(file, buffer.array(), StandardOpenOption.WRITE);
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void removeMachineCoreLocations(Set<Long> locations, World world) {

        Path file = world.getWorldFolder().toPath().resolve("machines.dat");

        try {
            byte[] bytes = Files.readAllBytes(file);
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            LongBuffer longBuffer = buffer.asLongBuffer();
            for (int i = 0; i < longBuffer.capacity(); i++) {
                long position = buffer.getLong(i * 8);
                if (!locations.contains(position)) continue;
                buffer.putLong(i * 8, 0);
            }
            Files.write(file, buffer.array(), StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void startMachineSaveDataProcess() {
        BukkitRunnable process = new BukkitRunnable() {
            @Override
            public void run() {
                saveMachinesDataToBlocks();
            }
        };
        process.runTaskTimer(machinery, 0, 20 * 60 * 10);
    }

    public void forceMachineDataSave() {
        saveMachinesDataToBlocks();
    }

    private void saveMachinesDataToBlocks() {
        MachineManager machineManager = machinery.getMachineManager();
        for (Map.Entry<Location, PlayerMachine> machineEntry : machineManager.getMachineCores().entrySet()) {
            machinery.updateMachineBlock(machineEntry.getValue(), false);
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

    private void makeBackup(Path file) {
        try {
            Files.copy(file, file.getParent().resolve("machines.bak.dat"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
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
