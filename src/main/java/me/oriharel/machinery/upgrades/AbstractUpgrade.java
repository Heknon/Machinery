package me.oriharel.machinery.upgrades;

import com.google.common.base.Preconditions;
import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.machine.MachineResourceGetProcess;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * abstract implementation of upgrade.
 * abstract since every upgrade has it's own modifying logic but behind the scenes upgrading and downgrading a upgrade is the same.
 */
public abstract class AbstractUpgrade {
    protected int level;
    protected boolean runOnlyOnProcessStart;
    protected YamlConfiguration configLoad;
    protected int maxLevel = -1;

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
        if (maxLevel == -1) maxLevel = calculateMaxLevel();
        return maxLevel;
    }

    private int calculateMaxLevel() {
        int max = 1;
        for (int key : getCosts().keySet()) {
            if (key > max) max = key;
        }
        return max;
    }

    public boolean isRunOnlyOnProcessStart() {
        return runOnlyOnProcessStart;
    }

    public abstract UpgradeType getUpgradeType();

    @Override
    public String toString() {
        return "AbstractUpgrade{" +
                "level=" + level +
                '}';
    }
}
