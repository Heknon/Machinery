package me.oriharel.machinery.fuel;

import com.google.gson.Gson;
import me.oriharel.machinery.Machinery;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class FuelManager {

    private Machinery machinery;
    private Gson gson;
    private Set<Fuel> fuels;
    private HashMap<String, Fuel> nbtIds;

    public FuelManager(Machinery machinery) {
        this.machinery = machinery;
        this.gson = new Gson();
        this.fuels = new HashSet<>();
        nbtIds = new HashMap<String, Fuel>();
        initializeFuels();
    }

    public Fuel getFuelByName(String name) {
        return fuels.stream().filter(f -> f.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }

    public PlayerFuel getPlayerFuelItem(String fuelName, int amount) {
        Fuel fuel = getFuelByName(fuelName);
        if (fuel == null) return null;
        fuel = fuel.clone();
        return new PlayerFuel(fuel.getName(), fuel.getType(), fuel.getNbtId(), fuel.getEnergy(), amount);
    }

    public PlayerFuel getPlayerFuelItem(Material material, int amount) {
        Optional<Fuel> fuelOptional = fuels.stream().filter(f -> f.getType() == material).findAny();
        if (!fuelOptional.isPresent()) return null;
        Fuel fuel = fuelOptional.get();
        return new PlayerFuel(fuel.getName(), fuel.getType(), fuel.getNbtId(), fuel.getEnergy(), amount);
    }

    public PlayerFuel getPlayerFuelItemByNbtId(String nbtId, int amount) {
        Fuel fuel = nbtIds.get(nbtId).clone();
        return new PlayerFuel(fuel.getName(), fuel.getType(), fuel.getNbtId(), fuel.getEnergy(), amount);
    }

    private void initializeFuels() {
        YamlConfiguration configLoad =
                Machinery.getInstance()
                        .getFileManager()
                        .getConfig("fuels.yml")
                        .get();
        for (String key : configLoad.getKeys(false)) {
            fuels.add(initializeFuel(key, configLoad));
        }
    }

    private Fuel initializeFuel(String fuelName, YamlConfiguration configLoad) {
        Material material = Material.getMaterial(configLoad.getString(fuelName.concat(".material"), "___"));
        String nbtString = configLoad.getString(fuelName + ".nbt");
        int energy = configLoad.getInt(fuelName.concat(".energy"), -1);
        if (energy <= 0)
            throw new RuntimeException(
                    "Invalid energy for fuel amount. Fuel energy most be above 0. Check config to see if you defined energy.");
        Fuel fuel = new Fuel(fuelName, material, nbtString, energy);
        if (nbtString != null) {
            nbtIds.put(nbtString, fuel);
        }
        return fuel;
    }

    public HashMap<String, Fuel> getNbtIds() {
        return nbtIds;
    }

    public Set<Fuel> getFuels() {
        return fuels;
    }
}
