package me.oriharel.machinery.fuel;

import org.bukkit.Material;

public class PlayerFuel extends Fuel implements Cloneable {

    public PlayerFuel(String name, Material material, String nbtId, int energy, int amount) {
        super(name, material, nbtId, energy);
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
}
