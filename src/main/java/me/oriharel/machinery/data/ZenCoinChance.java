package me.oriharel.machinery.data;

import java.util.Objects;

public class ZenCoinChance extends Chance {

    public ZenCoinChance(int minimumAmount, int maximumAmount) {
        super(minimumAmount, maximumAmount);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ZenCoinChance that = (ZenCoinChance) o;
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
        return "ZenCoinChance{" +
                "minimumAmount=" + minimumAmount +
                ", maximumAmount=" + maximumAmount +
                '}';
    }
}
