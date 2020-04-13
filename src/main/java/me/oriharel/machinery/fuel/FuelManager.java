package me.oriharel.machinery.fuel;

import com.google.gson.Gson;
import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.utilities.NMS;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64;

import java.util.Map;
import java.util.Set;

public class FuelManager {

    private Machinery machinery;
    private Gson gson;
    private Set<Fuel> fuels;

    public FuelManager(Machinery machinery) {
        this.machinery = machinery;
        this.gson = new Gson();
        initializeFuels();
    }

    private void initializeFuels() {
        YamlConfiguration configLoad =
                Machinery.getInstance()
                        .getFileManager()
                        .getConfig("fuels.yml")
                        .get();
        for (String key : configLoad.getKeys(false)) {
            Bukkit.getScheduler().runTaskAsynchronously(machinery, () -> fuels.add(initializeFuel(key, configLoad)));
        }
    }

    private Fuel initializeFuel(String fuelName, YamlConfiguration configLoad) {
        Material material = Material.getMaterial(configLoad.getString(fuelName.concat(".material"), "___"));
        NBTTagCompound nbt = NMS.nbtFromMap(gson.fromJson(new String(Base64.decodeBase64(configLoad.getString(fuelName + ".nbt"))), Map.class));
        int energy = configLoad.getInt(fuelName.concat(".energy"), -1);
        if (energy <= 0)
            throw new RuntimeException(
                    "Invalid energy for fuel amount. Fuel energy most be above 0. Check config to see if you defined energy.");
        else if (material == null || nbt.getString("") != null)
            throw new RuntimeException(
                    "There is no way to recognize the fuel made. You must set material or material and nbt"
                            + " in fuels.yml");
        return new Fuel(material, nbt, energy);
    }

    public Set<Fuel> getFuels() {
        return fuels;
    }
}
