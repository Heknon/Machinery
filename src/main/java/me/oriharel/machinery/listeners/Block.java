package me.oriharel.machinery.listeners;

import me.oriharel.machinery.exceptions.MachineNotFoundException;
import me.oriharel.machinery.items.MachineBlock;
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
        System.out.println(machineBlock.getMachine());
    }
}
