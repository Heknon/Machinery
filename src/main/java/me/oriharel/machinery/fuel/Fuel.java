package me.oriharel.machinery.fuel;

import me.oriharel.machinery.utilities.NMS;
import net.minecraft.server.v1_15_R1.NBTTagByte;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import net.minecraft.server.v1_15_R1.NBTTagInt;
import net.minecraft.server.v1_15_R1.NBTTagString;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class Fuel extends ItemStack implements Cloneable {

    private static final String ENERGY_NBT_KEY = "fuel_energy";
    private static final String FUEL_ITEM_NBT_IDENTIFIER = "machine_fuel";

    private int energy;

    public Fuel(Material material, int energy, int amount) {
        super(material, amount);
        this.energy = energy * amount;
        setItemMeta(new ItemStack(material, 1).getItemMeta());
        NMS.getItemStackUnhandledNBT(this).put(ENERGY_NBT_KEY, NBTTagInt.a(energy));
        NMS.getItemStackUnhandledNBT(this).put(FUEL_ITEM_NBT_IDENTIFIER, NBTTagByte.a(true));
    }

    public Fuel(ItemStack itemStack) {
        super(itemStack);
        NBTTagCompound compound = CraftItemStack.asNMSCopy(itemStack).getTag();
        this.energy = compound.getInt(ENERGY_NBT_KEY);
    }

    public Fuel(ItemStack itemStack, int energy) {
        super(itemStack);
        NMS.getItemStackUnhandledNBT(this).put(ENERGY_NBT_KEY, NBTTagInt.a(energy));
        NMS.getItemStackUnhandledNBT(this).put(FUEL_ITEM_NBT_IDENTIFIER, NBTTagByte.a(true));
    }

    public int getBaseEnergy() {
        return energy;
    }

    public int getEnergy() {
        return energy * getAmount();
    }

    public void setEnergy(int energy) {
        this.energy = energy;
        NMS.getItemStackUnhandledNBT(this).put(ENERGY_NBT_KEY, NBTTagInt.a(energy));
    }

    @Override
    public Fuel clone() {
        ItemStack item = super.clone();
        item.setAmount(getAmount());
        return new Fuel(item, energy);
    }

    @Override
    public String toString() {
        return "Fuel{" +
                "energy=" + energy +
                '}';
    }
}
