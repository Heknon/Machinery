package me.oriharel.machinery.upgrades

import me.oriharel.machinery.Machinery
import me.oriharel.machinery.machine.MachineResourceGetProcess
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import java.util.AbstractMap.SimpleEntry
import java.util.function.Function
import java.util.stream.Collectors
import javax.script.ScriptEngineManager
import javax.script.ScriptException

class SpeedUpgrade(level: Int) : AbstractUpgrade(level) {
    protected override var costs: Map<Int, Int>?
    override fun applyUpgradeModifier(mineProcess: MachineResourceGetProcess) {
        val config: YamlConfiguration = Machinery.Companion.getInstance().getFileManager().getConfig("upgrades.yml").get()

        // period is in ticks. 20 ticks is a second
        val basePeriod = config.getInt("speed.basePeriod") * 20
        if (level == 1) { // if level 1 don't apply any calculations on base speed
            mineProcess.minePeriod = basePeriod
            return
        }
        val mgr = ScriptEngineManager()
        val engine = mgr.getEngineByName("JavaScript")
        try {
            mineProcess.minePeriod = engine.eval(config.getString("speed.calculate")!!.replace("\\{basePeriod}".toRegex(), basePeriod.toString()).replace(
                    "\\{level}".toRegex(), level.toString())) as Int
        } catch (e: ScriptException) {
            Bukkit.getLogger().severe("Failed to calculate mine period!")
            Bukkit.getLogger().severe(e.message)
        }
    }

    override val upgradeName: String
        get() = "Speed Upgrade"

    override fun getCosts(): Map<Int, Int>? {
        if (costs == null) costs = configLoad!!.getConfigurationSection("speed.costs")!!.getValues(false).entries.stream().map { entry: Map.Entry<String?, Any> -> SimpleEntry<Int, Int>(Integer.valueOf(entry.key), entry.value as String?. toInt ()) }.collect(Collectors.toMap(Function<SimpleEntry<Int, Int>, Int> { java.util.Map.Entry.key }, Function<SimpleEntry<Int, Int>, Int> { java.util.Map.Entry.value }))
        return costs
    }

    override val upgradeType: UpgradeType
        get() = UpgradeType.SPEED

    override fun toString(): String {
        return "SpeedUpgrade{" +
                "level=" + level +
                ", runOnlyOnProcessStart=" + runOnlyOnProcessStart +
                '}'
    }

    init {
        runOnlyOnProcessStart = true
        costs = null
    }
}