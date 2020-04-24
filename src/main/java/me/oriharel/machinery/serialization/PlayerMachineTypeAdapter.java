package me.oriharel.machinery.serialization;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import me.oriharel.machinery.machine.Machine;
import me.oriharel.machinery.machine.MachineFactory;
import me.oriharel.machinery.machine.PlayerMachine;
import me.oriharel.machinery.upgrades.AbstractUpgrade;
import me.oriharel.machinery.utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PlayerMachineTypeAdapter extends MachineTypeAdapter<PlayerMachine> implements JsonSerializer<PlayerMachine>, JsonDeserializer<PlayerMachine> {

    public PlayerMachineTypeAdapter(MachineFactory factory) {
        super(factory);
    }

    @Override
    protected PlayerMachine getDeserializedMachine(JsonObject machineJsonObject, JsonDeserializationContext context) {
        Machine baseMachine = super.getDeserializedMachine(machineJsonObject, context);

        int energyInMachine = machineJsonObject.get("energyInMachine").getAsInt();

        double zenCoinsGained = machineJsonObject.get("zenCoinsGained").getAsDouble();
        double totalZenCoinsGained = machineJsonObject.get("totalZenCoinsGained").getAsDouble();
        double totalResourcesGained = machineJsonObject.get("totalResourcesGained").getAsDouble();

        JsonObject locObj = machineJsonObject.get("coreBlockLocation").getAsJsonObject();
        Location machineCoreBlockLocation = Utils.longToLocation(locObj.get("xyz").getAsLong(),
                Bukkit.getWorld(UUID.fromString(locObj.get("world").getAsString())));

        UUID ownerUuid = new UUID(machineJsonObject.get("ownerUuidMost").getAsLong(), machineJsonObject.get("ownerUuidLeast").getAsLong());

        List<AbstractUpgrade> upgrades = context.deserialize(machineJsonObject.get("upgrades"), new TypeToken<List<AbstractUpgrade>>() {
        }.getType());
        HashMap<Material, ItemStack> resourcesGained = context.deserialize(machineJsonObject.get("resourcesGained"), new TypeToken<HashMap<Material, ItemStack>>() {
        }.getType());
        Set<UUID> playersWithAccessPermission = context.deserialize(machineJsonObject.get("playersWithAccessPermission"), new TypeToken<Set<UUID>>() {
        }.getType());


        return factory.createMachine(baseMachine, machineCoreBlockLocation, totalResourcesGained, energyInMachine, zenCoinsGained, totalZenCoinsGained, ownerUuid,
                upgrades, resourcesGained, playersWithAccessPermission);
    }

    @Override
    protected JsonObject getSerializedMachine(PlayerMachine machine, JsonSerializationContext context) {
        JsonObject baseMachineSerialized = super.getSerializedMachine(machine, context);

        baseMachineSerialized.add("zenCoinsGained", new JsonPrimitive(machine.getZenCoinsGained()));
        baseMachineSerialized.add("totalZenCoinsGained", new JsonPrimitive(machine.getTotalZenCoinsGained()));
        baseMachineSerialized.add("totalResourcesGained", new JsonPrimitive(machine.getTotalResourcesGained()));
        baseMachineSerialized.add("energyInMachine", new JsonPrimitive(machine.getEnergyInMachine()));

        baseMachineSerialized.add("ownerUuidMost", new JsonPrimitive(machine.getOwner().getMostSignificantBits()));
        baseMachineSerialized.add("ownerUuidLeast", new JsonPrimitive(machine.getOwner().getLeastSignificantBits()));

        JsonObject locObj = new JsonObject();
        locObj.add("xyz", new JsonPrimitive(Utils.locationToLong(machine.getMachineCore())));
        locObj.add("world", new JsonPrimitive(machine.getMachineCore().getWorld().getUID().toString()));
        baseMachineSerialized.add("coreBlockLocation", locObj);

        baseMachineSerialized.add("resourcesGained", context.serialize(machine.getResourcesGained(), new TypeToken<HashMap<Material, ItemStack>>() {
        }.getType()));
        baseMachineSerialized.add("upgrades", context.serialize(machine.getUpgrades(), new TypeToken<List<AbstractUpgrade>>() {
        }.getType()));
        baseMachineSerialized.add("playersWithAccessPermission", context.serialize(machine.getPlayersWithAccessPermission(), new TypeToken<Set<UUID>>() {
        }.getType()));

        return baseMachineSerialized;
    }
}
