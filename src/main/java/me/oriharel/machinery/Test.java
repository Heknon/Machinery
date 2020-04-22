package me.oriharel.machinery;

import com.google.gson.Gson;
import me.oriharel.machinery.machine.MachineType;
import me.oriharel.machinery.machine.PlayerMachine;
import me.oriharel.machinery.utilities.Utils;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

public class Test {
    public static void main(String[] args) {
        PlayerMachine machine = new PlayerMachine(1, 1, 1, Collections.singletonList("fuel"), MachineType.MINER, null, null, "name", Material.COMMAND_BLOCK, 0,
                new HashMap<>(), new ArrayList<>(), new Location(null, 0, 0, 0), 0, 0, UUID.randomUUID(), new ArrayList<>(), null);
        Gson gson = Utils.getGsonSerializationBuilderInstance(PlayerMachine.class, null);
    }
}
