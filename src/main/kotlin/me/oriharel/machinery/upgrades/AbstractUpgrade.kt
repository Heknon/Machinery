package me.oriharel.machinery.upgrades

import com.google.common.base.Preconditions
import me.oriharel.machinery.Machinery
import me.oriharel.machinery.machine.MachineResourceGetProcess
import org.bukkit.configuration.file.YamlConfiguration

/**
 * abstract implementation of upgrade.
 * abstract since every upgrade has it's own modifying logic but behind the scenes upgrading and downgrading a upgrade is the same.
 */
abstract class AbstractUpgrade {
    var level: Int
        protected set
    var isRunOnlyOnProcessStart = false
        protected set
    protected var configLoad: YamlConfiguration? = null
    protected var maxLevel = -1

    constructor(level: Int) {
        this.level = level
        isRunOnlyOnProcessStart = false
        configLoad = Machinery.Companion.getInstance().getFileManager().getConfig("upgrades.yml").get()
    }

    protected constructor() {
        level = 1
    }

    fun upgrade() {
        ++level
    }

    fun downgrade() {
        Preconditions.checkArgument(level > 1, "You cannot downgrade a level 1 upgrade!")
        --level
    }

    abstract fun applyUpgradeModifier(mineProcess: MachineResourceGetProcess)
    abstract val upgradeName: String
    abstract val costs: Map<Int, Int>?
    fun getMaxLevel(): Int {
        if (maxLevel == -1) maxLevel = calculateMaxLevel()
        return maxLevel
    }

    private fun calculateMaxLevel(): Int {
        var max = 1
        for (key in costs!!.keys) {
            if (key > max) max = key
        }
        return max
    }

    abstract val upgradeType: UpgradeType
    override fun toString(): String {
        return "AbstractUpgrade{" +
                "level=" + level +
                '}'
    }
}