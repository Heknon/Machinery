package me.oriharel.machinery.resources.chance

interface Chanceable<T> {
    fun getChanced(lootModifier: Double): T?
}