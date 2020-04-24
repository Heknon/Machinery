package me.oriharel.machinery.utilities;

@FunctionalInterface
public interface BiSupplier<T, U> {
    void apply(T param1, U param2);
}
