package me.oriharel.machinery.data;

import com.google.gson.Gson;
import me.oriharel.machinery.machine.MachineFactory;
import me.oriharel.machinery.machine.PlayerMachine;
import me.oriharel.machinery.utilities.Utils;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

public class PlayerMachinePersistentDataType implements PersistentDataType<String, PlayerMachine> {

    private Gson gson;

    public PlayerMachinePersistentDataType(MachineFactory factory) {
        this.gson = Utils.getGsonSerializationBuilderInstance(PlayerMachine.class, factory);
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
