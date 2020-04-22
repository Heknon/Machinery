package me.oriharel.machinery.serialization;

import com.google.gson.*;
import me.oriharel.machinery.utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Type;
import java.util.UUID;

public class LocationTypeAdapter implements JsonSerializer<Location>, JsonDeserializer<Location> {
    @Override
    public Location deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject obj = jsonElement.getAsJsonObject();

        float pitch = obj.get("pitch").getAsFloat();
        float yaw = obj.get("yaw").getAsFloat();

        Location xyzLoc = Utils.longToLocation(obj.get("xyz").getAsLong(), Bukkit.getWorld(UUID.fromString(obj.get("world").getAsString())));

        xyzLoc.setPitch(pitch);
        xyzLoc.setYaw(yaw);

        return xyzLoc;
    }

    @Override
    public JsonElement serialize(Location location, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject obj = new JsonObject();
        obj.add("xyz", new JsonPrimitive(Utils.locationToLong(location)));
        obj.add("yaw", new JsonPrimitive(location.getYaw()));
        obj.add("pitch", new JsonPrimitive(location.getPitch()));
        obj.add("world", new JsonPrimitive(location.getWorld().getUID().toString()));
        return obj;
    }
}
