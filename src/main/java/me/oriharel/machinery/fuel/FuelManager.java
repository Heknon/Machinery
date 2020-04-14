package me.oriharel.machinery.fuel;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.utilities.NMS;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FuelManager {

    private Machinery machinery;
    private Gson gson;
    private Set<Fuel> fuels;

    public FuelManager(Machinery machinery) {
        this.machinery = machinery;
        this.gson = new Gson();
        this.fuels = new HashSet<>();
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
        String nbtString = configLoad.getString(fuelName + ".nbt");
        NBTTagCompound nbt = null;
        Map<String, Object> nbtMap = null;
        if (nbtString != null)
            try {
                nbtMap = gson.fromJson(new String(Base64.decodeBase64(nbtString)), Map.class);
            } catch (JsonSyntaxException e) {
                Bukkit.getLogger().severe("fuels.yml has an invalid NBT field. Please enter valid NBT data.");
            }
        if (nbtMap != null) nbt = NMS.nbtFromMap(nbtMap);
        int energy = configLoad.getInt(fuelName.concat(".energy"), -1);
        if (energy <= 0)
            throw new RuntimeException(
                    "Invalid energy for fuel amount. Fuel energy most be above 0. Check config to see if you defined energy.");
        return new Fuel(fuelName, material, nbt, energy);
    }

    public Set<Fuel> getFuels() {
        return fuels;
    }
}
