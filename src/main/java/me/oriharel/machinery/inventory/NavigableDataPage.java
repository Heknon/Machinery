package me.oriharel.machinery.inventory;

import me.oriharel.machinery.utilities.BiSupplier;

/**
 * handles the injection of the data into an InventoryPage using closures
 *
 * @param <T> the type of data stored
 */
public interface NavigableDataPage<T> extends NavigableData<T> {
    BiSupplier<T, NavigableDataPage<T>> getInjector();

    void setInjector(BiSupplier<T, NavigableDataPage<T>> injector);
}
