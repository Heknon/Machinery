package me.oriharel.machinery.serialization

import com.google.gson.*
import me.oriharel.machinery.utilities.NMS
import net.minecraft.server.v1_15_R1.NBTTagCompound
import java.lang.reflect.Type

class NBTTagCompoundTypeAdapter : JsonSerializer<NBTTagCompound>, JsonDeserializer<NBTTagCompound?> {
    @Throws(JsonParseException::class)
    override fun deserialize(jsonElement: JsonElement, type: Type, jsonDeserializationContext: JsonDeserializationContext): NBTTagCompound? {
        return NMS.nbtFromMap(jsonDeserializationContext.deserialize(jsonElement, MutableMap::class.java))
    }

    override fun serialize(compound: NBTTagCompound, type: Type, jsonSerializationContext: JsonSerializationContext): JsonElement {
        return JsonPrimitive(compound.toString())
    }
}