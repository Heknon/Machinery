package me.oriharel.machinery.listeners;

import me.oriharel.machinery.exceptions.MachineNotFoundException;
import me.oriharel.machinery.items.MachineBlock;
import me.oriharel.machinery.machine.Machine;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class Block implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        MachineBlock machineBlock;
        try {
            machineBlock = new MachineBlock(e.getItemInHand());
        } catch (MachineNotFoundException ex) {
            return;
        }
        Machine machine = machineBlock.getMachine();
        machine.build(e.getPlayer().getUniqueId(), e.getBlock().getLocation());
        e.getBlock().setType(Material.AIR);
    }
}
