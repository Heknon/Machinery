package me.oriharel.machinery.machine

import com.mojang.datafixers.util.Pair
import me.oriharel.machinery.data.*
import org.bukkit.Material
import org.bukkit.configuration.MemorySection
import org.bukkit.configuration.file.YamlConfiguration
import java.util.*

/**
 * initializes the resource map of a machine - where the weight chance of getting a certain item is
 * @param machineName the name of the machine to get the resource map for
 * @param configLoad the load of machines.yml
 */
class ResourceMap(machineName: String?, configLoad: YamlConfiguration?) : HashMap<Int?, ChancableList<out ChanceableOperation<*, MachineResourceGetProcess?>?>?>() {
    init {
        val resourcesSection = configLoad!!.getConfigurationSection(machineName!!)!!.getConfigurationSection("resources")
        val serializedSection = resourcesSection!!.getValues(false)
        // branch level one corresponds to Integer, Map<String, Map<String, Integer>> integer = weight
        for ((key, value) in serializedSection) {
            val weight = key.toInt()
            val levelTwo = (value as MemorySection).getValues(false)
            val materialChanceDelegate: MutableList<Pair<Material?, Range>> = ArrayList()
            val chancableList = ChancableList<ChanceableOperation<*, MachineResourceGetProcess?>?>()
            for ((key1, value1) in levelTwo) { // branch level two entry corresponds to String, Map<String, Integer> String is material
                val material = Material.getMaterial(key1)
                val levelThree = value1 as MemorySection // branch level three corresponds to - String, Integer - min/max
                val min = levelThree.getInt("min")
                val max = levelThree.getInt("min")
                if (key1.equals("zencoins", ignoreCase = true)) {
                    chancableList.add(ZenCoinChance(Range(min, max)))
                    continue
                }
                materialChanceDelegate.add(Pair.of(material, Range(min, max)))
            }
            chancableList.add(MaterialChance(materialChanceDelegate))
            put(weight, chancableList)
        }
    }
}