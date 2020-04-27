package me.oriharel.machinery.data

import com.mojang.datafixers.util.Pair
import me.oriharel.machinery.machine.MachineResourceGetProcess
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.stream.Collectors

class MaterialChance(private val materials: List<Pair<Material?, Range>>) : ChanceableOperation<List<ItemStack?>?, MachineResourceGetProcess> {
    override fun getChanced(lootModifier: Double): List<ItemStack?>? {
        return materials.stream().map { p: Pair<Material?, Range> -> ItemStack(p.first!!, (p.second.random() * lootModifier).toInt()) }.collect(Collectors.toList())
    }

    override fun executeChanceOperation(machineResourceGetProcess: MachineResourceGetProcess, lootModifier: Double) {
        machineResourceGetProcess.itemsGained.addAll(getChanced(lootModifier))
    }

}