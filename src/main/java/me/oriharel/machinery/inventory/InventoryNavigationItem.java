package me.oriharel.machinery.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * a class for easy inventory navigation
 */
public class InventoryNavigationItem extends InventoryItem {

    protected String routeToName;
    protected Inventory parentInventory;

    public InventoryNavigationItem(String routeToName, Inventory parentInventory, int indexInInventory, Material material, int amount, String displayName,
                                   String... lore) {
        super(indexInInventory, material, amount, displayName, lore);
        this.routeToName = routeToName;
        this.parentInventory = parentInventory;
    }

    public InventoryNavigationItem(String routeToName, Inventory parentInventory, int indexInInventory, ItemStack itemStack) {
        super(indexInInventory, itemStack);
        this.routeToName = routeToName;
        this.parentInventory = parentInventory;
    }

    public InventoryNavigationItem(String routeToName, Inventory parentInventory, int indexInInventory, Material material, int amount, String displayName) {
        super(indexInInventory, material, amount, displayName);
        this.routeToName = routeToName;
        this.parentInventory = parentInventory;
    }

    public InventoryNavigationItem navigate() {
        parentInventory.navigateToNamedRoute(routeToName);
        return this;
    }
}
