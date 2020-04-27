package me.oriharel.machinery.inventory

interface NavigableDataPage<T> : NavigableData<T?> {
    var injector: (T, NavigableDataPage<T>) -> Unit

}