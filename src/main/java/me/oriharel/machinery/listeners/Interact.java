package me.oriharel.machinery.listeners;

import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.inventory.MachineInventoryImpl;
import me.oriharel.machinery.machine.PlayerMachine;
import me.oriharel.machinery.message.Message;
import me.oriharel.machinery.utilities.Utils;
import org.bukkit.ChatColor;
import org.bukkit.block.TileState;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.text.DecimalFormat;

public class Interact implements Listener {

    private Machinery machinery;
    private DecimalFormat decimalFormat;

    public Interact(Machinery machinery) {
        this.machinery = machinery;
        decimalFormat = new DecimalFormat("#.##");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getClickedBlock() == null) return;
        if (!(e.getClickedBlock().getState() instanceof TileState)) return;

        if (machinery.getMachineManager().getMachinePartLocations().contains(e.getClickedBlock().getLocation())) {
            e.setUseInteractedBlock(Event.Result.DENY);
        }


        PlayerMachine machine = machinery.getMachineManager().getMachineCores().get(e.getClickedBlock().getLocation());
        if (machine != null) {
            if (!machine.getPlayersWithAccessPermission().contains(e.getPlayer().getUniqueId())) {
                new Message("messages.yml", "open_attempt_no_access", e.getPlayer(), Utils.getLocationPlaceholders(machine.getMachineCore(),
                        Utils.getMachinePlaceholders(machine))).send();
                e.setUseInteractedBlock(Event.Result.DENY);
                e.setCancelled(true);
                e.getPlayer().closeInventory();
                return;
            }
            e.setUseInteractedBlock(Event.Result.DENY);
            e.setCancelled(true);
            new Message("messages.yml", "open_machine_gui", e.getPlayer(), Utils.getLocationPlaceholders(machine.getMachineCore(),
                    Utils.getMachinePlaceholders(machine))).send();
            e.getPlayer().closeInventory();

            new MachineInventoryImpl(machine, e.getPlayer(), machinery).openInventory();

        }
    }
}
