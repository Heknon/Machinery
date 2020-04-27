package me.oriharel.machinery.machines.listeners

import me.oriharel.machinery.Machinery
import me.oriharel.machinery.machines.items.MachineItem
import me.oriharel.machinery.machines.machine.PlayerMachine
import me.oriharel.machinery.message.Message
import me.oriharel.machinery.utilities.Utils
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityExplodeEvent

class Block(private val machinery: Machinery) : Listener {

    /**
     * called when a player places a block
     * used to handle building a machine
     */
    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent) {
        val machineItem: MachineItem
        try {
            machineItem = MachineItem(e.itemInHand, machinery.machineManager?.machineFactory, PlayerMachine::class.java)
        } catch (ex: Exception) {
            val compound = CraftItemStack.asNMSCopy(e.itemInHand).tag
            if (compound != null && compound.hasKey("machine")) {
                ex.printStackTrace()
                Message("&4&l[!] &cAn error has occurred, please contact a server administrator.", e.player).send()
                e.isCancelled = true
            }
            return
        }
        val machine = machineItem.machine
        if (machinery.machineManager?.buildMachine(e.player.uniqueId, machine, e.block.location) == false) {
            Message("messages.yml", "not_empty_place", e.player).send()
            e.block.type = Material.AIR
            return
        }
        if (e.player.gameMode == GameMode.CREATIVE) {
            e.isCancelled = true
            return
        }
        e.block.type = Material.AIR
    }

    /**
     * Used to stop players from breaking parts of a machine
     */
    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (machinery.machineManager?.temporaryPreRegisterMachineLocations?.contains(e.block.location) == true || machinery.machineManager?.machinePartLocations?.contains(e.block.location) == true) {
            Message("messages.yml", "break_machine_attempt", e.player, Utils.getLocationPlaceholders(e.block.location)).send()
            e.isCancelled = true
        }
    }

    /**
     * used to block the explosion of part of a machine
     */
    @EventHandler
    fun onEntityExplosion(e: EntityExplodeEvent) {
        val blocks = e.blockList()
        for (block in blocks) {
            if (machinery.machineManager?.temporaryPreRegisterMachineLocations?.contains(block.location) == true || machinery.machineManager?.machinePartLocations?.contains(block.location) == true) {
                e.isCancelled = true
            }
        }
    }

}