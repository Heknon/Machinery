package me.oriharel.machinery.machine;

import com.mojang.datafixers.util.Pair;
import me.oriharel.machinery.data.*;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceMap extends HashMap<Integer, ChancableList<? extends ChanceableOperation<?, MachineResourceGetProcess>>> {

    public ResourceMap(String machineName, YamlConfiguration configLoad) {
        ConfigurationSection resourcesSection = configLoad.getConfigurationSection(machineName).getConfigurationSection("resources");

        Map<String, Object> serializedSection = resourcesSection.getValues(false);
        // branch level one corresponds to Integer, Map<String, Map<String, Integer>> integer = weight
        for (Map.Entry<String, Object> entryLevelOne : serializedSection.entrySet()) {
            int weight = Integer.parseInt(entryLevelOne.getKey());
            Map<String, Object> levelTwo = ((MemorySection) entryLevelOne.getValue()).getValues(false);
            List<Pair<Material, Range>> materialChanceDelegate = new ArrayList<>();
            ChancableList<ChanceableOperation<?, MachineResourceGetProcess>> chancableList = new ChancableList<>();
            for (Map.Entry<String, Object> entryLevelTwo : levelTwo.entrySet()) { // branch level two entry corresponds to String, Map<String, Integer> String is material
                Material material = Material.getMaterial(entryLevelTwo.getKey());
                MemorySection levelThree = (MemorySection) entryLevelTwo.getValue(); // branch level three corresponds to - String, Integer - min/max
                int min = levelThree.getInt("min");
                int max = levelThree.getInt("min");
                if (entryLevelTwo.getKey().equalsIgnoreCase("zencoins")) {
                    chancableList.add(new ZenCoinChance(new Range(min, max)));
                    continue;
                }
                materialChanceDelegate.add(Pair.of(material, new Range(min, max)));
            }
            chancableList.add(new MaterialChance(materialChanceDelegate));
            put(weight, chancableList);
        }
    }

}
