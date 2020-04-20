package me.oriharel.machinery.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.ItemStack;

public class Listeners implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() == null) return;
        if (!(e.getInventory().getHolder() instanceof InventoryPage)) return;
        InventoryPage inventory = (InventoryPage) e.getInventory().getHolder();
        ItemStack clicked = e.getCurrentItem();
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
        e.setCancelled(inventory.cancelClick);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() == null) return;
        if (!(e.getInventory().getHolder() instanceof InventoryPage)) return;
        InventoryPage inventory = (InventoryPage) e.getInventory().getHolder();
        if (inventory.onClose != null) inventory.onClose.apply();
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryMoveItem(InventoryMoveItemEvent e) {
        System.out.println("dsadsa");
        if (e.getSource().equals(e.getDestination())) return;
        System.out.println(!(e.getSource().getHolder() instanceof InventoryPage) && !(e.getDestination().getHolder() instanceof InventoryPage));
        if (!(e.getSource().getHolder() instanceof InventoryPage) && !(e.getDestination().getHolder() instanceof InventoryPage)) return;
        ItemStack item = e.getItem();
        System.out.println(item);
        if (e.getSource().getHolder() instanceof InventoryPage) {
            InventoryPage page = (InventoryPage) e.getSource();
            if (!page.title.equalsIgnoreCase("resources")) return;
            ItemStack resource = page.owner.getResourcesGained().get(item.getType());
            System.out.println(page.owner.getResourcesGained().get(item.getType()));
            resource.setAmount(resource.getAmount() - item.getAmount());
            System.out.println(page.owner.getResourcesGained().get(item.getType()));
        } else if (e.getDestination() instanceof InventoryPage) {
            InventoryPage page = (InventoryPage) e.getDestination();
            if (!page.title.equalsIgnoreCase("resources")) return;
            if (!page.owner.getResourcesGained().containsKey(item.getType())) {
                page.owner.getResourcesGained().put(item.getType(), item.clone());
                return;
            }
            ItemStack resource = page.owner.getResourcesGained().get(item.getType());
            resource.setAmount(resource.getAmount() + item.getAmount());
        }
    }
}
