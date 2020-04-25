package me.oriharel.machinery.utilities;

/**
 * Callbacks with only one parameter
 * @param <P> type of parameter
 */
@FunctionalInterface
public interface CallbackP<P> {
    void apply(P param);
}
