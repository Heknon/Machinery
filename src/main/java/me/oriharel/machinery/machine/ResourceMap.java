package me.oriharel.machinery.machine;

import com.mojang.datafixers.util.Pair;
import me.oriharel.machinery.data.ChanceableOperation;
import me.oriharel.machinery.data.MaterialChance;
import me.oriharel.machinery.data.Range;
import me.oriharel.machinery.data.ZenCoinChance;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceMap extends HashMap<Integer, ChanceableOperation<?, MachineResourceGetProcess>> {

    public ResourceMap(String machineName, YamlConfiguration configLoad) {
        ConfigurationSection resourcesSection = configLoad.getConfigurationSection(machineName).getConfigurationSection("resources");

        Map<String, Object> loadedSection = resourcesSection.getValues(true);
        // branch level one corresponds to Integer, Map<String, Map<String, Integer>> integer = weight
        for (Map.Entry<String, Object> entryLevelOne : loadedSection.entrySet()) {
            int weight = Integer.parseInt(entryLevelOne.getKey());
            Map<String, Object> levelTwo = (Map<String, Object>) entryLevelOne.getValue();
            List<Pair<Material, Range>> materialChanceDelegate = new ArrayList<>();
            for (Map.Entry<String, Object> entryLevelTwo : levelTwo.entrySet()) { // branch level two entry corresponds to String, Map<String, Integer> String is material
                Material material = Material.getMaterial(entryLevelTwo.getKey());
                Map<String, Object> levelThree = (Map<String, Object>) entryLevelTwo.getValue(); // branch level three corresponds to - String, Integer - min/max
                if (entryLevelTwo.getKey().equalsIgnoreCase("zencoins")) {
                    int min = (int) levelThree.get("min");
                    int max = (int) levelThree.get("min");
                    put(weight, new ZenCoinChance(new Range(min, max)));
                    continue;
                }
                int min = (int) levelThree.get("min");
                int max = (int) levelThree.get("min");
                materialChanceDelegate.add(Pair.of(material, new Range(min, max)));
            }
            put(weight, new MaterialChance(materialChanceDelegate));
        }
    }

}
