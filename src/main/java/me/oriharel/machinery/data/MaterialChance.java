package me.oriharel.machinery.data;

import com.mojang.datafixers.util.Pair;
import me.oriharel.machinery.machine.MachineResourceGetProcess;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class MaterialChance implements ChanceableOperation<List<ItemStack>, MachineResourceGetProcess> {

    private final List<Pair<Material, Range>> materials;

    public MaterialChance(List<Pair<Material, Range>> materials) {
        this.materials = materials;
    }


    @Override
    public List<ItemStack> getChanced(double lootModifier) {
        return materials.stream().map(p -> new ItemStack(p.getFirst(), (int) (p.getSecond().random() * lootModifier))).collect(Collectors.toList());
    }

    @Override
    public void executeChanceOperation(MachineResourceGetProcess machineResourceGetProcess, double lootModifier) {
        machineResourceGetProcess.getItemsGained().addAll(getChanced(lootModifier));
    }
}
