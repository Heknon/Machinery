package me.oriharel.machinery.serialization

import com.google.gson.*
import me.oriharel.machinery.upgrades.AbstractUpgrade
import me.oriharel.machinery.upgrades.LootBonusUpgrade
import me.oriharel.machinery.upgrades.SpeedUpgrade
import java.lang.reflect.Type

class AbstractUpgradeTypeAdapter : JsonSerializer<AbstractUpgrade>, JsonDeserializer<AbstractUpgrade?> {
    @Throws(JsonParseException::class)
    override fun deserialize(jsonElement: JsonElement, type: Type, jsonDeserializationContext: JsonDeserializationContext): AbstractUpgrade? {
        val obj = jsonElement.asJsonObject
        val upgradeType = obj["type"].asString
        if (upgradeType == LootBonusUpgrade::class.java.name) {
            return LootBonusUpgrade(obj["level"].asInt)
        } else if (upgradeType == SpeedUpgrade::class.java.name) {
            return SpeedUpgrade(obj["level"].asInt)
        }
        return null
    }

    override fun serialize(abstractUpgrade: AbstractUpgrade, type: Type, jsonSerializationContext: JsonSerializationContext): JsonElement {
        val obj = JsonObject()
        obj.add("type", JsonPrimitive(abstractUpgrade.javaClass.name))
        obj.add("level", JsonPrimitive(abstractUpgrade.level))
        return obj
    }
}