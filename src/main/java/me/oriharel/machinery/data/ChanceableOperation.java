package me.oriharel.machinery.data;

public interface ChanceableOperation<T, P> extends Chanceable<T> {
    void executeChanceOperation(P param1, double lootModifier);
}
