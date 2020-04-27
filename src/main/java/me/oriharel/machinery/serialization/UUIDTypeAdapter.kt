package me.oriharel.machinery.serialization

import com.google.gson.*
import java.lang.reflect.Type
import java.util.*

class UUIDTypeAdapter : JsonSerializer<UUID>, JsonDeserializer<UUID> {
    @Throws(JsonParseException::class)
    override fun deserialize(jsonElement: JsonElement, type: Type, jsonDeserializationContext: JsonDeserializationContext): UUID {
        val obj = jsonElement.asJsonObject
        val most = obj["mostBytes"].asLong
        val least = obj["leastBytes"].asLong
        return UUID(most, least)
    }

    override fun serialize(uuid: UUID, type: Type, jsonSerializationContext: JsonSerializationContext): JsonElement {
        val obj = JsonObject()
        obj.add("leastBytes", JsonPrimitive(uuid.leastSignificantBits))
        obj.add("mostBytes", JsonPrimitive(uuid.mostSignificantBits))
        return obj
    }
}