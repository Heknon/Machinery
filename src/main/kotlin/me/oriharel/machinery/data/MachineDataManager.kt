package me.oriharel.machinery.data

import me.oriharel.machinery.Machinery
import me.oriharel.machinery.utilities.Utils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.TileState
import org.bukkit.scheduler.BukkitRunnable
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.file.*
import java.util.*
import java.util.function.Consumer
import java.util.logging.Level

class MachineDataManager(private val machinery: Machinery) {
    fun loadMachineData(world: World) {
        val machinery = machinery
        val machineManager = machinery.machineManager
        val worldFolder = world.worldFolder.toPath()
        val data = worldFolder.resolve("machines.dat")
        val dataBackup = worldFolder.resolve("machines.bak.dat")
        if (Files.exists(dataBackup) && !Files.exists(data)) {
            makeBackup(data)
        }
        val locationsToRemove: MutableSet<Long> = HashSet()
        machinery.server.logger.log(Level.INFO, "Loading machines from machine.dat for world " + world.name)
        try {
            Files.newByteChannel(data, StandardOpenOption.READ).use { `in` ->
                val dataLocations = Files.readAllBytes(data)
                val buffer = ByteBuffer.wrap(dataLocations)
                `in`.read(buffer)
                buffer.flip()
                val longBuffer = buffer.asLongBuffer()
                for (i in 0 until longBuffer.capacity()) {
                    val position = buffer.getLong(i * 8)
                    if (position == 0L) continue
                    val loc = Utils.longToLocation(position, world)
                    val block = loc.block
                    try {
                        if (block.state !is TileState) throw ClassCastException()
                        val machine = machineManager!!.getPlayerMachineFromBlock(block) ?: throw ClassCastException()
                        machineManager.machineCores[loc] = machine
                        val locations: List<Location> = machineManager.getPlayerMachineLocations(block)
                        locations.forEach(Consumer { l: Location -> l.world = world })
                        machineManager.machinePartLocations.addAll(locations)
                        Bukkit.getScheduler().runTaskLater(machinery, Runnable { machine.minerProcess.startProcess() }, 40)
                    } catch (e: ClassCastException) {
                        locationsToRemove.add(position)
                        try {
                            `in`.close()
                        } catch (ex: IOException) {
                            ex.printStackTrace()
                        }
                    }
                }
                if (locationsToRemove.size != 0) {
                    removeMachineCoreLocations(locationsToRemove, world)
                    makeBackup(data)
                    machinery.logger.severe("Removed " + locationsToRemove.size + " locations from the machines.dat file since they were not machines")
                }
                `in`.close()
                machinery.server.logger.log(Level.INFO, "Loaded all machines from machine.dat for world " + world.name)
            }
        } catch (ex: IOException) {
            if (ex is NoSuchFileException) {
                machinery.server.logger.log(Level.INFO, "Couldn't find machines data 'machines.dat' for world " + world.name + ". creating...")
                createFileIfNotExist(data) { machinery.server.logger.log(Level.INFO, "Created machines.dat for world " + world.name) }
                return
            }
            ex.printStackTrace()
        }
    }

    fun addMachineCoreLocation(location: Location?) {
        val file = location!!.world!!.worldFolder.toPath().resolve("machines.dat")
        createFileIfNotExist(file)
        try {
            Files.write(file, ByteBuffer.allocate(java.lang.Long.SIZE / java.lang.Byte.SIZE).putLong(Utils.locationToLong(location)).array(), StandardOpenOption.APPEND)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun removeMachineCoreLocation(location: Location?) {
        val file = location!!.world!!.worldFolder.toPath().resolve("machines.dat")
        val longToFind = Utils.locationToLong(location)
        try {
            val bytes = Files.readAllBytes(file)
            val buffer = ByteBuffer.wrap(bytes)
            val longBuffer = buffer.asLongBuffer()
            for (i in 0 until longBuffer.capacity()) {
                val position = buffer.getLong(i * 8)
                if (position != longToFind) continue
                buffer.putLong(i * 8, 0)
                Files.write(file, buffer.array(), StandardOpenOption.WRITE)
                break
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun removeMachineCoreLocations(locations: Set<Long>, world: World) {
        val file = world.worldFolder.toPath().resolve("machines.dat")
        try {
            val bytes = Files.readAllBytes(file)
            val buffer = ByteBuffer.wrap(bytes)
            val longBuffer = buffer.asLongBuffer()
            for (i in 0 until longBuffer.capacity()) {
                val position = buffer.getLong(i * 8)
                if (!locations.contains(position)) continue
                buffer.putLong(i * 8, 0)
            }
            Files.write(file, buffer.array(), StandardOpenOption.WRITE)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun startMachineSaveDataProcess() {
        val process: BukkitRunnable = object : BukkitRunnable() {
            override fun run() {
                saveMachinesDataToBlocks()
                for (world in Bukkit.getWorlds()) {
                    val file = world.worldFolder.toPath().resolve("machines.dat")
                    try {
                        Files.copy(file, file.parent.resolve("machines.bak.dat"), StandardCopyOption.REPLACE_EXISTING)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        process.runTaskTimer(machinery, 0, 20 * 60 * 10.toLong())
    }

    fun forceMachineDataSave() {
        saveMachinesDataToBlocks()
    }

    private fun saveMachinesDataToBlocks() {
        val machineManager = machinery.machineManager
        for ((_, value) in machineManager?.machineCores!!) {
            machinery.updateMachineBlock(value, false)
        }
    }

    private fun createFileIfNotExist(file: Path) {
        if (!file.toFile().exists()) {
            try {
                file.toFile().createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun makeBackup(file: Path) {
        try {
            Files.copy(file, file.parent.resolve("machines.bak.dat"), StandardCopyOption.REPLACE_EXISTING)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun createFileIfNotExist(file: Path, ifSuccess: () -> Unit) {
        if (!file.toFile().exists()) {
            try {
                file.toFile().createNewFile()
                ifSuccess()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}