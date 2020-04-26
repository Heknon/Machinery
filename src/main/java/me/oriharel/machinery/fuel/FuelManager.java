package me.oriharel.machinery.fuel;

import com.google.gson.Gson;
import me.oriharel.machinery.Machinery;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class FuelManager {

    private Machinery machinery;
    private Gson gson;
    private Material defaultType;
    private List<String> defaultLore;
    private String defaultDisplayName;

    public FuelManager(Machinery machinery) {
        this.machinery = machinery;
        this.gson = new Gson();
        YamlConfiguration configLoad = machinery.getFileManager().getConfig("fuel.yml").get();
        this.defaultType = Material.getMaterial(configLoad.getString("default_fuel_type"));
        this.defaultLore = configLoad.getStringList("default_lore");
        this.defaultDisplayName = configLoad.getString("default_display_name");

    }

    public Fuel getFuel(int amount, int energy) {
        return new Fuel(defaultType, energy, amount, defaultDisplayName, defaultLore);
    }

    public Fuel getFuel(Material material, int amount, int energy) {
        return new Fuel(material, energy, amount, defaultDisplayName, defaultLore);
    }

    public Fuel getFuel(ItemStack itemStack) {
        return new Fuel(itemStack, defaultLore);
    }

    public Fuel getFuel(ItemStack itemStack, int energy) {
        return new Fuel(itemStack, energy, defaultLore);
    }
}
