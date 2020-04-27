package me.oriharel.machinery.utilities

/**
 * Callbacks with only one parameter
 * @param <P> type of parameter
</P> */
@FunctionalInterface
interface CallbackP<P> {
    fun apply(param: P)
}