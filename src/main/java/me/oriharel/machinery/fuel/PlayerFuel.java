package me.oriharel.machinery.fuel;

import org.bukkit.Material;

public class PlayerFuel extends Fuel implements Cloneable {

    private int baseEnergy;

    public PlayerFuel(String name, Material material, String nbtId, int energy, int amount) {
        super(name, material, nbtId, energy * amount);
        this.baseEnergy = energy;
        setAmount(amount);
    }

    @Override
    public int getEnergy() {
        return super.getEnergy();
    }

    @Override
    public void setEnergy(int energy) {
        super.setEnergy(energy * getAmount());
    }

    public int getBaseEnergy() {
        return baseEnergy;
    }

    public void setEnergySloppy(int energy) {
        super.setEnergy(energy);
    }

    @Override
    public void setAmount(int amount) {
        super.setAmount(amount);
        setEnergy(baseEnergy);
    }

    @Override
    public PlayerFuel clone() {
        Fuel fuel = super.clone();
        return new PlayerFuel(fuel.getName(), fuel.getType(), fuel.getNbtId(), baseEnergy, fuel.getAmount());
    }

    @Override
    public String toString() {
        return "PlayerFuel{" +
                "baseEnergy=" + baseEnergy +
                ",energy=" + getEnergy() +
                '}';
    }
}
