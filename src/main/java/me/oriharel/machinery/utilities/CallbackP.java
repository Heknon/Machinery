package me.oriharel.machinery.utilities;

@FunctionalInterface
public interface CallbackP<P> {
    void apply(P param);
}
