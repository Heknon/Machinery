package me.oriharel.machinery.serialization;

import com.google.gson.*;
import me.oriharel.machinery.upgrades.AbstractUpgrade;
import me.oriharel.machinery.upgrades.LootBonusUpgrade;
import me.oriharel.machinery.upgrades.SpeedUpgrade;

import java.lang.reflect.Type;

public class AbstractUpgradeTypeAdapter implements JsonSerializer<AbstractUpgrade>, JsonDeserializer<AbstractUpgrade> {
    @Override
    public AbstractUpgrade deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject obj = jsonElement.getAsJsonObject();
        String upgradeType = obj.get("type").getAsString();
        if (upgradeType.equals(LootBonusUpgrade.class.getName())) {
            return new LootBonusUpgrade(obj.get("level").getAsInt());
        } else if (upgradeType.equals(SpeedUpgrade.class.getName())) {
            return new SpeedUpgrade(obj.get("level").getAsInt());
        }
        return null;
    }

    @Override
    public JsonElement serialize(AbstractUpgrade abstractUpgrade, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject obj = new JsonObject();
        obj.add("type", new JsonPrimitive(abstractUpgrade.getClass().getName()));
        obj.add("level", new JsonPrimitive(abstractUpgrade.getLevel()));
        return obj;
    }
}
