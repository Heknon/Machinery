package me.oriharel.machinery.listeners;

import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.machine.PlayerMachine;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class Interact implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        Bukkit.getScheduler().runTaskAsynchronously(Machinery.getInstance(), () -> {
            for (PlayerMachine playerMachine : Machinery.getInstance().getMachineManager().getPlayerMachines().values()) {
                if (playerMachine.getOpenGUIBlockLocation().equals(e.getClickedBlock().getLocation())) {
                    e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                            Machinery.getInstance().getFileManager().getConfig("config.yml").get().getString("open_machine_gui_message")));
                    // TODO: Open gui machine management GUI logic
                    e.setCancelled(true);
                    return;
                }
            }
        });
    }
}
