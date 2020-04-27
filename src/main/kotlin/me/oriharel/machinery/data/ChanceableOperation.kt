package me.oriharel.machinery.data

interface ChanceableOperation<T, P> : Chanceable<T?> {
    fun executeChanceOperation(param1: P?, lootModifier: Double)
}