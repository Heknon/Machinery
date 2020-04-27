package me.oriharel.machinery.message

import me.oriharel.machinery.resources.chance.Chanceable

interface ChanceableOperation<T, P> : Chanceable<T?> {
    fun executeChanceOperation(param1: P?, lootModifier: Double)
}