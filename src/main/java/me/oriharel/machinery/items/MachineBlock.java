package me.oriharel.machinery.items;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.oriharel.customrecipes.recipe.Recipe;
import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.exceptions.MachineNotFoundException;
import me.oriharel.machinery.fuel.Fuel;
import me.oriharel.machinery.machine.Machine;
import me.oriharel.machinery.machine.PlayerMachine;
import me.oriharel.machinery.serialization.MachineTypeAdapter;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Inject Machine into recipe NBT
 */
public class MachineBlock {

    private transient final Recipe recipe;
    private transient final Machine machine;

    public MachineBlock(Recipe recipe, Machine machine) {
        this.recipe = recipe;
        this.machine = machine;
    }

    public MachineBlock(ItemStack itemStack) throws MachineNotFoundException {
        net.minecraft.server.v1_15_R1.ItemStack is = CraftItemStack.asNMSCopy(itemStack);
        if (!is.hasTag() || !is.getTag().hasKey("machine")) throw new MachineNotFoundException("Machine not found in ItemStack passed to MachineBlock constructor.");
        String data = is.getTag().getString("machine");
        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeHierarchyAdapter(Machine.class,
                new MachineTypeAdapter(Machinery.getInstance().getMachineManager().getMachineFactory())).create();
        this.machine = gson.fromJson(data, Machine.class);
        this.recipe = this.machine.getRecipe();
    }

    public ItemStack getItemStackWithAppliedPlaceholders() {
        ItemStack is = recipe.getResult().getItemStackWithNBT();
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(applyPlaceholders(meta.getDisplayName()));
        meta.setLore(meta.getLore().stream().map(this::applyPlaceholders).collect(Collectors.toList()));
        is.setItemMeta(meta);
        return is;
    }

    private String applyPlaceholders(String string) {
        string = string.replaceAll("%total_resources_gained%",
                String.valueOf(machine instanceof  PlayerMachine ? ((PlayerMachine)machine).getTotalResourcesGained() : 0));
        string = string.replaceAll("%resources_gained%",
                String.valueOf(machine instanceof  PlayerMachine ? ((PlayerMachine)machine).getResourcesGained().stream().mapToInt(ItemStack::getAmount).sum() : 0));
        string = string.replaceAll("%total_zen_coins_gained%",
                String.valueOf(machine instanceof  PlayerMachine ? ((PlayerMachine)machine).getTotalZenCoinsGained() : 0));
        string = string.replaceAll("%zen_coins_gained%",
                String.valueOf(machine instanceof  PlayerMachine ? ((PlayerMachine)machine).getZenCoinsGained() : 0));
        string = string.replaceAll("%energy%", String.valueOf(machine instanceof PlayerMachine ?
                ((PlayerMachine) machine).getFuels().stream().mapToInt(Fuel::getEnergy).sum() : 0));
        return string;
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
        return Objects.hash(recipe);
    }
}
