package me.oriharel.machinery.machine;

import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.api.events.PostMachineBuildEvent;
import me.oriharel.machinery.api.events.PreMachineBuildEvent;
import me.oriharel.machinery.data.PlayerMachinePersistentDataType;
import me.oriharel.machinery.exceptions.*;
import me.oriharel.machinery.message.Message;
import me.oriharel.machinery.upgrades.LootBonusUpgrade;
import me.oriharel.machinery.upgrades.SpeedUpgrade;
import me.oriharel.machinery.utilities.Utils;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
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
    private Map<String, ResourceMap> machineResourceTrees;

    public MachineManager(Machinery machinery) {
        this.machinery = machinery;
        this.machineFactory = new MachineFactory(machinery);
        this.machines = new ArrayList<>();
        this.machineCores = new HashMap<>();
        this.machinePartLocations = new HashSet<>();
        this.machineResourceTrees = new HashMap<>();
        this.temporaryPreRegisterMachineLocations = new HashSet<>();
        this.MACHINE_PERSISTENT_DATA_TYPE = new PlayerMachinePersistentDataType(machineFactory);
        this.MACHINE_NAMESPACE_KEY = new NamespacedKey(machinery, "machine");
        this.MACHINE_LOCATIONS_NAMESPACE_KEY = new NamespacedKey(machinery, "machine_locations");
        initializeBaseMachines();
    }

    /**
     * initialize all base machine types
     */
    private void initializeBaseMachines() {
        YamlConfiguration configLoad = machinery.getFileManager().getConfig("machines.yml").get();
        Set<String> machineKeys = configLoad.getKeys(false);
        for (String key : machineKeys) {
            try {
                Machine machine = machineFactory.createMachine(key);
                machineResourceTrees.put(key, new ResourceMap(key, configLoad));
                machines.add(machine);
            } catch (IllegalArgumentException | NotMachineTypeException | MachineNotFoundException | MaterialNotFoundException | RecipeNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * registers a new machine to core machine block and to plugin caches
     * @param playerMachine the machine to register
     * @param machineLocations the locations of the machine in the world
     */
    public void registerNewPlayerMachine(PlayerMachine playerMachine, Set<Location> machineLocations) {
        try {
            setPlayerMachineBlock(playerMachine.getMachineCore().getBlock(), playerMachine);
            Object[] locations = machineLocations.toArray();
            setPlayerMachineLocations(playerMachine.getMachineCore().getBlock(), Arrays.copyOf(locations, locations.length, Location[].class));
            this.machineCores.put(playerMachine.getMachineCore(), playerMachine);
            this.machinePartLocations.addAll(machineLocations);
            Location machineLocation = playerMachine.getMachineCore();
            if (machineLocation == null) try {
                throw new MachineException("Machine has no open gui block in it's schematic!");
            } catch (MachineException e) {
                e.printStackTrace();
            }
            machinery.getMachineDataManager().addMachineCoreLocation(machineLocation);
            playerMachine.getMinerProcess().startProcess();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * removes player machine data from all plugin caches
     * @param machine the machine to remove
     */
    protected void unregisterPlayerMachine(PlayerMachine machine) {
        machineCores.remove(machine.getMachineCore());
        Location[] playerMachinePartLocations = getPlayerMachineLocations(machine.getMachineCore().getBlock());
        machinePartLocations.removeAll(Arrays.asList(playerMachinePartLocations));
        machinery.getMachineDataManager().removeMachineCoreLocation(machine.getMachineCore());
    }

    public HashMap<Location, PlayerMachine> getMachineCores() {
        return machineCores;
    }

    /**
     * sets machine data to a TileState block
     * will throw an exception if block type doesn't extend TileState
     * @param block the machine core
     * @param playerMachine the machine data to set
     */
    public void setPlayerMachineBlock(Block block, PlayerMachine playerMachine) {
        TileState tileState = (TileState) block.getState();
        PersistentDataContainer persistentDataContainer = tileState.getPersistentDataContainer();
        persistentDataContainer.set(MACHINE_NAMESPACE_KEY, MACHINE_PERSISTENT_DATA_TYPE, playerMachine);
        tileState.update();
    }

    /**
     * gets a player machine from a block storing the machine data
     * @param block the machine core
     * @return the PlayerMachine if data found, otherwise, null
     */
    @Nullable
    public PlayerMachine getPlayerMachineFromBlock(Block block) {
        try {
            TileState tileState = (TileState) block.getState();
            PersistentDataContainer persistentDataContainer = tileState.getPersistentDataContainer();
            return persistentDataContainer.get(MACHINE_NAMESPACE_KEY, MACHINE_PERSISTENT_DATA_TYPE);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * sets the locations belonging to the machine's entir build
     * @param block the core block of the machine
     * @param locations the locations belonging to the machine
     */
    public void setPlayerMachineLocations(Block block, Location[] locations) {
        TileState tileState = (TileState) block.getState();
        PersistentDataContainer persistentDataContainer = tileState.getPersistentDataContainer();
        long[] arr =
                ArrayUtils.toPrimitive(Arrays.stream(locations).map(Utils::locationToLong).toArray(Long[]::new));
        persistentDataContainer.set(MACHINE_LOCATIONS_NAMESPACE_KEY, PersistentDataType.LONG_ARRAY, arr);
        tileState.update();
    }

    /**
     * get from a machine block it's other parts in the world.
     * Ex. Used to stop players from breaking the machine
     * @param block the machine core block
     * @return all the locations belonging to the machine
     */
    public Location[] getPlayerMachineLocations(Block block) {
        TileState tileState = (TileState) block.getState();
        PersistentDataContainer persistentDataContainer = tileState.getPersistentDataContainer();
        return Arrays.stream(persistentDataContainer.get(MACHINE_LOCATIONS_NAMESPACE_KEY, PersistentDataType.LONG_ARRAY)).mapToObj(packed -> Utils.longToLocation(packed, block.getWorld())).toArray(Location[]::new);
    }

    /**
     * Removes all plugin made data from a tilestate
     * Made since if a block is air and has TileState data bad stuff will happen
     * @param block the block to remove data from
     */
    protected void clearMachineTileStateDataFromBlock(Block block) {
        TileState tileState = (TileState) block.getState();
        PersistentDataContainer persistentDataContainer = tileState.getPersistentDataContainer();
        persistentDataContainer.remove(MACHINE_LOCATIONS_NAMESPACE_KEY);
        persistentDataContainer.remove(MACHINE_NAMESPACE_KEY);
    }

    /**
     * Further abstract the process of building and registering a new machine
     * @param playerUuid uuid of machine owner
     * @param machine the machine to create
     * @param buildLocation the location to build it in
     * @param <T> the type of the machine
     * @return whether the build was successful or not
     */
    public <T extends Machine> boolean buildMachine(UUID playerUuid, T machine, Location buildLocation) {
        try {
            PreMachineBuildEvent preMachineBuildEvent = new PreMachineBuildEvent(machine, buildLocation);
            Bukkit.getPluginManager().callEvent(preMachineBuildEvent);
            if (preMachineBuildEvent.isCancelled()) return false;
            Player p = Bukkit.getPlayer(playerUuid);
            List<Location> locations = machine.structure.build(buildLocation, p, machine.machineCoreBlockType, (printResult) -> {
                PlayerMachine machineToRegister;
                if (machine instanceof PlayerMachine) {
                    PlayerMachine pMachine = (PlayerMachine) machine;
                    machineToRegister = machineFactory.createMachine(machine, printResult.getOpenGUIBlockLocation(), pMachine.getTotalResourcesGained(),
                            pMachine.getEnergyInMachine(), pMachine.getZenCoinsGained(), pMachine.getTotalZenCoinsGained(), pMachine.getOwner(), pMachine.getUpgrades(),
                            pMachine.getResourcesGained(), pMachine.getPlayersWithAccessPermission());
                } else {
                    machineToRegister = Machinery.getInstance().getMachineManager().getMachineFactory().createMachine(machine,
                            printResult.getOpenGUIBlockLocation(), 0, 0, 0, 0, playerUuid, Arrays.asList(
                                    new LootBonusUpgrade(1),
                                    new SpeedUpgrade(1)
                            ), new HashMap<>(), Collections.singleton(playerUuid));
                }
                registerNewPlayerMachine(machineToRegister, new HashSet<>(printResult.getPlacementLocations()));
                PostMachineBuildEvent postMachineBuildEvent = new PostMachineBuildEvent(machineToRegister, buildLocation);
                Bukkit.getPluginManager().callEvent(postMachineBuildEvent);
                return true;
            });
            if (locations == null) {
                new Message("messages.yml", "not_empty_place", p).send();
                return false;
            }
            Machinery.getInstance().getMachineManager().addTemporaryPreRegisterMachinePartLocations(locations);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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

    public Map<String, ResourceMap> getMachineResourceTrees() {
        return machineResourceTrees;
    }
}
