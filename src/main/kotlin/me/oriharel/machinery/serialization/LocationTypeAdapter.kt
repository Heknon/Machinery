package me.oriharel.machinery.serialization

import com.google.gson.*
import me.oriharel.machinery.utilities.Utils
import org.bukkit.Bukkit
import org.bukkit.Location
import java.lang.reflect.Type
import java.util.*

class LocationTypeAdapter : JsonSerializer<Location>, JsonDeserializer<Location?> {
    @Throws(JsonParseException::class)
    override fun deserialize(jsonElement: JsonElement, type: Type, jsonDeserializationContext: JsonDeserializationContext): Location? {
        val obj = jsonElement.asJsonObject
        val pitch = obj["pitch"].asFloat
        val yaw = obj["yaw"].asFloat
        val xyzLoc = Utils.longToLocation(obj["xyz"].asLong, Bukkit.getWorld(UUID.fromString(obj["world"].asString)))
        xyzLoc!!.pitch = pitch
        xyzLoc.yaw = yaw
        return xyzLoc
    }

    override fun serialize(location: Location, type: Type, jsonSerializationContext: JsonSerializationContext): JsonElement {
        val obj = JsonObject()
        obj.add("xyz", JsonPrimitive(Utils.locationToLong(location)))
        obj.add("yaw", JsonPrimitive(location.yaw))
        obj.add("pitch", JsonPrimitive(location.pitch))
        obj.add("world", JsonPrimitive(location.world!!.uid.toString()))
        return obj
    }
}