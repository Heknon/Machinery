package me.oriharel.machinery.upgrades;

import com.google.common.base.Preconditions;
import me.oriharel.machinery.machine.MachineResourceGetProcess;

public abstract class AbstractUpgrade {
    protected int level;
    protected boolean runOnlyOnProcessStart;

    public AbstractUpgrade(int level) {
        this.level = level;
        this.runOnlyOnProcessStart = false;
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
