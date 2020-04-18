package me.oriharel.machinery;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.oriharel.machinery.machine.MachineFactory;
import me.oriharel.machinery.machine.PlayerMachine;
import me.oriharel.machinery.serialization.AbstractUpgradeTypeAdapter;
import me.oriharel.machinery.serialization.LocationTypeAdapter;
import me.oriharel.machinery.serialization.PlayerMachineTypeAdapter;
import me.oriharel.machinery.upgrades.AbstractUpgrade;
import org.bukkit.Location;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

public class PlayerMachinePersistentDataType implements PersistentDataType<String, PlayerMachine> {

    private Gson gson;

    public PlayerMachinePersistentDataType(MachineFactory machineFactory) {
        this.gson = new GsonBuilder().registerTypeHierarchyAdapter(PlayerMachine.class,
                new PlayerMachineTypeAdapter(machineFactory)).registerTypeHierarchyAdapter(Location.class,
                new LocationTypeAdapter()).registerTypeHierarchyAdapter(AbstractUpgrade.class, new AbstractUpgradeTypeAdapter()).create();
    }

    @Override
    public Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public Class<PlayerMachine> getComplexType() {
        return PlayerMachine.class;
    }

    @Override
    public String toPrimitive(PlayerMachine playerMachine, PersistentDataAdapterContext persistentDataAdapterContext) {
        return gson.toJson(playerMachine, PlayerMachine.class);
    }

    @Override
    public PlayerMachine fromPrimitive(String string, PersistentDataAdapterContext persistentDataAdapterContext) {
        return gson.fromJson(string, PlayerMachine.class);
    }
}
