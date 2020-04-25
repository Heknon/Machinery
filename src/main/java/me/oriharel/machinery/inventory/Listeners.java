package me.oriharel.machinery.inventory;

import me.oriharel.machinery.Machinery;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class Listeners implements Listener {

    private Machinery machinery;

    public Listeners(Machinery machinery) {
        this.machinery = machinery;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() == null) return;
        if (!(e.getInventory().getHolder() instanceof InventoryPage)) return;
        InventoryPage inventory = (InventoryPage) e.getInventory().getHolder();
        ItemStack clicked = e.getCurrentItem();
        e.setCancelled(inventory.cancelClick);
        if (clicked == null) return;
        for (InventoryItem item : inventory.inventoryItems) {
            if (item.indexInInventory == e.getSlot()) {
                if (item instanceof InventoryNavigationItem) {
                    InventoryNavigationItem inventoryNavigationItem = (InventoryNavigationItem) item;
                    inventoryNavigationItem.runOnClick();
                    inventoryNavigationItem.navigate();
                } else {
                    item.runOnClick();
                }
            }
        }

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() == null) return;
        if (!(e.getInventory().getHolder() instanceof InventoryPage)) return;
        InventoryPage inventory = (InventoryPage) e.getInventory().getHolder();
        if (inventory.onClose != null) inventory.onClose.apply();
    }
}

