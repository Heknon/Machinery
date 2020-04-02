package me.oriharel.machinery.items;

import me.oriharel.customrecipes.recipe.Recipe;
import me.oriharel.machinery.exceptions.MachineNotFoundException;
import me.oriharel.machinery.machine.Machine;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.Objects;

/**
 * Inject Machine into recipe NBT
 */
public class MachineBlock {

    private final Recipe recipe;
    private final Machine machine;

    public MachineBlock(Recipe recipe, Machine machine) throws IOException {
        this.recipe = recipe;
        this.machine = machine;
        ItemStack itemStack = recipe.getResult().getItemStackWithNBT();
        net.minecraft.server.v1_15_R1.ItemStack nmsIs = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tagCompound = nmsIs.getTag();
        if (tagCompound == null) tagCompound = new NBTTagCompound();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(machine);
        oos.flush();
        byte[] data = bos.toByteArray();
        tagCompound.setByteArray("machine", data);
    }

    public MachineBlock(ItemStack itemStack) throws MachineNotFoundException, IOException, ClassNotFoundException {
        net.minecraft.server.v1_15_R1.ItemStack is = CraftItemStack.asNMSCopy(itemStack);
        if (!is.hasTag() || !is.getTag().hasKey("machine")) throw new MachineNotFoundException("Machine not found in ItemStack passed to MachineBlock constructor.");
        byte[] data = is.getTag().getByteArray("machine");
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(in);
        this.machine = (Machine) ois.readObject();
        this.recipe = this.machine.getRecipe();
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public Machine getMachine() {
        return machine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MachineBlock that = (MachineBlock) o;
        return Objects.equals(recipe, that.recipe) &&
                Objects.equals(machine, that.machine);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipe, machine);
    }
}
