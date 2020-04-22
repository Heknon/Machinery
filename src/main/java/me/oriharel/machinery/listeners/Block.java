package me.oriharel.machinery.listeners;

import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.exceptions.MachineNotFoundException;
import me.oriharel.machinery.items.MachineBlock;
import me.oriharel.machinery.machine.Machine;
import me.oriharel.machinery.machine.PlayerMachine;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.ChatColor;
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

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        MachineBlock machineBlock;
        try {
            machineBlock = new MachineBlock(e.getItemInHand(), machinery.getMachineManager().getMachineFactory(), PlayerMachine.class);
        } catch (MachineNotFoundException ex) {
            NBTTagCompound compound = CraftItemStack.asNMSCopy(e.getItemInHand()).getTag();
            if (compound != null && compound.hasKey("machine")) e.setCancelled(true);
            return;
        }
        Machine machine = machineBlock.getMachine();
        if (!machinery.getMachineManager().buildMachine(e.getPlayer().getUniqueId(), machine, e.getBlock().getLocation())) return;
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE) {
            e.setCancelled(true);
            return;
        }
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
