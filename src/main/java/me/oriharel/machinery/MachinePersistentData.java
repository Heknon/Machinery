package me.oriharel.machinery;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.oriharel.machinery.machine.Machine;
import me.oriharel.machinery.machine.MachineFactory;
import me.oriharel.machinery.machine.PlayerMachine;
import me.oriharel.machinery.serialization.LocationTypeAdapter;
import me.oriharel.machinery.serialization.PlayerMachineTypeAdapter;
import org.bukkit.Location;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

public class MachinePersistentData implements PersistentDataType<byte[], PlayerMachine> {

    private Gson gson;

    public MachinePersistentData(MachineFactory machineFactory) {
        this.gson = new GsonBuilder().registerTypeHierarchyAdapter(PlayerMachine.class,
                new PlayerMachineTypeAdapter(machineFactory)).registerTypeHierarchyAdapter(Location.class,
                new LocationTypeAdapter()).create();
    }

    @Override
    public Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public Class<PlayerMachine> getComplexType() {
        return PlayerMachine.class;
    }

    @Override
    public byte[] toPrimitive(PlayerMachine playerMachine, PersistentDataAdapterContext persistentDataAdapterContext) {
        return gson.toJson(playerMachine, PlayerMachine.class).getBytes();
    }

    @Override
    public PlayerMachine fromPrimitive(byte[] bytes, PersistentDataAdapterContext persistentDataAdapterContext) {
        String json = new String(bytes);
        return gson.fromJson(json, PlayerMachine.class);
    }
}
