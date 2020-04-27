package me.oriharel.machinery.fuel;

import me.oriharel.machinery.message.Message;
import me.oriharel.machinery.message.Placeholder;
import me.oriharel.machinery.utilities.NMS;
import net.minecraft.server.v1_15_R1.NBTTagByte;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import net.minecraft.server.v1_15_R1.NBTTagInt;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

public class Fuel extends ItemStack implements Cloneable {

    private static final String ENERGY_NBT_KEY = "fuel_energy";
    private static final String FUEL_ITEM_NBT_IDENTIFIER = "machine_fuel";

    private int energy;
    private List<String> lore;

    public Fuel(Material material, int energy, int amount, String displayName, List<String> lore) {
        super(material, amount);
        this.energy = energy * amount;
        this.lore = lore;
        ItemMeta meta = new ItemStack(material, 1).getItemMeta();
        meta.setDisplayName(new Message(displayName).getAppliedText());
        meta.setLore(lore.stream().map(s -> new Message(s, new Placeholder("%amount%", energy)).getAppliedText()).collect(Collectors.toList()));
        setItemMeta(meta);
        NMS.getItemStackUnhandledNBT(this).put(ENERGY_NBT_KEY, NBTTagInt.a(energy));
        NMS.getItemStackUnhandledNBT(this).put(FUEL_ITEM_NBT_IDENTIFIER, NBTTagByte.a(true));
    }

    public Fuel(ItemStack itemStack, List<String> placeholderLore) {
        super(itemStack);
        this.lore = placeholderLore;
        NBTTagCompound compound = CraftItemStack.asNMSCopy(itemStack).getTag();
        this.energy = compound.getInt(ENERGY_NBT_KEY);
    }

    public Fuel(ItemStack itemStack, int energy, List<String> placeholderLore) {
        super(itemStack);
        this.lore = placeholderLore;
        NMS.getItemStackUnhandledNBT(this).put(ENERGY_NBT_KEY, NBTTagInt.a(energy));
        NMS.getItemStackUnhandledNBT(this).put(FUEL_ITEM_NBT_IDENTIFIER, NBTTagByte.a(true));
    }

    public int getBaseEnergy() {
        return energy;
    }

    public int getEnergy() {
        return energy * getAmount();
    }

    /**
     * sets the energy of a fuel.
     * applies new lore to show the amount of energy the fuel has.
     * sets a new NBT to hide away from the end user the amount of energy in the fuel and for easy access for the programmer
     *
     * @param energy energy to set to
     */
    public void setEnergy(int energy) {
        this.energy = energy;
        ItemMeta meta = getItemMeta();
        meta.setLore(lore.stream().map(s -> new Message(s, new Placeholder("%amount%", energy)).getAppliedText()).collect(Collectors.toList()));
        setItemMeta(meta);
        NMS.getItemStackUnhandledNBT(this).put(ENERGY_NBT_KEY, NBTTagInt.a(energy));
    }

    @Override
    public Fuel clone() {
        ItemStack item = super.clone();
        item.setAmount(getAmount());
        return new Fuel(item, energy, lore);
    }

    @Override
    public String toString() {
        return "Fuel{" +
                "energy=" + energy +
                '}';
    }
}
