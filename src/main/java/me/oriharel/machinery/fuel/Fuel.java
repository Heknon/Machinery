package me.oriharel.machinery.fuel;

import com.google.gson.annotations.JsonAdapter;
import me.oriharel.machinery.serialization.NBTTagCompoundTypeAdapter;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

class Fuel implements Cloneable {
    private final String name;
    private final Material material;
    @JsonAdapter(NBTTagCompoundTypeAdapter.class)
    private final NBTTagCompound nbt;
    private int energy;

    public Fuel(String name, Material material, NBTTagCompound nbt, int energy) {
        this.material = material;
        this.nbt = nbt;
        this.energy = energy;
        this.name = name;
    }

    public ItemStack getItem(int amount) {
        ItemStack itemStack = new ItemStack(material, amount);
        net.minecraft.server.v1_15_R1.ItemStack is = CraftItemStack.asNMSCopy(itemStack);
        nbt.setInt("energy", energy);
        is.setTag(nbt);
        return CraftItemStack.asBukkitCopy(is);
    }

    public Material getMaterial() {
        return material;
    }

    public NBTTagCompound getNbt() {
        return nbt;
    }

    public int getEnergy() {
        return energy;
    }

    public String getName() {
        return name;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    protected Fuel clone() {
        return new Fuel(name, material, nbt.clone(), energy);
    }
}
