package me.oriharel.machinery.serialization;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.UUID;

public class UUIDTypeAdapter implements JsonSerializer<UUID>, JsonDeserializer<UUID> {
    @Override
    public UUID deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject obj = jsonElement.getAsJsonObject();
        long most = obj.get("mostBytes").getAsLong();
        long least = obj.get("leastBytes").getAsLong();
        return new UUID(most, least);
    }

    @Override
    public JsonElement serialize(UUID uuid, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject obj = new JsonObject();
        obj.add("leastBytes", new JsonPrimitive(uuid.getLeastSignificantBits()));
        obj.add("mostBytes", new JsonPrimitive(uuid.getMostSignificantBits()));
        return obj;
    }
}
