package me.oriharel.machinery.serialization;

import com.google.gson.*;
import me.oriharel.machinery.fuel.PlayerFuel;
import me.oriharel.machinery.utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.lang.reflect.Type;
import java.util.UUID;

public class PlayerFuelTypeAdapter implements JsonSerializer<PlayerFuel>, JsonDeserializer<PlayerFuel> {
    @Override
    public PlayerFuel deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject obj = jsonElement.getAsJsonObject();
        return new PlayerFuel(obj.get("name").getAsString(), Material.getMaterial(obj.get("type").getAsString()), obj.get("nbtId").getAsString(),
                obj.get("energy").getAsInt(), obj.get("amount").getAsInt());
    }

    @Override
    public JsonElement serialize(PlayerFuel playerFuel, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject obj = new JsonObject();
        obj.add("type", new JsonPrimitive(playerFuel.getType().toString()));
        obj.add("name", new JsonPrimitive(playerFuel.getName()));
        obj.add("nbtId", new JsonPrimitive(playerFuel.getNbtId()));
        obj.add("energy", new JsonPrimitive(playerFuel.getEnergy()));
        obj.add("amount", new JsonPrimitive(playerFuel.getAmount()));
        return obj;
    }
}
