package me.oriharel.machinery;

import org.bukkit.Material;

import java.util.*;
import java.util.stream.Collectors;

public final class MaterialChance {
    private final List<Material> materials;
    private final int minimumAmount;
    private final int maximumAmount;

    public MaterialChance(List<Material> materials, int minimumAmount, int maximumAmount) {
        this.materials = materials;
        this.minimumAmount = minimumAmount;
        this.maximumAmount = maximumAmount;
    }

    public MaterialChance(int minimumAmount, int maximumAmount, Material... materials) {
        this.materials = Arrays.stream(materials).collect(Collectors.toList());
        this.minimumAmount = minimumAmount;
        this.maximumAmount = maximumAmount;
    }

    public MaterialChance(Material material, int minimumAmount, int maximumAmount) {
        this.materials = new ArrayList<>(Collections.singleton(material));
        this.minimumAmount = minimumAmount;
        this.maximumAmount = maximumAmount;
    }

    public MaterialChance(int minimumAmount, int maximumAmount) {
        this.materials = new ArrayList<>();
        this.minimumAmount = minimumAmount;
        this.maximumAmount = maximumAmount;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public int getMinimumAmount() {
        return minimumAmount;
    }

    public int getMaximumAmount() {
        return maximumAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaterialChance that = (MaterialChance) o;
        return
                minimumAmount == that.minimumAmount &&
                        maximumAmount == that.maximumAmount &&
                        materials == that.materials;
    }

    @Override
    public int hashCode() {
        return Objects.hash(materials, minimumAmount, maximumAmount);
    }

    @Override
    public String toString() {
        return "MaterialChance{" +
                "material=" + materials +
                ", minimumAmount=" + minimumAmount +
                ", maximumAmount=" + maximumAmount +
                '}';
    }
}
