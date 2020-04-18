package me.oriharel.machinery.machine;

import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.PlayerMachinePersistentDataType;
import me.oriharel.machinery.exceptions.*;
import me.oriharel.machinery.utilities.Utils;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class MachineManager {
    private final Machinery machinery;
    private MachineFactory machineFactory;
    private List<Machine> machines;
    private HashMap<Location, PlayerMachine> machineCores;
    private HashSet<Location> machinePartLocations;
    private HashSet<Location> temporaryPreRegisterMachineLocations;
    private PlayerMachinePersistentDataType MACHINE_PERSISTENT_DATA_TYPE;
    private NamespacedKey MACHINE_NAMESPACE_KEY;
    private NamespacedKey MACHINE_LOCATIONS_NAMESPACE_KEY;

    public MachineManager(Machinery machinery) {
        this.machinery = machinery;
        this.machineFactory = new MachineFactory(machinery);
        this.machines = new ArrayList<Machine>();
        this.machineCores = new HashMap<>();
        this.machinePartLocations = new HashSet<>();
        this.temporaryPreRegisterMachineLocations = new HashSet<>();
        this.MACHINE_PERSISTENT_DATA_TYPE = new PlayerMachinePersistentDataType(machineFactory);
        this.MACHINE_NAMESPACE_KEY = new NamespacedKey(machinery, "machine");
        this.MACHINE_LOCATIONS_NAMESPACE_KEY = new NamespacedKey(machinery, "machine_locations");
        initializeBaseMachines();
    }

    private void initializeBaseMachines() {
        YamlConfiguration configLoad = machinery.getFileManager().getConfig("machines.yml").get();
        Set<String> machineKeys = configLoad.getKeys(false);
        for (String key : machineKeys) {
            try {
                Machine machine = machineFactory.createMachine(key);
                machines.add(machine);
            } catch (IllegalArgumentException | NotMachineTypeException | MachineNotFoundException | MaterialNotFoundException | RecipeNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void registerNewPlayerMachine(PlayerMachine playerMachine, Set<Location> machineLocations) {
        try {
            setPlayerMachineBlock(playerMachine.getOpenGUIBlockLocation().getBlock(), playerMachine);
            Object[] locations = machineLocations.toArray();
            setPlayerMachineLocations(playerMachine.getOpenGUIBlockLocation().getBlock(), Arrays.copyOf(locations, locations.length, Location[].class));
            this.machineCores.put(playerMachine.getOpenGUIBlockLocation(), playerMachine);
            this.machinePartLocations.addAll(machineLocations);
            Location machineLocation = playerMachine.getOpenGUIBlockLocation();
            if (machineLocation == null) try {
                throw new MachineException("Machine has no open gui block in it's schematic!");
            } catch (MachineException e) {
                e.printStackTrace();
            }
            machinery.getMachineDataManager().addMachineCoreLocation(machineLocation);
            playerMachine.run().startProcess();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void unregisterPlayerMachine(PlayerMachine machine) {
        machineCores.remove(machine.getOpenGUIBlockLocation());
        Location[] playerMachinePartLocations = getPlayerMachineLocations(machine.getOpenGUIBlockLocation().getBlock());
        machinePartLocations.removeAll(Arrays.asList(playerMachinePartLocations));
        machinery.getMachineDataManager().removeMachineCoreLocation(machine.getOpenGUIBlockLocation());
    }

    public HashMap<Location, PlayerMachine> getMachineCores() {
        return machineCores;
    }

    public void setPlayerMachineBlock(Block block, PlayerMachine playerMachine) {
        TileState tileState = (TileState) block.getState();
        PersistentDataContainer persistentDataContainer = tileState.getPersistentDataContainer();
        persistentDataContainer.set(MACHINE_NAMESPACE_KEY, MACHINE_PERSISTENT_DATA_TYPE, playerMachine);
        tileState.update();
    }

    public PlayerMachine getPlayerMachineFromBlock(Block block) {
        TileState tileState = (TileState) block.getState();
        PersistentDataContainer persistentDataContainer = tileState.getPersistentDataContainer();
        return persistentDataContainer.get(MACHINE_NAMESPACE_KEY, MACHINE_PERSISTENT_DATA_TYPE);
    }

    public void setPlayerMachineLocations(Block block, Location[] locations) {
        TileState tileState = (TileState) block.getState();
        PersistentDataContainer persistentDataContainer = tileState.getPersistentDataContainer();
        long[] arr =
                ArrayUtils.toPrimitive(Arrays.stream(locations).map(Utils::locationToLong).toArray(Long[]::new));
        persistentDataContainer.set(MACHINE_LOCATIONS_NAMESPACE_KEY, PersistentDataType.LONG_ARRAY, arr);
        tileState.update();
    }

    public Location[] getPlayerMachineLocations(Block block) {
        TileState tileState = (TileState) block.getState();
        PersistentDataContainer persistentDataContainer = tileState.getPersistentDataContainer();
        return Arrays.stream(persistentDataContainer.get(MACHINE_LOCATIONS_NAMESPACE_KEY, PersistentDataType.LONG_ARRAY)).mapToObj(packed -> Utils.longToLocation(packed, block.getWorld())).toArray(Location[]::new);
    }

    protected void clearMachineTileStateDataFromBlock(Block block) {
        TileState tileState = (TileState) block.getState();
        PersistentDataContainer persistentDataContainer = tileState.getPersistentDataContainer();
        persistentDataContainer.remove(MACHINE_LOCATIONS_NAMESPACE_KEY);
        persistentDataContainer.remove(MACHINE_NAMESPACE_KEY);
    }

    public HashSet<Location> getMachinePartLocations() {
        return machinePartLocations;
    }

    public HashSet<Location> getTemporaryPreRegisterMachineLocations() {
        return temporaryPreRegisterMachineLocations;
    }

    public void addTemporaryPreRegisterMachinePartLocations(List<Location> locations) {
        temporaryPreRegisterMachineLocations.addAll(locations);
    }

    public List<Machine> getMachines() {
        return machines;
    }

    public MachineFactory getMachineFactory() {
        return machineFactory;
    }
}
