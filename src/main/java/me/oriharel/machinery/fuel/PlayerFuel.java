package me.oriharel.machinery.fuel;

import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.Material;

public class PlayerFuel extends Fuel {

    private int amount;

    public PlayerFuel(String name, Material material, NBTTagCompound nbt, int energy, int amount) {
        super(name, material, nbt, energy);
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }
}
