package me.oriharel.machinery.structure

import me.oriharel.machinery.Machinery
import me.oriharel.machinery.utilities.Callback
import org.bukkit.Bukkit
import schematics.Schematic
import java.io.File
import java.util.*
import java.util.function.Consumer

/**
 * used to register all structures found in machines.yml
 */
class StructureManager(private val machinery: Machinery) {
    private val structures: HashMap<String, Structure?>
    private val callbacksOnDone: MutableList<Callback>

    /**
     * Get a schematic by it's path in jar
     *
     * @param path File#getPath
     * @return Schematic or null if not found
     */
    fun getSchematicByPath(path: String): Structure? {
        return structures.getOrDefault(path, null)
    }

    private fun registerSchematic(key: String) {
        if (structures.containsKey(key)) return
        val file = File(machinery.dataFolder, "structures/" + key + Machinery.Companion.STRUCTURE_EXTENSION)
        val schematic = Schematic(machinery, file)
        schematic.loadSchematic()
        val structure = Structure(schematic, key)
        structures[file.path] = structure
    }

    private fun registerSchematics() {
        val configLoad = machinery.fileManager.getConfig("machines.yml").get()
        val keys = configLoad!!.getKeys(false)
        Bukkit.getScheduler().runTaskAsynchronously(machinery, Runnable {
            for (key in keys) {
                registerSchematic(key)
            }
            Bukkit.getScheduler().runTask(machinery, Runnable { callbacksOnDone.forEach(Consumer { obj: Callback -> obj.apply() }) })
        })
    }

    fun registerOnDoneCallback(callback: () -> Unit) {
        callbacksOnDone.add(callback)
    }

    init {
        structures = HashMap()
        callbacksOnDone = ArrayList()
        Bukkit.getScheduler().runTaskAsynchronously(machinery, Runnable { registerSchematics() })
    }
}