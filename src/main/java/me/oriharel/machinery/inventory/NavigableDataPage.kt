package me.oriharel.machinery.inventory

import me.oriharel.machinery.utilities.BiSupplier

interface NavigableDataPage<T> : NavigableData<T> {
    fun getInjector(): BiSupplier<T, NavigableDataPage<T>>
    fun setInjector(injector: BiSupplier<T, NavigableDataPage<T>>)
}