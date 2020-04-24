package me.oriharel.machinery.data;

import me.oriharel.machinery.machine.MachineResourceGetProcess;

import java.util.Objects;

public class ZenCoinChance implements ChanceableOperation<Integer, MachineResourceGetProcess> {

    private Range range;

    public ZenCoinChance(Range range) {
        this.range = range;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ZenCoinChance that = (ZenCoinChance) o;
        return Objects.equals(range, that.range);
    }

    @Override
    public int hashCode() {
        return Objects.hash(range);
    }

    @Override
    public String toString() {
        return "ZenCoinChance{" +
                "range=" + range +
                '}';
    }

    @Override
    public Integer getChanced() {
        return range.random();
    }

    @Override
    public void executeChanceOperation(MachineResourceGetProcess machineResourceGetProcess) {
        machineResourceGetProcess.addZenCoinsGained(getChanced());
    }
}
