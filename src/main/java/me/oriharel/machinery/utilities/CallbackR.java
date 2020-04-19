package me.oriharel.machinery.utilities;

@FunctionalInterface
public interface CallbackR<R> {
    R apply();
}
