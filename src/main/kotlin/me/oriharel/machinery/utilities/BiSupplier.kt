package me.oriharel.machinery.utilities

/**
 * Callback with two parameters
 * @param <T> parameter one type
 * @param <U> parameter two type
</U></T> */
@FunctionalInterface
interface BiSupplier<T, U> {
    fun apply(param1: T, param2: U)
}