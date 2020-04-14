package me.oriharel.machinery.listeners;

import com.google.common.collect.Sets;
import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.fuel.PlayerFuel;
import me.oriharel.machinery.inventory.Inventory;
import me.oriharel.machinery.inventory.InventoryItem;
import me.oriharel.machinery.inventory.InventoryPage;
import me.oriharel.machinery.machine.PlayerMachine;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.TileState;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
            e.setUseInteractedBlock(Event.Result.DENY);
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    machinery.getFileManager().getConfig("config.yml").get().getString("open_machine_gui_message")));
            e.getPlayer().closeInventory();
            // TODO: Open gui machine management GUI logic

            Map<String, InventoryPage> routes = new HashMap<>();
            Inventory inventory = new Inventory(routes, e.getPlayer());
            routes.put("start", new InventoryPage(54, machine.getType().toTitle(), new InventoryItem(Material.GRAY_STAINED_GLASS_PANE, 1, ""), Sets.newHashSet(
                    new InventoryItem(20, Material.GHAST_TEAR, 1, "Bank", "§9Click to open the withdrawal menu",
                            "§bThere are currently §e" + decimalFormat.format(machine.getZenCoinsGained()) + " §bZen Coins stored in the machine").setOnClick(() -> {
                        inventory.navigateToNamedRoute("bank");
                        return true;
                    }),
                    new InventoryItem(24, Material.PAPER, 1, "Storage", "§9Click to open open the storage of the machine",
                            "§bThere are currently §e" + decimalFormat.format(machine.getResourcesGained()) + " §bresources stored in the machine").setOnClick(() -> {
                        inventory.navigateToNamedRoute("resources");
                        return true;
                    }),
                    new InventoryItem(45, Material.REPEATER, 1, "Statistics", "§9Statistics:", "§bTotal resources gained: §e" + machine.getTotalResourcesGained(),
                            "§bTotal Zen Coins gained: §e" + machine.getTotalZenCoinsGained()).setOnClick(() -> true),
                    new InventoryItem(48, Material.OBSIDIAN, 1, "Fuel", "§9Fuel up your machine.",
                            machine.getFuels().stream().mapToInt(PlayerFuel::getEnergy).sum() + "/" + machine.getMaxFuel()).setOnClick(() -> {
                        inventory.navigateToNamedRoute("fuels");
                        return true;
                    }),
                    new InventoryItem(50, Material.HOPPER, 1, "Upgrades", "§9Upgrade your machine").setOnClick(() -> {
                        inventory.navigateToNamedRoute("upgrades");
                        return true;
                    }),
                    new InventoryItem(53, Material.BARRIER, 1, "Back", "§cExit the Machine GUI").setOnClick(() -> {
                        e.getPlayer().closeInventory();
                        return true;
                    })
            )));

            routes.put("resources", new InventoryPage(9, "Resources", null, machine.getResourcesGained().stream().map(InventoryItem::new).collect(Collectors.toSet())));
            routes.put("fuels", new InventoryPage(5, "Fuels", null,
                    machine.getFuels().stream().map(f -> new InventoryItem(f.getItem(f.getAmount()))).collect(Collectors.toSet())));
            routes.put("bank", new InventoryPage(9, "Withdrawal", new InventoryItem(Material.GRAY_STAINED_GLASS_PANE, 1, ""), Sets.newHashSet(
                    new InventoryItem(0, Material.MAGMA_CREAM, 1, "§61,000 Zen Coins")
            )));


        }
    }
}
