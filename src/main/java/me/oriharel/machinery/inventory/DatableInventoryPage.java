package me.oriharel.machinery.inventory;

import me.oriharel.machinery.machine.PlayerMachine;
import me.oriharel.machinery.utilities.Callback;

import java.util.Set;

public class DatableInventoryPage<T> extends InventoryPage implements Datable<T> {

    private T pageData;

    public DatableInventoryPage(int size, String title, InventoryItem fillment, Set<InventoryItem> inventoryItems, PlayerMachine owner) {
        super(size, title, fillment, inventoryItems, owner);
        this.pageData = null;
    }

    public DatableInventoryPage(int size, String title, InventoryItem fillment, Set<InventoryItem> inventoryItems, Callback onClose, PlayerMachine owner) {
        super(size, title, fillment, inventoryItems, onClose, owner);
        this.pageData = null;
    }

    @Override
    public T getStoredData() {
        return pageData;
    }

    @Override
    public DatableInventoryPage<T> setStoredData(T data) {
        this.pageData = data;
        return this;
    }
}
