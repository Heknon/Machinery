package me.oriharel.machinery.fuel;

import com.google.gson.annotations.JsonAdapter;
import me.oriharel.machinery.serialization.NBTTagCompoundTypeAdapter;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

class Fuel {

    private final Material material;
    @JsonAdapter(NBTTagCompoundTypeAdapter.class)
    private final NBTTagCompound nbt;
    private int energy;

    public Fuel(Material material, NBTTagCompound nbt, int energy) {
        this.material = material;
        this.nbt = nbt;
        this.energy = energy;
    }

    public ItemStack getItem(int amount) {
        ItemStack itemStack = new ItemStack(material, amount);
        net.minecraft.server.v1_15_R1.ItemStack is = CraftItemStack.asNMSCopy(itemStack);
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
}
