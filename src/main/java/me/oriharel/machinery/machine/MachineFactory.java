package me.oriharel.machinery.machine;

import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.exceptions.MachineNotFoundException;
import me.oriharel.machinery.exceptions.MaterialNotFoundException;
import me.oriharel.machinery.exceptions.NotMachineTypeException;
import me.oriharel.machinery.exceptions.RecipeNotFoundException;
import me.oriharel.machinery.structure.Structure;
import me.oriharel.machinery.upgrades.AbstractUpgrade;
import me.oriharel.machinery.utilities.NMS;
import me.oriharel.machinery.utilities.Utils;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import net.minecraft.server.v1_15_R1.NBTTagString;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MachineFactory {

    private Machinery machinery;

    public MachineFactory(Machinery machinery) {
        this.machinery = machinery;
    }

    /**
     * Create a new machine it's config name
     *
     * @param machineKey config name in machines.yml
     * @return Machine representing the config data
     * @throws IllegalArgumentException  machine key is null
     * @throws MachineNotFoundException  machine not found in config
     * @throws MaterialNotFoundException machine core type is not a valid type
     * @throws RecipeNotFoundException   recipe made for machine is not found
     * @throws NotMachineTypeException   the machine type given is not a real machine type {@see MachineType}
     */
    @Nullable
    public Machine createMachine(String machineKey) throws IllegalArgumentException, MachineNotFoundException, MaterialNotFoundException,
            RecipeNotFoundException, NotMachineTypeException {
        if (machineKey == null) throw new IllegalArgumentException("Machine machineKey must not be null (MachineFactory)");
        YamlConfiguration configLoad = machinery.getFileManager().getConfig("machines.yml").get();
        if (!configLoad.isConfigurationSection(machineKey)) throw new MachineNotFoundException("The machine with the key of \"" + machineKey + "\" was not found in " +
                "machines.yml.");
        ConfigurationSection section = machinery.getFileManager().getConfig("machines.yml").get().getConfigurationSection(machineKey);
        assert section != null;
        String machineCoreBlockTypeString = section.getString("open_gui_block_type");
        if (machineCoreBlockTypeString == null)
            throw new MaterialNotFoundException("You must give a open_gui_block_type in the machine section of machine " + machineKey);
        Material machineCoreBlockType = Material.getMaterial(machineCoreBlockTypeString);
        if (machineCoreBlockType == null) throw new MaterialNotFoundException("No material named \"" + machineCoreBlockTypeString + "\" was found.");
        int maxFuel = section.getInt("max_fuel", 0);
        int fuelDeficiency = section.getInt("deficiency", 0);
        String recipeName = section.getString("recipe", null);
        if (recipeName == null) throw new NullPointerException("No recipe given in machine section for machine named " + machineKey);
        recipeName = recipeName.replaceAll("\\|", ":");
        CustomRecipe<?> recipe = CustomCrafting.getRecipeHandler().getRecipe(recipeName);
        if (recipe == null) throw new RecipeNotFoundException("Recipe with the name " + recipeName + " given in the machine section of " + machineKey + " has not been " +
                "located.");
        MachineType machineType = MachineType.getMachine(section.getString("type", null));
        Structure structure =
                machinery.getStructureManager().getSchematicByPath(new File(machinery.getDataFolder(), "structures/" + machineKey + Machinery.STRUCTURE_EXTENSION).getPath());
        return createMachine(maxFuel, fuelDeficiency, machineType, structure, recipe, machineKey, machineCoreBlockType);
    }

    /**
     * Create a machine with already known values
     *
     * @param maxFuel              the max fuel a machine can hold
     * @param fuelDeficiency       the fuel deficiency. the amount of fuel removed every resource get cycle
     * @param machineType          the type of the machine
     * @param structure            the build structure of the machine
     * @param recipe               the recipe used to make the machine
     * @param machineKey           the key of the machine in config aka machine name
     * @param machineCoreBlockType the core block type later used to figure out, when building the machine, where the core block is
     * @return Machine based on values given
     * @throws IllegalArgumentException type give is null
     */
    @Nullable
    public Machine createMachine(int maxFuel,
                                 int fuelDeficiency,
                                 MachineType machineType,
                                 Structure structure,
                                 CustomRecipe<?> recipe,
                                 String machineKey, Material machineCoreBlockType) throws IllegalArgumentException {
        if (machineType == null) throw new IllegalArgumentException("Machine type must not be null (MachineFactory)");
        Machine machine = new Machine(maxFuel, fuelDeficiency, machineType, structure,
                recipe, machineKey, machineCoreBlockType, this);
        machine.setRecipe(injectMachineNBTIntoRecipe(machine.getRecipe(), machine));
        return machine;
    }

    @Nullable
    public PlayerMachine createMachine(int maxFuel,
                                       int fuelDeficiency,
                                       MachineType machineType,
                                       Structure structure,
                                       CustomRecipe<?> recipe,
                                       String machineKey, Material machineCoreBlockType, double totalResourcesGained,
                                       HashMap<Material, ItemStack> resourcesGained,
                                       int energyInMachine, Location machineCoreBlockLocation, double zenCoinsGained, double totalZenCoinsGained, UUID owner,
                                       List<AbstractUpgrade> upgrades, Set<UUID> playersWithAccessPermission) throws IllegalArgumentException {
        if (machineType == null) throw new IllegalArgumentException("Machine type must not be null (MachineFactory)");
        return new PlayerMachine(maxFuel, fuelDeficiency, machineType, structure,
                recipe, machineKey, machineCoreBlockType, playersWithAccessPermission, totalResourcesGained, resourcesGained, energyInMachine, machineCoreBlockLocation,
                zenCoinsGained, totalZenCoinsGained, owner, upgrades, this);
    }

    public PlayerMachine createMachine(Machine machine, Location machineCoreBlockLocation, double totalResourcesGained,
                                       int energyInMachine, double zenCoinsGained, double totalZenCoinsGained, UUID owner,
                                       List<AbstractUpgrade> upgrades, HashMap<Material, ItemStack> resourcesGained, Set<UUID> playersWithAccessPermission) throws IllegalArgumentException {
        if (machine == null) throw new IllegalArgumentException("Machine must not be null (MachineFactory)");

        return createMachine(machine.maxFuel, machine.fuelDeficiency, machine.machineType, machine.structure, machine.recipe, machine.machineName,
                machine.machineCoreBlockType, totalResourcesGained, resourcesGained, energyInMachine, machineCoreBlockLocation, zenCoinsGained, totalZenCoinsGained,
                owner, upgrades, playersWithAccessPermission);
    }

    /**
     * Inject machine data into a recipe
     * used so that when crafting a recipe you will have inside the NBT (data) of the itemstack a serialized machine so that later on you could build it
     * @param recipe the recipe to inject into
     * @param machine the machine to inject into the recipe
     * @return the newly injected recipe
     */
    private CustomRecipe<?> injectMachineNBTIntoRecipe(CustomRecipe<?> recipe, Machine machine) {
        List<CustomItem> results = recipe.getCustomResults();
        for (CustomItem item : results) {
            NMS.getItemStackUnhandledNBT(item).put("machine",
                    NBTTagString.a(Utils.getGsonSerializationBuilderInstance(PlayerMachine.class, this).toJson(machine,
                            Machine.class)));
        }
        return recipe;
    }

    public Machinery getMachinery() {
        return machinery;
    }
}
