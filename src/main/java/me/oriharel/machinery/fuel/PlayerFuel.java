package me.oriharel.machinery.fuel;

import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class PlayerFuel extends Fuel {

    private int amount;

    public PlayerFuel(String name, Material material, NBTTagCompound nbt, int energy, int amount) {
        super(name, material, nbt, energy);
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public ItemStack getItem() {
        ItemStack itemStack = new ItemStack(getMaterial(), amount);
        net.minecraft.server.v1_15_R1.ItemStack is = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = getNbt();
        tag.setInt("energy", getEnergy());
        is.setTag(tag);
        return CraftItemStack.asBukkitCopy(is);
    }

    @Override
    protected PlayerFuel clone() {
        return new PlayerFuel(getName(), getMaterial(), getNbt().clone(), getEnergy(), amount);
    }
}
