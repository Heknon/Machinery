package me.oriharel.machinery.upgrades

import me.oriharel.machinery.machine.MachineResourceGetProcess
import org.bukkit.Bukkit
import java.util.AbstractMap.SimpleEntry
import java.util.function.Function
import java.util.stream.Collectors
import javax.script.ScriptEngineManager
import javax.script.ScriptException

class LootBonusUpgrade(level: Int) : AbstractUpgrade(level) {
    protected override var costs: Map<Int, Int>?
    override fun applyUpgradeModifier(mineProcess: MachineResourceGetProcess) {
        val baseAmplifier = configLoad!!.getDouble("lootbonus.baseAmplifier")
        if (level == 1) { // if level 1 don't apply any calculations on base amplifier
            mineProcess.lootAmplifier = baseAmplifier
            return
        }
        val mgr = ScriptEngineManager()
        val engine = mgr.getEngineByName("JavaScript") // use javascript engine to evaluate math expression
        try {
            mineProcess.lootAmplifier = engine.eval(configLoad!!.getString("lootbonus.lootAmplifier")!!.replace("\\{basePeriod}".toRegex(), baseAmplifier.toString()).replace(
                    "\\{level}".toRegex(), level.toString())) as Double
        } catch (e: ScriptException) {
            Bukkit.getLogger().severe("Failed to calculate loot amplifier!")
            Bukkit.getLogger().severe(e.message)
        }
    }

    override val upgradeName: String
        get() = "Loot Bonus Upgrade"

    override fun getCosts(): Map<Int, Int>? {
        if (costs == null) costs = configLoad!!.getConfigurationSection("lootbonus.costs")!!.getValues(false).entries.stream()
                .map { entry: Map.Entry<String?, Any?> -> SimpleEntry(Integer.valueOf(entry.key), entry.value as Int?) }
                .collect(Collectors.toMap(Function<SimpleEntry<Int, Int?>, Int> { java.util.Map.Entry.key }, Function<SimpleEntry<Int, Int?>, Int> { java.util.Map.Entry.value }))
        return costs
    }

    override val upgradeType: UpgradeType
        get() = UpgradeType.LOOT_BONUS

    override fun toString(): String {
        return "LootBonusUpgrade{" +
                "level=" + level +
                ", runOnlyOnProcessStart=" + runOnlyOnProcessStart +
                '}'
    }

    init {
        runOnlyOnProcessStart = true
        costs = null
    }
}