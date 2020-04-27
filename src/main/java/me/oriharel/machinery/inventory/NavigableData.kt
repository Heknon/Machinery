package me.oriharel.machinery.inventory

interface NavigableData<T> {
    val storedData: T
    fun setStoredData(data: T): NavigableData<T>
}