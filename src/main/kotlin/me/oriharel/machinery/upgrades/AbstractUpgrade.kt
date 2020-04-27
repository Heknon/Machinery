package me.oriharel.machinery.upgrades

import com.google.common.base.Preconditions
import me.oriharel.machinery.Machinery
import me.oriharel.machinery.machines.machine.MachineResourceGetProcess
import org.bukkit.configuration.file.YamlConfiguration

/**
 * abstract implementation of upgrade.
 * abstract since every upgrade has it's own modifying logic but behind the scenes upgrading and downgrading a upgrade is the same.
 */
abstract class AbstractUpgrade(currentLevel: Int) {
    abstract val isRunOnlyOnProcessStart: Boolean
    abstract val upgradeType: UpgradeType
    abstract val costs: Map<Int, Int>?
    abstract val upgradeName: String
    abstract fun applyUpgradeModifier(mineProcess: MachineResourceGetProcess)

    var level: Int = currentLevel
        protected set
    val maxLevel: Int


    init {
        maxLevel = calculateMaxLevel()
    }

    fun upgrade() {
        ++level
    }

    fun downgrade() {
        Preconditions.checkArgument(level > 1, "You cannot downgrade a level 1 upgrade!")
        --level
    }

    private fun calculateMaxLevel(): Int {
        var max = 1

        for (key in costs!!.keys) {
            if (key > max) max = key
        }
        return max
    }

    override fun toString(): String {
        return "AbstractUpgrade{" +
                "level=" + level +
                '}'
    }

    companion object {
        private var configCache: YamlConfiguration? = null

        val configLoad: YamlConfiguration?
            get() {
                if (configCache != null) {
                    return configCache
                }
                configCache = Machinery.instance?.fileManager?.getConfig("upgrades.yml")?.get();
                return configCache;
            }
    }
}