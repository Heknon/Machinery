package me.oriharel.machinery.inventory;

import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.fuel.PlayerFuel;
import me.oriharel.machinery.machine.PlayerMachine;
import me.oriharel.machinery.utilities.NMS;
import me.oriharel.machinery.utilities.Utils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

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
}

