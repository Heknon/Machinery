package me.oriharel.machinery.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Requires an InventoryPage that implements DatablePage
 * @param <T> type of data
 */
public class InventoryNavigationItemData<T> extends InventoryNavigationItem implements Datable<T> {

    private T navigationData;

    public InventoryNavigationItemData(String routeToName, Inventory parentInventory, int indexInInventory, Material material, int amount, String displayName,
                                       T navigationData, String... lore) {
        super(routeToName, parentInventory, indexInInventory, material, amount, displayName, lore);
        this.navigationData = navigationData;
    }

    public InventoryNavigationItemData(String routeToName, Inventory parentInventory, int indexInInventory, ItemStack itemStack, T navigationData) {
        super(routeToName, parentInventory, indexInInventory, itemStack);
        this.navigationData = navigationData;
    }

    public InventoryNavigationItemData(String routeToName, Inventory parentInventory, int indexInInventory, Material material, int amount, String displayName,
                                       T navigationData) {
        super(routeToName, parentInventory, indexInInventory, material, amount, displayName);
        this.navigationData = navigationData;
    }

    public T getNavigationData() {
        return navigationData;
    }

    @Override
    public InventoryNavigationItemData<T> navigate() {
        parentInventory.navigateToNamedRoute(routeToName, navigationData);
        return this;
    }

    @Override
    public T getStoredData() {
        return navigationData;
    }

    @Override
    public InventoryNavigationItemData<T> setStoredData(T data) {
        this.navigationData = data;
        return this;
    }
}
