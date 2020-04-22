package me.oriharel.machinery.data;

import org.bukkit.Material;

import java.util.*;
import java.util.stream.Collectors;

public final class MaterialChance extends Chance {
    private final List<Material> materials;

    public MaterialChance(List<Material> materials, int minimumAmount, int maximumAmount) {
        super(minimumAmount, maximumAmount);
        this.materials = materials;
    }

    public MaterialChance(int minimumAmount, int maximumAmount, Material... materials) {
        super(minimumAmount, maximumAmount);
        this.materials = Arrays.stream(materials).collect(Collectors.toList());
    }

    public MaterialChance(Material material, int minimumAmount, int maximumAmount) {
        super(minimumAmount, maximumAmount);
        this.materials = new ArrayList<>(Collections.singleton(material));
    }

    public MaterialChance(int minimumAmount, int maximumAmount) {
        super(minimumAmount, maximumAmount);
        this.materials = new ArrayList<>();
    }

    public List<Material> getMaterials() {
        return materials;
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
