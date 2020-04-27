package me.oriharel.machinery.upgrades

import me.oriharel.machinery.machines.machine.MachineResourceGetProcess
import me.oriharel.machinery.utilities.Utils

class SpeedUpgrade(level: Int) : AbstractUpgrade(level) {
    private val speedCalculateExpression: String = configLoad?.getString("speed.calculate")!!
    private val basePeriod: Int = (configLoad?.getInt("speed.basePeriod") ?: 10) * 20

    override val costs: Map<Int, Int>? = configLoad?.getConfigurationSection("speed.costs")?.getValues(false)?.entries?.associate { it.key.toInt() to it.value as Int }
    override val upgradeName: String = "Speed Upgrade"
    override val isRunOnlyOnProcessStart: Boolean = true
    override val upgradeType: UpgradeType = UpgradeType.SPEED

    override fun applyUpgradeModifier(mineProcess: MachineResourceGetProcess) {
        return if (level == 1)
            mineProcess.minePeriod = basePeriod
        else
            mineProcess.minePeriod = Utils.evaluateJavaScriptExpression(placeholderAppliedExpression)

    }

    private val placeholderAppliedExpression: String
        get() = speedCalculateExpression
                .replace("\\{basePeriod}".toRegex(), basePeriod.toString())
                .replace("\\{level}".toRegex(), level.toString())

    override fun toString(): String {
        return "SpeedUpgrade{" +
                "level=" + level +
                ", runOnlyOnProcessStart=" + isRunOnlyOnProcessStart +
                '}'
    }
}