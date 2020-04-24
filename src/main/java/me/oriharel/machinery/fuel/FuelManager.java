package me.oriharel.machinery.fuel;

import com.google.gson.Gson;
import me.oriharel.machinery.Machinery;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class FuelManager {

    private Machinery machinery;
    private Gson gson;
    private Material defaultType;

    public FuelManager(Machinery machinery) {
        this.machinery = machinery;
        this.gson = new Gson();
        this.defaultType = Material.getMaterial(machinery.getFileManager().getConfig("fuel.yml").get().getString("default_fuel_type"));

    }

    public Fuel getFuel(int amount, int energy) {
        return new Fuel(defaultType, energy, amount);
    }

    public Fuel getFuel(Material material, int amount, int energy) {
        return new Fuel(material, energy, amount);
    }

    public Fuel getFuel(ItemStack itemStack) {
        return new Fuel(itemStack);
    }

    public Fuel getFuel(ItemStack itemStack, int energy) {
        return new Fuel(itemStack, energy);
    }
}
