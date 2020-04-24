package me.oriharel.machinery.inventory;

import me.oriharel.machinery.machine.PlayerMachine;
import me.oriharel.machinery.utilities.Callback;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftInventoryCustom;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class InventoryPage implements InventoryHolder {


    protected final int size;
    protected final String title;
    protected final InventoryItem fillment;
    protected final Inventory inventory;
    protected Set<InventoryItem> inventoryItems;
    protected Callback onClose;
    protected boolean cancelClick = true;
    protected PlayerMachine owner;

    public InventoryPage(int size, String title, InventoryItem fillment, Set<InventoryItem> inventoryItems, PlayerMachine owner) {
        this.size = size;
        this.title = title;
        this.fillment = fillment;
        this.inventoryItems = inventoryItems;
        this.onClose = null;
        this.owner = owner;
        inventory = new CraftInventoryCustom(this, size, title);
        inventory.setMaxStackSize(20000000);
        populateItems();
    }

    public InventoryPage(int size, String title, InventoryItem fillment, Set<InventoryItem> inventoryItems,
                         Callback onClose, PlayerMachine owner) {
        this.size = size;
        this.title = title;
        this.fillment = fillment;
        this.inventoryItems = inventoryItems;
        this.onClose = onClose;
        this.owner = owner;
        inventory = new CraftInventoryCustom(this, size, title);
        inventory.setMaxStackSize(2000000000);
        populateItems();
    }

    public InventoryPage setInventoryItems(Set<InventoryItem> inventoryItems) {
        this.inventoryItems = inventoryItems;
        populateItems();
        return this;
    }

    private void populateItems() {
        if (inventoryItems == null || inventoryItems.isEmpty()) return;
        ItemStack[] contents = inventory.getContents();
        inventoryItems.forEach(item -> contents[item.indexInInventory] = item);
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if ((item == null || item.getType() == Material.AIR) && fillment != null) {
                contents[i] = fillment.clone();
            }
        }
        inventory.setContents(contents);
    }


    public InventoryPage setCancelClick(boolean cancelClick) {
        this.cancelClick = cancelClick;
        return this;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
