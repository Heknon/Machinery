package me.oriharel.machinery.listeners;

import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.exceptions.MachineNotFoundException;
import me.oriharel.machinery.items.MachineBlock;
import me.oriharel.machinery.machine.Machine;
import me.oriharel.machinery.machine.PlayerMachine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
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

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Location loc = e.getBlock().getLocation();
        Bukkit.getScheduler().runTaskAsynchronously(Machinery.getInstance(), () -> {
            for (PlayerMachine playerMachine : Machinery.getInstance().getMachineManager().getPlayerMachines().values()) {
                for (Location location : playerMachine.getBlockLocations()) {
                    if (location.equals(loc)) {
                        e.setCancelled(true);
                        return;
                    }
                }
            }
        });
    }
}
