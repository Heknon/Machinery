package me.oriharel.machinery.data;

import me.oriharel.machinery.machine.MachineResourceGetProcess;

import java.util.ArrayList;

public class ChancableList<T extends ChanceableOperation<?, MachineResourceGetProcess>> extends ArrayList<T> implements ChanceableOperation<T,
        MachineResourceGetProcess> {
    @Override
    public void executeChanceOperation(MachineResourceGetProcess param1, double lootModifier) {
        for (T t : this) {
            t.executeChanceOperation(param1, lootModifier);
        }
    }

    @Override
    public T getChanced(double lootModifier) {
        return null;
    }
}
