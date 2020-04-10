package me.oriharel.machinery.listeners;

import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.machine.PlayerMachine;
import org.bukkit.ChatColor;
import org.bukkit.block.TileState;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class Interact implements Listener {

    private Machinery machinery;

    public Interact(Machinery machinery) {
        this.machinery = machinery;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (!(e.getClickedBlock().getState() instanceof TileState)) return;

        if (machinery.getMachineManager().getMachinePartLocations().contains(e.getClickedBlock().getLocation())) {
            e.setUseInteractedBlock(Event.Result.DENY);
        }

        PlayerMachine machine = machinery.getMachineManager().getMachineCores().get(e.getClickedBlock().getLocation());
        if (machine != null) {
            e.setUseInteractedBlock(Event.Result.DENY);
            e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    machinery.getFileManager().getConfig("config.yml").get().getString("open_machine_gui_message")));
            // TODO: Open gui machine management GUI logic
        }
    }
}
