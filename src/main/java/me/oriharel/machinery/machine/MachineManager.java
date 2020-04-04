package me.oriharel.machinery.machine;

import com.sun.javaws.exceptions.InvalidArgumentException;
import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.exceptions.MachineNotRegisteredException;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.*;

public class MachineManager {
    private final Machinery machinery;
    private MachineFactory machineFactory;
    private List<Machine> machines;
    private HashMap<UUID, PlayerMachine> playerMachines;

    public MachineManager(Machinery machinery) {
        this.machinery = machinery;
        this.machineFactory = new MachineFactory();
        this.machines = new ArrayList<Machine>();
        this.playerMachines = new HashMap<>();
    }

    private void initializeBaseMachines() {
        FileConfiguration configLoad = machinery.getFileManager().getConfig(new File(machinery.getDataFolder(), "machines.yml")).getFileConfiguration();
        Set<String> machineKeys = configLoad.getKeys(false);
        for (String key : machineKeys) {
            try {
                machines.add(machineFactory.createMachine(key, MachineType.valueOf(configLoad.getString(key + ".type"))));
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    private void initializePlayerMachines() {

    }

    public void registerPlayerMachine(UUID uuid, PlayerMachine playerMachine) {
        this.playerMachines.put(uuid, playerMachine);
    }

    public PlayerMachine getPlayerMachine(UUID uuid) throws MachineNotRegisteredException {
        PlayerMachine playerMachine = this.playerMachines.getOrDefault(uuid, null);
        if (playerMachine == null) throw new MachineNotRegisteredException("This player machine with the uuid given has not been found in the player machine registry " +
                ".");
        return playerMachine;
    }

    public void addMachine(Machine machine) {
        machines.add(machine);
    }

    public void removeMachine(Machine machine) {
        machines.remove(machine);
    }

    public List<Machine> getMachines() {
        return machines;
    }

    public MachineFactory getMachineFactory() {
        return machineFactory;
    }
}
