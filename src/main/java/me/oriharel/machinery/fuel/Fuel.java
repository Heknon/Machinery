package me.oriharel.machinery.fuel;

import me.oriharel.machinery.utilities.NMS;
import net.minecraft.server.v1_15_R1.NBTTagInt;
import net.minecraft.server.v1_15_R1.NBTTagString;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Fuel extends ItemStack implements Cloneable {
    private final String name;
    private int energy;
    private String nbtId;

    public Fuel(String name, Material material, String nbtId, int energy) {
        super(material);
        this.energy = energy;
        this.name = name;
        this.nbtId = nbtId;
        setItemMeta(new ItemStack(material, 1).getItemMeta());
        if (nbtId != null) NMS.getItemStackUnhandledNBT(this).put(nbtId, NBTTagString.a(""));
        NMS.getItemStackUnhandledNBT(this).put("energy", NBTTagInt.a(energy));
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
        NMS.getItemStackUnhandledNBT(this).put("energy", NBTTagInt.a(energy));
    }

    public String getName() {
        return name;
    }

    public String getNbtId() {
        return nbtId;
    }

    @Override
    public Fuel clone() {
        return new Fuel(name, getType(), nbtId, energy);
    }
}
