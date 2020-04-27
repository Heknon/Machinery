package me.oriharel.machinery.upgrades

import me.oriharel.machinery.machine.MachineResourceGetProcess
import org.bukkit.Bukkit
import javax.script.ScriptEngineManager
import javax.script.ScriptException

class SpeedUpgrade(level: Int) : AbstractUpgrade(level) {
    override var costs: Map<Int, Int>? = null
        get() {
            if (field == null) field = configLoad?.getConfigurationSection("speed.costs")?.getValues(false)?.entries?.associate { it.key.toInt() to it.value as Int }
            return field
        }

    override fun applyUpgradeModifier(mineProcess: MachineResourceGetProcess) {
        // period is in ticks. 20 ticks is a second
        val basePeriod = (configLoad?.getInt("speed.basePeriod") ?: 10) * 20
        if (level == 1) { // if level 1 don't apply any calculations on base speed
            mineProcess.minePeriod = basePeriod
            return
        }
        val mgr = ScriptEngineManager()
        val engine = mgr.getEngineByName("JavaScript")
        try {
            mineProcess.minePeriod = engine.eval(configLoad?.getString("speed.calculate")!!.replace("\\{basePeriod}".toRegex(), basePeriod.toString()).replace(
                    "\\{level}".toRegex(), level.toString())) as Int
        } catch (e: ScriptException) {
            Bukkit.getLogger().severe("Failed to calculate mine period!")
            Bukkit.getLogger().severe(e.message)
        }
    }

    override val upgradeName: String
        get() = "Speed Upgrade"

    override val upgradeType: UpgradeType
        get() = UpgradeType.SPEED

    override fun toString(): String {
        return "SpeedUpgrade{" +
                "level=" + level +
                ", runOnlyOnProcessStart=" + isRunOnlyOnProcessStart +
                '}'
    }

    init {
        isRunOnlyOnProcessStart = true
        costs = null
    }
}