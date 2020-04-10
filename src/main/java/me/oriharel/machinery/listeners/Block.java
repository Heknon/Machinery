package me.oriharel.machinery.listeners;

import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.exceptions.MachineNotFoundException;
import me.oriharel.machinery.items.MachineBlock;
import me.oriharel.machinery.machine.Machine;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class Block implements Listener {

    private Machinery machinery;

    public Block(Machinery machinery) {
        this.machinery = machinery;
    }

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
        if (machinery.getMachineManager().getTemporaryPreRegisterMachineLocations().contains(e.getBlock().getLocation()) || machinery.getMachineManager().getMachinePartLocations().contains(e.getBlock().getLocation())) {
            e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', Machinery.getInstance().getFileManager().getConfig("config.yml").get().getString(
                    "break_machine_attempt")));
            e.setCancelled(true);
        }
    }
}
