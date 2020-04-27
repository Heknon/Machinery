package me.oriharel.machinery.upgrades

import me.oriharel.machinery.machines.machine.MachineResourceGetProcess
import me.oriharel.machinery.utilities.Utils

class LootBonusUpgrade(level: Int) : AbstractUpgrade(level) {
    private val lootAmplifierExpression: String = configLoad!!.getString("lootbonus.lootAmplifier")!!
    private val baseAmplifier: Double = configLoad!!.getDouble("lootbonus.baseAmplifier")

    override val costs = configLoad?.getConfigurationSection("lootbonus.costs")?.getValues(false)?.entries?.associate { it.key.toInt() to it.value as Int }
    override val upgradeType: UpgradeType = UpgradeType.LOOT_BONUS
    override val isRunOnlyOnProcessStart: Boolean = true
    override val upgradeName: String = "Loot Bonus Upgrade"

    override fun applyUpgradeModifier(mineProcess: MachineResourceGetProcess) {

        return if (level == 1)
            mineProcess.lootAmplifier = baseAmplifier
        else
            mineProcess.lootAmplifier = Utils.evaluateJavaScriptExpression(placeholderAppliedExpression)

    }

    private val placeholderAppliedExpression: String
        get() = lootAmplifierExpression
                .replace("\\{basePeriod}".toRegex(), baseAmplifier.toString())
                .replace("\\{level}".toRegex(), level.toString())

    override fun toString(): String {
        return "LootBonusUpgrade{" +
                "level=" + level +
                ", runOnlyOnProcessStart=" + isRunOnlyOnProcessStart +
                '}'
    }


}