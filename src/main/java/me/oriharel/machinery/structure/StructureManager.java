package me.oriharel.machinery.structure;

import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.utilities.Callback;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import schematics.Schematic;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * used to register all structures found in machines.yml
 */
public class StructureManager {
    private HashMap<String, Structure> structures;
    private Machinery machinery;
    private List<Callback> callbacksOnDone;

    public StructureManager(Machinery machinery) {
        this.machinery = machinery;
        this.structures = new HashMap<>();
        this.callbacksOnDone = new ArrayList<>();
        Bukkit.getScheduler().runTaskAsynchronously(machinery, this::registerSchematics);
    }

    /**
     * Get a schematic by it's path in jar
     *
     * @param path File#getPath
     * @return Schematic or null if not found
     */
    public Structure getSchematicByPath(String path) {
        return structures.getOrDefault(path, null);
    }

    private void registerSchematic(String key) {
        if (structures.containsKey(key)) return;
        File file = new File(machinery.getDataFolder(), "structures/" + key + Machinery.STRUCTURE_EXTENSION);
        Schematic schematic = new Schematic(machinery, file);

        schematic.loadSchematic();

        Structure structure = new Structure(schematic, key);
        structures.put(file.getPath(), structure);
    }

    private void registerSchematics() {
        YamlConfiguration configLoad = machinery.getFileManager().getConfig("machines.yml").get();
        Set<String> keys = configLoad.getKeys(false);
        Bukkit.getScheduler().runTaskAsynchronously(machinery, () -> {
            for (String key : keys) {
                registerSchematic(key);
            }
            Bukkit.getScheduler().runTask(machinery, () -> this.callbacksOnDone.forEach(Callback::apply));
        });

    }

    public void registerOnDoneCallback(Callback callback) {
        this.callbacksOnDone.add(callback);
    }
}

