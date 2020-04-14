package me.oriharel.machinery.serialization;

import com.google.gson.*;
import me.oriharel.machinery.utilities.NMS;
import net.minecraft.server.v1_15_R1.NBTTagCompound;

import java.lang.reflect.Type;
import java.util.Map;

public class NBTTagCompoundTypeAdapter implements JsonSerializer<NBTTagCompound>, JsonDeserializer<NBTTagCompound> {
    @Override
    public NBTTagCompound deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return NMS.nbtFromMap(jsonDeserializationContext.deserialize(jsonElement, Map.class));
    }

    @Override
    public JsonElement serialize(NBTTagCompound compound, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(compound.toString());
    }
}
