package me.oriharel.machinery;

@FunctionalInterface
public interface CallbackR<R> {
    R apply();
}
