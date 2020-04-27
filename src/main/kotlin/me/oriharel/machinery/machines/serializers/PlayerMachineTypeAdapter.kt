package me.oriharel.machinery.machines.serializers

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import me.oriharel.machinery.machines.MachineFactory
import me.oriharel.machinery.machines.machine.Machine
import me.oriharel.machinery.machines.machine.PlayerMachine
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

    override fun getSerializedMachine(machine: PlayerMachine?, context: JsonSerializationContext): JsonObject {
        val baseMachineSerialized = super.getSerializedMachine(machine, context)
        baseMachineSerialized.add("zenCoinsGained", JsonPrimitive(machine?.zenCoinsGained))
        baseMachineSerialized.add("totalZenCoinsGained", JsonPrimitive(machine?.totalZenCoinsGained))
        baseMachineSerialized.add("totalResourcesGained", JsonPrimitive(machine?.totalResourcesGained))
        baseMachineSerialized.add("energyInMachine", JsonPrimitive(machine?.energyInMachine))
        baseMachineSerialized.add("ownerUuidMost", JsonPrimitive(machine?.owner?.mostSignificantBits))
        baseMachineSerialized.add("ownerUuidLeast", JsonPrimitive(machine?.owner?.leastSignificantBits))

        val locObj = JsonObject()

        locObj.add("xyz", JsonPrimitive(Utils.locationToLong(machine?.core)))
        locObj.add("world", JsonPrimitive(machine?.core?.world?.uid.toString()))
        baseMachineSerialized.add("coreBlockLocation", locObj)
        baseMachineSerialized.add("resourcesGained", context.serialize(machine?.resourcesGained, object : TypeToken<HashMap<Material?, ItemStack?>?>() {}.type))
        baseMachineSerialized.add("upgrades", context.serialize(machine?.upgrades, object : TypeToken<List<AbstractUpgrade?>?>() {}.type))
        baseMachineSerialized.add("playersWithAccessPermission", context.serialize(machine?.playersWithAccessPermission, object : TypeToken<Set<UUID?>?>() {}.type))

        return baseMachineSerialized
    }
}