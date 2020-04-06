package me.oriharel.machinery.serialization;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Type;
import java.util.UUID;

public class LocationTypeAdapter implements JsonSerializer<Location>, JsonDeserializer<Location> {
    @Override
    public Location deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject obj = jsonElement.getAsJsonObject();
        double x = obj.get("x").getAsDouble();
        double y = obj.get("y").getAsDouble();
        double z = obj.get("z").getAsDouble();
        float pitch = obj.get("x").getAsFloat();
        float yaw = obj.get("x").getAsFloat();
        World world = Bukkit.getWorld(UUID.fromString(obj.get("world").getAsString()));
        return new Location(world, x, y, z, yaw, pitch);
    }

    @Override
    public JsonElement serialize(Location location, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject obj = new JsonObject();
        obj.add("y", new JsonPrimitive(location.getY()));
        obj.add("x", new JsonPrimitive(location.getX()));
        obj.add("z", new JsonPrimitive(location.getZ()));
        obj.add("yaw", new JsonPrimitive(location.getYaw()));
        obj.add("pitch", new JsonPrimitive(location.getPitch()));
        obj.add("world", new JsonPrimitive(location.getWorld().getUID().toString()));
        return obj;
    }
}
