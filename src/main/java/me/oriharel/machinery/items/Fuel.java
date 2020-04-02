package me.oriharel.machinery.items;

import me.oriharel.machinery.Machinery;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;

import java.io.File;

public class Fuel {

  @Nullable private final Material material;
  @Nullable private final NBTTagCompound nbt;
  private int energy;

  public Fuel(NBTTagCompound nbt, int energy) throws Exception {
    this.material = null;
    this.nbt = nbt;
    this.energy = energy;
    if (this.energy <= 0)
      throw new Exception(
          "Invalid energy for fuel amount. Fuel energy most be above 0. Check config to see if you defined energy.");
    else if (nbt == null)
      throw new Exception(
          "There is no way to recognize the fuel made. You must set material or nbt"
              + " in fuels.yml");
  }

  public Fuel(Material material, int energy) throws Exception {
    this.material = material;
    this.nbt = null;
    this.energy = energy;
    if (this.energy <= 0)
      throw new Exception(
          "Invalid energy for fuel amount. Fuel energy most be above 0. Check config to see if you defined energy.");
    else if (this.material == null)
      throw new Exception(
          "There is no way to recognize the fuel made. You must set material or nbt"
              + " in fuels.yml");
  }

  public Fuel(String fuelName) throws Exception {
    FileConfiguration configLoad =
        Machinery.getInstance()
            .getFileManager()
            .getConfig(new File(Machinery.getInstance().getDataFolder(), "fuels.yml"))
            .getFileConfiguration();
    this.material = Material.getMaterial(configLoad.getString(fuelName.concat(".material"), "___"));
    this.nbt = new NBTTagCompound();
    this.nbt.setString(configLoad.getString(fuelName.concat(".nbt"), ""), "");
    this.energy = configLoad.getInt(fuelName.concat(".energy"), -1);
    if (this.energy <= 0)
      throw new Exception(
          "Invalid energy for fuel amount. Fuel energy most be above 0. Check config to see if you defined energy.");
    else if (this.material == null || this.nbt.getString("") != null)
      throw new Exception(
          "There is no way to recognize the fuel made. You must set material or nbt"
              + " in fuels.yml");
  }
}
