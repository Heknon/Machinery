package me.oriharel.machinery.inventory;

import me.oriharel.machinery.utilities.BiSupplier;

public interface NavigableDataPage<T> extends NavigableData<T> {
    BiSupplier<T, NavigableDataPage<T>> getInjector();

    void setInjector(BiSupplier<T, NavigableDataPage<T>> injector);
}
