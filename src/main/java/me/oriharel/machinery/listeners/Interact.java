package me.oriharel.machinery.listeners;

import me.oriharel.machinery.Machinery;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.block.TileState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class Interact implements Listener {

    private Machinery machinery;

    public Interact(Machinery machinery) {
        this.machinery = machinery;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (!(e.getClickedBlock().getState() instanceof TileState)) return;
        System.out.println(machinery.getMachineManager().getPlayerMachineFromBlock(e.getClickedBlock()));
        if (machinery.getMachineManager().getMachineLocations().contains(e.getClickedBlock().getLocation())) {
            e.setCancelled(true);
        }


        if (machinery.getMachineManager().getLocationPlayerMachineMap().containsKey(e.getClickedBlock().getLocation())) {
            e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    machinery.getFileManager().getConfig("config.yml").get().getString("open_machine_gui_message")));
            // TODO: Open gui machine management GUI logic
        }
    }
}
