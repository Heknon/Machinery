package me.oriharel.machinery.machine;

import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.PlayerMachinePersistentDataType;
import me.oriharel.machinery.config.FileManager;
import me.oriharel.machinery.exceptions.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class MachineManager {
    private final Machinery machinery;
    private MachineFactory machineFactory;
    private List<Machine> machines;
    private List<Location> registeredPlayerMachines;
    private PlayerMachinePersistentDataType MACHINE_PERSISTENT_DATA_TYPE;
    private NamespacedKey MACHINE_NAMESPACE_KEY;
    private NamespacedKey MACHINE_LOCATION_NAMESPACE_KEY;

    public MachineManager(Machinery machinery) {
        this.machinery = machinery;
        this.machineFactory = new MachineFactory(machinery);
        this.machines = new ArrayList<Machine>();
        this.registeredPlayerMachines = new ArrayList<>();
        this.MACHINE_PERSISTENT_DATA_TYPE = new PlayerMachinePersistentDataType(machineFactory);
        this.MACHINE_LOCATION_NAMESPACE_KEY = new NamespacedKey(machinery, "machine_location");
        this.MACHINE_NAMESPACE_KEY = new NamespacedKey(machinery, "machine");
        initializeBaseMachines();
        initializePlayerMachines();
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

    private void initializePlayerMachines() {
        YamlConfiguration configLoad = machinery.getFileManager().getConfig("machine_registry.yml").get();
        List<String> machinesLocations = configLoad.getStringList("locations");
        for (String loc : machinesLocations) {
            String[] split = loc.split("\\|");
            Location location = locationFromLong(Long.parseLong(split[0]), Bukkit.getWorld(UUID.fromString(split[1])));
            PlayerMachine machine = getPlayerMachineFromBlock(location.getBlock());
            registeredPlayerMachines.add(machine.getOpenGUIBlockLocation());
        }
    }

    public void registerNewPlayerMachine(PlayerMachine playerMachine, Set<Location> machineLocations) {
        setPlayerMachineBlock(playerMachine.getOpenGUIBlockLocation().getBlock(), playerMachine);
        setAsMachineLocation((Block[]) machineLocations.stream().map(Location::getBlock).toArray());
        registeredPlayerMachines.add(playerMachine.getOpenGUIBlockLocation());
        FileManager.Config config = machinery.getFileManager().getConfig("machine_registry.yml");
        YamlConfiguration configLoad = config.get();
        Location machineLocation = playerMachine.getOpenGUIBlockLocation();
        if (machineLocation == null) try {
            throw new MachineException("Machine has no open gui block in it's schematic!");
        } catch (MachineException e) {
            e.printStackTrace();
        }
        String loc =
                locationToLong(machineLocation) + "|" + machineLocation.getWorld().getUID().toString();
        List<String> locations = configLoad.getStringList("locations");
        locations.add(loc);
        configLoad.set("locations", locations);
        config.save();
    }

    public List<PlayerMachine> getRegisteredPlayerMachines() {
        return this.registeredPlayerMachines.stream().map(loc -> getPlayerMachineFromBlock(loc.getBlock())).collect(Collectors.toList());
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

    public void setAsMachineLocation(Block[] blocks) {
        for (Block block : blocks) {
            TileState tileState = (TileState) block.getState();
            PersistentDataContainer persistentDataContainer = tileState.getPersistentDataContainer();
            persistentDataContainer.set(MACHINE_LOCATION_NAMESPACE_KEY, PersistentDataType.STRING, "machine_location");
            tileState.update();
        }
    }

    public boolean isMachineLocation(Block block) {
        TileState tileState = (TileState) block.getState();
        PersistentDataContainer persistentDataContainer = tileState.getPersistentDataContainer();
        String data = persistentDataContainer.get(MACHINE_LOCATION_NAMESPACE_KEY, PersistentDataType.STRING);
        return data != null && data.equals("machine_locations");
    }

    private Location locationFromLong(long packed, World world) {
        return new Location(world, (int) ((packed << 37) >> 37), (int) (packed >>> 54), (int) ((packed << 10) >> 37));
    }

    private long locationToLong(Location loc) {
        return ((long) loc.getX() & 0x7FFFFFF) | (((long) loc.getZ() & 0x7FFFFFF) << 27) | ((long) loc.getY() << 54);
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
