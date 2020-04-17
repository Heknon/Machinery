package me.oriharel.machinery.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

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
        ItemStack[] contents = inventory.getContents();
        inventoryItems.forEach(item -> contents[item.indexInInventory] = item);
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item == null || item.getType() == Material.AIR) {
                contents[i] = fillment.clone();
            }
        }
        inventory.setContents(contents);
    }


    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
