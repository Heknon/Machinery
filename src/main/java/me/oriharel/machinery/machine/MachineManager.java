package me.oriharel.machinery.machine;

import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.exceptions.MachineNotRegisteredException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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
