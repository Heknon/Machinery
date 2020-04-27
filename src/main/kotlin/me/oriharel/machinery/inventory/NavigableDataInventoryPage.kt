package me.oriharel.machinery.inventory

import me.oriharel.machinery.machine.PlayerMachine
import me.oriharel.machinery.utilities.BiSupplier
import me.oriharel.machinery.utilities.Callback

class NavigableDataInventoryPage<T> : InventoryPage, NavigableDataPage<T> {
    override var storedData: T
        private set
    private var injector: BiSupplier<T, NavigableDataPage<T>>

    constructor(size: Int, title: String?, fillment: InventoryItem?, inventoryItems: Set<InventoryItem?>?, owner: PlayerMachine, injector: BiSupplier<T, NavigableDataPage<T>>) : super(size, title, fillment, inventoryItems, owner) {
        this.injector = injector
        storedData = null
    }

    constructor(size: Int, title: String?, fillment: InventoryItem?, inventoryItems: Set<InventoryItem?>?, onClose: Callback?, owner: PlayerMachine,
                injector: BiSupplier<T, NavigableDataPage<T>>) : super(size, title, fillment, inventoryItems, onClose, owner) {
        this.injector = injector
        storedData = null
    }

    override fun getInjector(): BiSupplier<T, NavigableDataPage<T>> {
        return injector
    }

    override fun setInjector(injector: BiSupplier<T, NavigableDataPage<T>>) {
        this.injector = injector
    }

    override fun setStoredData(data: T): NavigableDataInventoryPage<T> {
        storedData = data
        return this
    }
}