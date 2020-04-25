package me.oriharel.machinery.utilities;

/**
 * Callback with two parameters
 * @param <T> parameter one type
 * @param <U> parameter two type
 */
@FunctionalInterface
public interface BiSupplier<T, U> {
    void apply(T param1, U param2);
}
