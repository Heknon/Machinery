package me.oriharel.machinery.listeners;

import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.items.MachineItem;
import me.oriharel.machinery.machine.Machine;
import me.oriharel.machinery.machine.PlayerMachine;
import me.oriharel.machinery.message.Message;
import me.oriharel.machinery.utilities.Utils;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.List;

public class Block implements Listener {

    private Machinery machinery;

    public Block(Machinery machinery) {
        this.machinery = machinery;
    }

    /**
     * called when a player places a block
     * used to handle building a machine
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        MachineItem machineItem;
        try {
            machineItem = new MachineItem(e.getItemInHand(), machinery.getMachineManager().getMachineFactory(), PlayerMachine.class);
        } catch (Exception ex) {
            NBTTagCompound compound = CraftItemStack.asNMSCopy(e.getItemInHand()).getTag();
            if (compound != null && compound.hasKey("machine")) {
                ex.printStackTrace();
                new Message("&4&l[!] &cAn error has occurred, please contact a server administrator.", e.getPlayer()).send();
                e.setCancelled(true);
            }
            return;
        }
        Machine machine = machineItem.getMachine();
        if (!machinery.getMachineManager().buildMachine(e.getPlayer().getUniqueId(), machine, e.getBlock().getLocation())) {
            new Message("messages.yml", "not_empty_place", e.getPlayer()).send();
            e.getBlock().setType(Material.AIR);
            return;
        }
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE) {
            e.setCancelled(true);
            return;
        }
        e.getBlock().setType(Material.AIR);

    }

    /**
     * Used to stop players from breaking parts of a machine
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (machinery.getMachineManager().getTemporaryPreRegisterMachineLocations().contains(e.getBlock().getLocation()) || machinery.getMachineManager().getMachinePartLocations().contains(e.getBlock().getLocation())) {
            new Message("messages.yml", "break_machine_attempt", e.getPlayer(), Utils.getLocationPlaceholders(e.getBlock().getLocation())).send();
            e.setCancelled(true);
        }
    }

    /**
     * used to block the explosion of part of a machine
     */
    @EventHandler
    public void onEntityExplosion(EntityExplodeEvent e) {
        List<org.bukkit.block.Block> blocks = e.blockList();
        for (org.bukkit.block.Block block : blocks) {
            if (machinery.getMachineManager().getTemporaryPreRegisterMachineLocations().contains(block.getLocation()) || machinery.getMachineManager().getMachinePartLocations().contains(block.getLocation())) {
                e.setCancelled(true);
            }
        }
    }
}
