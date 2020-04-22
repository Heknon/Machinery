package me.oriharel.machinery.upgrades;

import com.google.common.base.Preconditions;
import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.machine.MachineResourceGetProcess;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractUpgrade {
    protected int level;
    protected boolean runOnlyOnProcessStart;
    protected YamlConfiguration configLoad;

    public AbstractUpgrade(int level) {
        this.level = level;
        this.runOnlyOnProcessStart = false;
        this.configLoad = Machinery.getInstance().getFileManager().getConfig("upgrades.yml").get();
    }

    protected AbstractUpgrade() {
        level = 1;
    }

    public void upgrade() {
        ++level;
    }

    public void downgrade() {
        Preconditions.checkArgument(level > 1, "You cannot downgrade a level 1 upgrade!");
        --level;
    }

    public int getLevel() {
        return level;
    }

    public abstract void applyUpgradeModifier(MachineResourceGetProcess mineProcess);

    public abstract String getUpgradeName();

    public abstract Map<Integer, Integer> getCosts();

    public int getMaxLevel() {
        int max = 1;
        for (int key : getCosts().keySet()) {
            if (key > max) max = key;
        }
        return max;
    }

    public boolean isRunOnlyOnProcessStart() {
        return runOnlyOnProcessStart;
    }

    @Override
    public String toString() {
        return "AbstractUpgrade{" +
                "level=" + level +
                '}';
    }
}
