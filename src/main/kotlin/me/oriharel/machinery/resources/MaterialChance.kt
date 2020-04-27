package me.oriharel.machinery.resources

import com.mojang.datafixers.util.Pair
import me.oriharel.machinery.machines.machine.MachineResourceGetProcess
import me.oriharel.machinery.message.ChanceableOperation
import me.oriharel.machinery.resources.chance.Range
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.stream.Collectors

class MaterialChance(private val materials: List<Pair<Material?, Range>>) : ChanceableOperation<List<ItemStack?>?, MachineResourceGetProcess?> {
    override fun getChanced(lootModifier: Double): List<ItemStack?> {
        return materials.stream().map { p: Pair<Material?, Range> -> ItemStack(p.first!!, (p.second.random() * lootModifier).toInt()) }.collect(Collectors.toList())
    }

    override fun executeChanceOperation(param1: MachineResourceGetProcess?, lootModifier: Double) {
        param1?.itemsGained?.addAll(getChanced(lootModifier))
    }

}