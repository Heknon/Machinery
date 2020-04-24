package me.oriharel.machinery.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Requires an InventoryPage that implements DatablePage
 * @param <T> type of data
 */
public class InventoryNavigationItemData<T> extends InventoryNavigationItem implements NavigableData<T> {

    private T navigationData;
    private NavigableDataInventoryPage<T> routePage;

    public InventoryNavigationItemData(NavigableDataInventoryPage<T> routePage, Inventory parentInventory, int indexInInventory, Material material, int amount, String displayName,
                                       T navigationData, String... lore) {
        super(null, parentInventory, indexInInventory, material, amount, displayName, lore);
        this.navigationData = navigationData;
        this.routePage = routePage;
    }

    public InventoryNavigationItemData(NavigableDataInventoryPage<T> routePage, Inventory parentInventory, int indexInInventory, ItemStack itemStack, T navigationData) {
        super(null, parentInventory, indexInInventory, itemStack);
        this.navigationData = navigationData;
        this.routePage = routePage;
    }

    public InventoryNavigationItemData(NavigableDataInventoryPage<T> routePage, Inventory parentInventory, int indexInInventory, Material material, int amount, String displayName,
                                       T navigationData) {
        super(null, parentInventory, indexInInventory, material, amount, displayName);
        this.navigationData = navigationData;
        this.routePage = routePage;
    }

    public T getNavigationData() {
        return navigationData;
    }

    @Override
    public InventoryNavigationItemData<T> navigate() {
        parentInventory.navigateDirect(routePage, navigationData);
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
