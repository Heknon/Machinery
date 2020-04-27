package me.oriharel.machinery.serialization

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import me.oriharel.machinery.machine.Machine
import me.oriharel.machinery.machine.MachineFactory
import me.oriharel.machinery.machine.PlayerMachine
import me.oriharel.machinery.upgrades.AbstractUpgrade
import me.oriharel.machinery.utilities.Utils
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.*

class PlayerMachineTypeAdapter(factory: MachineFactory?) : MachineTypeAdapter<PlayerMachine?>(factory), JsonSerializer<PlayerMachine?>, JsonDeserializer<PlayerMachine?> {
    override fun getDeserializedMachine(machineJsonObject: JsonObject, context: JsonDeserializationContext): PlayerMachine? {
        val baseMachine: Machine? = super.getDeserializedMachine(machineJsonObject, context)
        val energyInMachine = machineJsonObject["energyInMachine"].asInt
        val zenCoinsGained = machineJsonObject["zenCoinsGained"].asDouble
        val totalZenCoinsGained = machineJsonObject["totalZenCoinsGained"].asDouble
        val totalResourcesGained = machineJsonObject["totalResourcesGained"].asDouble
        val locObj = machineJsonObject["coreBlockLocation"].asJsonObject
        val machineCoreBlockLocation = Utils.longToLocation(locObj["xyz"].asLong,
                Bukkit.getWorld(UUID.fromString(locObj["world"].asString)))
        val ownerUuid = UUID(machineJsonObject["ownerUuidMost"].asLong, machineJsonObject["ownerUuidLeast"].asLong)
        val upgrades = context.deserialize<List<AbstractUpgrade?>>(machineJsonObject["upgrades"], object : TypeToken<List<AbstractUpgrade?>?>() {}.type)
        val resourcesGained = context.deserialize<HashMap<Material?, ItemStack?>>(machineJsonObject["resourcesGained"], object : TypeToken<HashMap<Material?, ItemStack?>?>() {}.type)
        val playersWithAccessPermission = context.deserialize<Set<UUID?>>(machineJsonObject["playersWithAccessPermission"], object : TypeToken<Set<UUID?>?>() {}.type)
        return factory!!.createMachine(baseMachine, machineCoreBlockLocation, totalResourcesGained, energyInMachine, zenCoinsGained, totalZenCoinsGained, ownerUuid,
                upgrades, resourcesGained, playersWithAccessPermission)
    }

    override fun getSerializedMachine(machine: PlayerMachine?, context: JsonSerializationContext): JsonObject? {
        val baseMachineSerialized = super.getSerializedMachine(machine, context)
        baseMachineSerialized!!.add("zenCoinsGained", JsonPrimitive(machine.getZenCoinsGained()))
        baseMachineSerialized.add("totalZenCoinsGained", JsonPrimitive(machine.getTotalZenCoinsGained()))
        baseMachineSerialized.add("totalResourcesGained", JsonPrimitive(machine.getTotalResourcesGained()))
        baseMachineSerialized.add("energyInMachine", JsonPrimitive(machine.getEnergyInMachine()))
        baseMachineSerialized.add("ownerUuidMost", JsonPrimitive(machine.getOwner().mostSignificantBits))
        baseMachineSerialized.add("ownerUuidLeast", JsonPrimitive(machine.getOwner().leastSignificantBits))
        val locObj = JsonObject()
        locObj.add("xyz", JsonPrimitive(Utils.locationToLong(machine.getMachineCore())))
        locObj.add("world", JsonPrimitive(machine.getMachineCore().world.getUID().toString()))
        baseMachineSerialized.add("coreBlockLocation", locObj)
        baseMachineSerialized.add("resourcesGained", context.serialize(machine.getResourcesGained(), object : TypeToken<HashMap<Material?, ItemStack?>?>() {}.type))
        baseMachineSerialized.add("upgrades", context.serialize(machine.getUpgrades(), object : TypeToken<List<AbstractUpgrade?>?>() {}.type))
        baseMachineSerialized.add("playersWithAccessPermission", context.serialize(machine.getPlayersWithAccessPermission(), object : TypeToken<Set<UUID?>?>() {}.type))
        return baseMachineSerialized
    }
}