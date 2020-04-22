package me.oriharel.machinery.data;

import java.util.Objects;

public class Chance {
    protected final int minimumAmount;
    protected final int maximumAmount;

    public Chance(int minimumAmount, int maximumAmount) {
        this.minimumAmount = minimumAmount;
        this.maximumAmount = maximumAmount;
    }


    public int getMinimumAmount() {
        return minimumAmount;
    }

    public int getMaximumAmount() {
        return maximumAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chance that = (Chance) o;
        return
                minimumAmount == that.minimumAmount &&
                        maximumAmount == that.maximumAmount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minimumAmount, maximumAmount);
    }

    @Override
    public String toString() {
        return "Chance{" +
                "minimumAmount=" + minimumAmount +
                ", maximumAmount=" + maximumAmount +
                '}';
    }
}
