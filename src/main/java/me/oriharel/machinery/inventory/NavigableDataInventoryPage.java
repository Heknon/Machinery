package me.oriharel.machinery.inventory;

import me.oriharel.machinery.machine.PlayerMachine;
import me.oriharel.machinery.utilities.BiSupplier;
import me.oriharel.machinery.utilities.Callback;

import java.util.Set;

public class NavigableDataInventoryPage<T> extends InventoryPage implements NavigableDataPage<T> {

    private T pageData;
    private BiSupplier<T, NavigableDataPage<T>> injector;

    public NavigableDataInventoryPage(int size, String title, InventoryItem fillment, Set<InventoryItem> inventoryItems, PlayerMachine owner, BiSupplier<T,
            NavigableDataPage<T>> injector) {
        super(size, title, fillment, inventoryItems, owner);
        this.injector = injector;
        this.pageData = null;
    }

    public NavigableDataInventoryPage(int size, String title, InventoryItem fillment, Set<InventoryItem> inventoryItems, Callback onClose, PlayerMachine owner,
                                      BiSupplier<T, NavigableDataPage<T>> injector) {
        super(size, title, fillment, inventoryItems, onClose, owner);
        this.injector = injector;
        this.pageData = null;
    }

    @Override
    public T getStoredData() {
        return pageData;
    }

    public BiSupplier<T, NavigableDataPage<T>> getInjector() {
        return injector;
    }

    public void setInjector(BiSupplier<T, NavigableDataPage<T>> injector) {
        this.injector = injector;
    }

    @Override
    public NavigableDataInventoryPage<T> setStoredData(T data) {
        this.pageData = data;
        return this;
    }
}
