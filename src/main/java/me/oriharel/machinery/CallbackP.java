package me.oriharel.machinery;

@FunctionalInterface
public interface CallbackP<P> {
    void apply(P param);
}
