package me.oriharel.machinery.upgrades;

import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.machine.MachineResourceGetProcess;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SpeedUpgrade extends AbstractUpgrade {

    protected Map<Integer, Integer> costs;

    public SpeedUpgrade(int level) {
        super(level);
        runOnlyOnProcessStart = true;
        costs = null;
    }

    @Override
    public void applyUpgradeModifier(MachineResourceGetProcess mineProcess) {
        YamlConfiguration config = Machinery.getInstance().getFileManager().getConfig("upgrades.yml").get();
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        // period is in ticks. 20 ticks is a second
        int basePeriod = config.getInt("speed.basePeriod") * 20;
        if (level == 1) {
            mineProcess.setMinePeriod(basePeriod);
            return;
        }
        try {
            mineProcess.setMinePeriod((Integer) engine.eval(config.getString("speed.calculate").replaceAll("\\{basePeriod}", String.valueOf(basePeriod)).replaceAll(
                    "\\{level}",
                    String.valueOf(level))));
        } catch (ScriptException e) {
            Bukkit.getLogger().severe("Failed to calculate mine period!");
            Bukkit.getLogger().severe(e.getMessage());
        }
    }

    @Override
    public String getUpgradeName() {
        return "Speed Upgrade";
    }

    @Override
    public Map<Integer, Integer> getCosts() {
        if (costs == null) costs =
                configLoad.getConfigurationSection("speed.costs").getValues(false).entrySet().stream().map(entry -> new AbstractMap.SimpleEntry<>(Integer.valueOf(entry.getKey()), Integer.parseInt((String) entry.getValue()))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return costs;
    }

    @Override
    public UpgradeType getUpgradeType() {
        return UpgradeType.SPEED;
    }

    @Override
    public String toString() {
        return "SpeedUpgrade{" +
                "level=" + level +
                ", runOnlyOnProcessStart=" + runOnlyOnProcessStart +
                '}';
    }
}
