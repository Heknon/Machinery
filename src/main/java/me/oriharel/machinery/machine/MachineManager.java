package me.oriharel.machinery.machine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.config.FileManager;
import me.oriharel.machinery.exceptions.*;
import me.oriharel.machinery.serialization.LocationTypeAdapter;
import me.oriharel.machinery.serialization.PlayerMachineTypeAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

public class MachineManager {
    private final Machinery machinery;
    private MachineFactory machineFactory;
    private List<Machine> machines;
    private HashMap<UUID, PlayerMachine> playerMachines;

    public MachineManager(Machinery machinery) {
        this.machinery = machinery;
        this.machineFactory = new MachineFactory(machinery);
        this.machines = new ArrayList<Machine>();
        this.playerMachines = new HashMap<>();
        initializeBaseMachines();
        initializePlayerMachines();
    }

    public PlayerMachine getPlayerMachineByLocation(Location machineLocation) {
        return machineFactory.createMachine(machineLocation);
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
        Set<String> machineLocations = configLoad.getKeys(false);
        for (String loc : machineLocations) {
            String[] split = loc.split("\\|");
            Location location = new Location(
                    Bukkit.getWorld(UUID.fromString(split[3])), Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
            PlayerMachine machine = machineFactory.createMachine(location);
            UUID playerUuid = UUID.fromString(configLoad.getString(loc + ".player"));
            this.playerMachines.put(playerUuid, machine);
        }
    }

    public void registerNewPlayerMachine(UUID uuid, PlayerMachine playerMachine) {
        FileManager.Config config = machinery.getFileManager().getConfig("machine_registry.yml");
        YamlConfiguration configLoad = config.get();
        Location machineLocation = playerMachine.getLocation();
        String configKey =
                machineLocation.getBlockX() + "|" + machineLocation.getBlockY() + "|" + machineLocation.getBlockZ() + "|" + machineLocation.getWorld().getUID().toString();
        ConfigurationSection section = configLoad.createSection(configKey);
        Gson gson =
                new GsonBuilder().setPrettyPrinting().registerTypeHierarchyAdapter(Location.class, new LocationTypeAdapter()).registerTypeHierarchyAdapter(PlayerMachine.class, new PlayerMachineTypeAdapter()).create();
        section.set("machine", gson.toJson(playerMachine, PlayerMachine.class));
        section.set("player", uuid.toString());
        config.save();
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
