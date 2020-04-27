package me.oriharel.machinery.data

interface Chanceable<T> {
    fun getChanced(lootModifier: Double): T?
}