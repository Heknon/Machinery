package me.oriharel.machinery.machine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.oriharel.machinery.PlayerMachinePersistentData;
import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.config.FileManager;
import me.oriharel.machinery.exceptions.*;
import me.oriharel.machinery.serialization.LocationTypeAdapter;
import me.oriharel.machinery.serialization.PlayerMachineTypeAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_15_R1.persistence.CraftPersistentDataContainer;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.*;

public class MachineManager {
    private final Machinery machinery;
    private MachineFactory machineFactory;
    private List<Machine> machines;
    private HashMap<UUID, Set<PlayerMachine>> playerMachines;
    private HashSet<Location> machineLocations; // used for constant look up for events. This includes every block the machine is made up of
    private HashMap<Location, PlayerMachine> locationPlayerMachineMap; // used for constant look up for events. Location is the machine gui opener
    private PlayerMachinePersistentData MACHINE_PERSISTENT_DATA;
    private NamespacedKey NAMESPACED_KEY;

    public MachineManager(Machinery machinery) {
        this.machinery = machinery;
        this.machineFactory = new MachineFactory(machinery);
        this.machines = new ArrayList<Machine>();
        this.locationPlayerMachineMap = new HashMap<>();
        this.playerMachines = new HashMap<UUID, Set<PlayerMachine>>();
        this.machineLocations = new HashSet<Location>();
        this.MACHINE_PERSISTENT_DATA = new PlayerMachinePersistentData(machineFactory);
        this.NAMESPACED_KEY = new NamespacedKey(machinery, "machine");
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

    public void registerMachineLocations(List<Location> locations) {
        Bukkit.getScheduler().runTaskAsynchronously(machinery, () -> this.machineLocations.addAll(locations));
    }

    private void initializePlayerMachines() {
        YamlConfiguration configLoad = machinery.getFileManager().getConfig("machine_registry.yml").get();
        Set<String> machineLocations = configLoad.getKeys(false);
        for (String loc : machineLocations) {
            String[] split = loc.split("\\|");
            Location location = new Location(
                    Bukkit.getWorld(UUID.fromString(split[3])), Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
            PlayerMachine machine = machineFactory.createMachine(location);
            UUID playerUuid = UUID.fromString(configLoad.getString(loc + ".player"));
            if (!this.playerMachines.containsKey(playerUuid)) this.playerMachines.put(playerUuid, new HashSet<>());
            this.playerMachines.get(playerUuid).add(machine);
            this.machineLocations.addAll(machine.getBlockLocations());
            this.locationPlayerMachineMap.put(machine.getOpenGUIBlockLocation(), machine);
        }
    }

    public void registerNewPlayerMachine(UUID uuid, PlayerMachine playerMachine) {
        this.locationPlayerMachineMap.put(playerMachine.getOpenGUIBlockLocation(), playerMachine);
        Bukkit.getScheduler().runTaskAsynchronously(machinery, () -> {
            FileManager.Config config = machinery.getFileManager().getConfig("machine_registry.yml");
            YamlConfiguration configLoad = config.get();
            Location machineLocation = playerMachine.getReferenceBlockLocation();
            if (machineLocation == null) try {
                throw new MachineException("Machine has no reference block in it's schematic!");
            } catch (MachineException e) {
                e.printStackTrace();
            }
            String configKey =
                    machineLocation.getBlockX() + "|" + machineLocation.getBlockY() + "|" + machineLocation.getBlockZ() + "|" + machineLocation.getWorld().getUID().toString();
            ConfigurationSection section = configLoad.createSection(configKey);
            Gson gson =
                    new GsonBuilder().registerTypeHierarchyAdapter(Location.class, new LocationTypeAdapter()).registerTypeHierarchyAdapter(PlayerMachine.class,
                            new PlayerMachineTypeAdapter(getMachineFactory())).create();
            Block openGUIBlock = playerMachine.getOpenGUIBlockLocation().getBlock();
            String machineJson = gson.toJson(playerMachine, PlayerMachine.class);

            Bukkit.getScheduler().runTask(machinery, () -> {
                setPlayerMachineBlock(openGUIBlock, playerMachine);
                System.out.println("DATA: " + getPlayerMachineFromBlock(openGUIBlock));
            });
            section.set("machine", machineJson);
            section.set("player", uuid.toString());
            config.save();
            if (!this.playerMachines.containsKey(uuid)) this.playerMachines.put(uuid, new HashSet<>());
            this.playerMachines.get(uuid).add(playerMachine);
        });
    }

    public Set<PlayerMachine> getPlayerMachines(UUID uuid) throws MachineNotRegisteredException {
        Set<PlayerMachine> playerMachines = this.playerMachines.getOrDefault(uuid, null);
        if (playerMachines == null) throw new MachineNotRegisteredException("This player machine with the uuid given has not been found in the player machine registry " +
                ".");
        return playerMachines;
    }

    public void setPlayerMachineBlock(Block block, PlayerMachine playerMachine) {
        TileState tileState = (TileState) block.getState();
        PersistentDataContainer persistentDataContainer = tileState.getPersistentDataContainer();
        persistentDataContainer.set(NAMESPACED_KEY, MACHINE_PERSISTENT_DATA, playerMachine);
        tileState.update();
    }

    public PlayerMachine getPlayerMachineFromBlock(Block block) {
        TileState tileState = (TileState) block.getState();
        PersistentDataContainer persistentDataContainer = tileState.getPersistentDataContainer();
        System.out.println(((CraftPersistentDataContainer) persistentDataContainer).getRaw());
        return persistentDataContainer.get(NAMESPACED_KEY, MACHINE_PERSISTENT_DATA);
    }

    public HashSet<Location> getMachineLocations() {
        return machineLocations;
    }

    public HashMap<Location, PlayerMachine> getLocationPlayerMachineMap() {
        return locationPlayerMachineMap;
    }

    public HashMap<UUID, Set<PlayerMachine>> getPlayerMachines() {
        return playerMachines;
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
