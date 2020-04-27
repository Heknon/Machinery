package me.oriharel.machinery.listeners

import me.oriharel.machinery.Machinery
import me.oriharel.machinery.inventory.MachineInventoryImpl
import me.oriharel.machinery.message.Message
import me.oriharel.machinery.utilities.Utils
import org.bukkit.block.TileState
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import java.text.DecimalFormat

class Interact(private val machinery: Machinery) : Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerInteract(e: PlayerInteractEvent) {
        if (e.action != Action.RIGHT_CLICK_BLOCK || e.clickedBlock == null) return
        if (e.clickedBlock!!.state !is TileState) return
        if (machinery.machineManager?.machinePartLocations?.contains(e.clickedBlock!!.location) == true) {
            e.setUseInteractedBlock(Event.Result.DENY)
        }
        val machine = machinery.machineManager?.machineCores?.get(e.clickedBlock!!.location)
        if (machine != null) {
            if (machine.playersWithAccessPermission?.contains(e.player.uniqueId) == false) {
                Message("messages.yml", "open_attempt_no_access", e.player, Utils.getLocationPlaceholders(machine.machineCore,
                        Utils.getMachinePlaceholders(machine))).send()
                e.setUseInteractedBlock(Event.Result.DENY)
                e.isCancelled = true
                e.player.closeInventory()
                return
            }
            e.setUseInteractedBlock(Event.Result.DENY)
            e.isCancelled = true
            Message("messages.yml", "open_machine_gui", e.player, Utils.getLocationPlaceholders(machine.machineCore,
                    Utils.getMachinePlaceholders(machine))).send()
            e.player.closeInventory()
            MachineInventoryImpl(machine, e.player, machinery).openInventory()
        }
    }

}