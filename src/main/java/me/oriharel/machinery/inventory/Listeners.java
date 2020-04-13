package me.oriharel.machinery.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class Listeners implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() == null) return;
        if (!(e.getInventory().getHolder() instanceof Inventory)) return;
        Inventory inventory = (Inventory) e.getInventory().getHolder();
        ItemStack clicked = e.getCurrentItem();
        if (clicked == null) return;
        for (InventoryItem item : inventory.currentPage.inventoryItems) {
            if (!item.equals(clicked, e.getSlot())) return;
            boolean cancel = item.onClick.apply();
            e.setCancelled(cancel);
        }
    }
}
