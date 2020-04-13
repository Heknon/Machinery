package me.oriharel.machinery.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class InventoryPage implements InventoryHolder {
    protected final int size;
    protected final String title;
    protected final InventoryItem fillment;
    protected final Set<InventoryItem> inventoryItems;
    protected final Inventory inventory;

    public InventoryPage(int size, String title, InventoryItem fillment, Set<InventoryItem> inventoryItems) {
        this.size = size;
        this.title = title;
        this.fillment = fillment;
        this.inventoryItems = inventoryItems;
        inventory = Bukkit.createInventory(this, size, title);
        inventory.setMaxStackSize(2000000000);
        populateItems();
    }

    private void populateItems() {
        Set<Integer> populatedIndices = new HashSet<>();
        AtomicInteger indexOfUnspecified = new AtomicInteger();
        inventoryItems.forEach(item -> {
            int index = item.indexInInventory;
            if (index == -1) {
                index = indexOfUnspecified.getAndIncrement();
                if (populatedIndices.contains(index)) {
                    for (int i = index + 1; i < size; i++) {
                        if (!populatedIndices.contains(i)) {
                            index = i;
                            item.indexInInventory = i;
                            break;
                        }
                    }
                }
            }
            inventory.setItem(index, item.itemStack);
            populatedIndices.add(index);
        });
        ItemStack[] items = inventory.getContents();
        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            if (item == null || item.getType().equals(Material.AIR)) {
                items[i] = fillment.itemStack;
            }
        }
        inventory.setContents(items);
    }


    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
