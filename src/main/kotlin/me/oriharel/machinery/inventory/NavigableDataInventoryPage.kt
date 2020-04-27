package me.oriharel.machinery.inventory

import me.oriharel.machinery.machines.machine.PlayerMachine

class NavigableDataInventoryPage<T> : InventoryPage, NavigableDataPage<T?> {


    constructor(size: Int, title: String?, fillment: InventoryItem?, inventoryItems: Set<InventoryItem?>?, owner: PlayerMachine, injector: (T?, NavigableDataPage<T?>) -> Unit) : super(size, title, fillment, inventoryItems, owner) {
        this.injector = injector
        storedData = null
    }

    constructor(size: Int, title: String?, fillment: InventoryItem?, inventoryItems: Set<InventoryItem?>?, onClose: () -> Unit, owner: PlayerMachine,
                injector: (T?, NavigableDataPage<T?>) -> Unit) : super(size, title, fillment, inventoryItems, onClose, owner) {
        this.injector = injector
        storedData = null
    }


    override var injector: (T?, NavigableDataPage<T?>) -> Unit
    override var storedData: T?
}