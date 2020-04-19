package me.oriharel.machinery.serialization;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import me.oriharel.machinery.fuel.PlayerFuel;
import me.oriharel.machinery.machine.Machine;
import me.oriharel.machinery.machine.MachineFactory;
import me.oriharel.machinery.machine.PlayerMachine;
import me.oriharel.machinery.upgrades.AbstractUpgrade;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class PlayerMachineTypeAdapter extends MachineTypeAdapter<PlayerMachine> implements JsonSerializer<PlayerMachine>, JsonDeserializer<PlayerMachine> {

    public PlayerMachineTypeAdapter(MachineFactory factory) {
        super(factory);
    }

    @Override
    protected PlayerMachine getDeserializedMachine(JsonObject machineJsonObject, JsonDeserializationContext context) {
        Machine baseMachine = super.getDeserializedMachine(machineJsonObject, context);

        double zenCoinsGained = machineJsonObject.get("zenCoinsGained").getAsDouble();
        double totalZenCoinsGained = machineJsonObject.get("totalZenCoinsGained").getAsDouble();
        double totalResourcesGained = machineJsonObject.get("totalResourcesGained").getAsDouble();

        Location machineCoreBlockLocation = context.deserialize(machineJsonObject.get("coreBlockLocation"), Location.class);

        UUID ownerUuid = new UUID(machineJsonObject.get("ownerUuidMost").getAsLong(), machineJsonObject.get("ownerUuidLeast").getAsLong());

        List<PlayerFuel> fuels = context.deserialize(machineJsonObject.get("resourcesGained"), new TypeToken<List<PlayerFuel>>() {
        }.getType());
        List<AbstractUpgrade> upgrades = context.deserialize(machineJsonObject.get("resourcesGained"), new TypeToken<List<AbstractUpgrade>>() {
        }.getType());
        List<ItemStack> resourcesGained = context.deserialize(machineJsonObject.get("resourcesGained"), new TypeToken<List<ItemStack>>() {
        }.getType());


        return factory.createMachine(baseMachine, machineCoreBlockLocation, totalResourcesGained, fuels, zenCoinsGained, totalZenCoinsGained, ownerUuid, upgrades,
                resourcesGained);
    }

    @Override
    protected JsonObject getSerializedMachine(PlayerMachine machine, JsonSerializationContext context) {
        JsonObject baseMachineSerialized = super.getSerializedMachine(machine, context);

        baseMachineSerialized.add("zenCoinsGained", new JsonPrimitive(machine.getZenCoinsGained()));
        baseMachineSerialized.add("totalZenCoinsGained", new JsonPrimitive(machine.getTotalZenCoinsGained()));
        baseMachineSerialized.add("totalResourcesGained", new JsonPrimitive(machine.getTotalResourcesGained()));

        baseMachineSerialized.add("ownerUuidMost", new JsonPrimitive(machine.getOwner().getMostSignificantBits()));
        baseMachineSerialized.add("ownerUuidLeast", new JsonPrimitive(machine.getOwner().getLeastSignificantBits()));

        baseMachineSerialized.add("coreBlockLocation", context.serialize(machine, Location.class));

        baseMachineSerialized.add("resourcesGained", context.serialize(machine.getResourcesGained(), new TypeToken<List<ItemStack>>() {
        }.getType()));
        baseMachineSerialized.add("upgrades", context.serialize(machine.getUpgrades(), new TypeToken<List<AbstractUpgrade>>() {
        }.getType()));
        baseMachineSerialized.add("fuels", context.serialize(machine.getFuels(), new TypeToken<List<PlayerFuel>>() {
        }.getType()));

        return baseMachineSerialized;
    }
}
