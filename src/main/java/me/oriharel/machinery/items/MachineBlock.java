package me.oriharel.machinery.items;

import com.google.gson.Gson;
import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.exceptions.MachineNotFoundException;
import me.oriharel.machinery.machine.Machine;
import me.oriharel.machinery.machine.MachineFactory;
import me.oriharel.machinery.machine.PlayerMachine;
import me.oriharel.machinery.utilities.NMS;
import me.oriharel.machinery.utilities.Utils;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import net.minecraft.server.v1_15_R1.NBTTagString;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Inject Machine into recipe NBT
 */
public class MachineBlock {

    private transient final CustomRecipe<?> recipe;
    private transient final Machine machine;
    private transient final Gson gson;

    public MachineBlock(CustomRecipe<?> recipe, Machine machine, MachineFactory factory) {
        this.gson = Utils.getGsonSerializationBuilderInstance(machine.getClass(), factory);
        this.recipe = recipe;
        this.machine = machine;
    }

    public MachineBlock(ItemStack itemStack, MachineFactory factory, Class<? extends Machine> machineType) throws MachineNotFoundException {
        this.gson = Utils.getGsonSerializationBuilderInstance(machineType, factory);
        net.minecraft.server.v1_15_R1.ItemStack is = CraftItemStack.asNMSCopy(itemStack);

        if (!is.hasTag() || (!is.getTag().hasKey("playerMachine") && !is.getTag().hasKey("machine"))) throw new MachineNotFoundException("Machine not found in " +
                "ItemStack passed to MachineBlock constructor.");

        if (is.getTag().hasKey("playerMachine")) {
            String data = is.getTag().getString("playerMachine");
            this.machine =
                    gson.fromJson(data, PlayerMachine.class);
            this.recipe = this.machine.getRecipe();
        } else if (is.getTag().hasKey("machine")) {
            String data = is.getTag().getString("machine");
            this.machine = gson.fromJson(data, Machine.class);
            this.recipe = this.machine.getRecipe();
        } else {
            machine = null;
            recipe = null;
        }
    }

    public ItemStack getItemStackWithAppliedPlaceholders() {
        if (machine.getClass() == PlayerMachine.class) {
            YamlConfiguration configLoad = Machinery.getInstance().getFileManager().getConfig("machines.yml").get();
            ItemStack is = new ItemStack(recipe.getResult().getType(), 1);
            ItemMeta meta = is.getItemMeta();

            meta.setDisplayName(applyPlaceholders(configLoad.getString(machine.getMachineName() + ".deconstructedItem.displayName")));
            meta.setLore(configLoad.getStringList(machine.getMachineName() + ".deconstructedItem.lore"));
            meta.setLore(meta.getLore().stream().map(this::applyPlaceholders).collect(Collectors.toList()));
            is.setItemMeta(meta);
            NMS.getItemStackUnhandledNBT(is).put("playerMachine", NBTTagString.a(gson.toJson(machine, PlayerMachine.class)));
            return is;
        } else if (machine.getClass() == Machine.class) {
            return recipe.getResult();
        }
        return null;
    }

    private String applyPlaceholders(String string) {
        string = string.replaceAll("%total_resources_gained%",
                String.valueOf(machine instanceof PlayerMachine ? (int)((PlayerMachine) machine).getTotalResourcesGained() : 0));
        string = string.replaceAll("%resources_gained%",
                String.valueOf(machine instanceof PlayerMachine ?
                        ((PlayerMachine) machine).getResourcesGained().values().stream().mapToInt(ItemStack::getAmount).sum() : 0));
        string = string.replaceAll("%total_zen_coins_gained%",
                String.valueOf(machine instanceof PlayerMachine ? (int)((PlayerMachine) machine).getTotalZenCoinsGained() : 0));
        string = string.replaceAll("%zen_coins_gained%",
                String.valueOf(machine instanceof PlayerMachine ? (int)((PlayerMachine) machine).getZenCoinsGained() : 0));
        string = string.replaceAll("%energy%", String.valueOf(machine instanceof PlayerMachine ?
                ((PlayerMachine) machine).getEnergyInMachine() : 0));
        return string;
    }

    public CustomRecipe<?> getRecipe() {
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
