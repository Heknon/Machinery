package me.oriharel.machinery.upgrades;

import me.oriharel.machinery.machine.MachineResourceGetProcess;
import org.bukkit.Bukkit;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

public class LootBonusUpgrade extends AbstractUpgrade {

    protected Map<Integer, Integer> costs;

    public LootBonusUpgrade(int level) {
        super(level);
        this.runOnlyOnProcessStart = true;
        costs = null;
    }

    @Override
    public void applyUpgradeModifier(MachineResourceGetProcess mineProcess) {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        double baseAmplifier = configLoad.getDouble("lootbonus.baseAmplifier");
        if (level == 1) {
            mineProcess.setLootAmplifier(baseAmplifier);
            return;
        }
        try {
            mineProcess.setLootAmplifier((Double) engine.eval(configLoad.getString("lootbonus.lootAmplifier").replaceAll("\\{basePeriod}",
                    String.valueOf(baseAmplifier)).replaceAll(
                    "\\{level}", String.valueOf(level))));
        } catch (ScriptException e) {
            Bukkit.getLogger().severe("Failed to calculate loot amplifier!");
            Bukkit.getLogger().severe(e.getMessage());
        }
    }

    @Override
    public String getUpgradeName() {
        return "Loot Bonus Upgrade";
    }

    @Override
    public Map<Integer, Integer> getCosts() {
        if (costs == null) costs =
                configLoad.getConfigurationSection("lootbonus.costs").getValues(false).entrySet().stream().map(entry -> new AbstractMap.SimpleEntry<>(Integer.valueOf(entry.getKey()), Integer.parseInt((String) entry.getValue()))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return costs;
    }

    @Override
    public String toString() {
        return "LootBonusUpgrade{" +
                "level=" + level +
                ", runOnlyOnProcessStart=" + runOnlyOnProcessStart +
                '}';
    }
}
