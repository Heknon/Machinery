package me.oriharel.machinery.structure;

import com.sun.istack.internal.NotNull;
import net.islandearth.schematics.extended.Schematic;
import net.islandearth.schematics.extended.SchematicNotLoadedException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class Structure {
    private final Schematic schematic;
    private String name;

    public Structure(Schematic schematic, String name) {
        this.name = name;
        this.schematic = schematic;
    }

    public Schematic getSchematic() {
        return schematic;
    }

    public String getName() {
        return name;
    }

    public void build(@NotNull Location loc, Player player, Material specialMaterial, Material openGUIBlockMaterial, Function<Schematic.PrintResult, Boolean> callback) {
        try {
            callback.apply(schematic.pasteSchematic(loc, player, 5, specialMaterial, openGUIBlockMaterial, Schematic.Options.REALISTIC));
        } catch (SchematicNotLoadedException e) {
            e.printStackTrace();
            callback.apply(null);
        }
    }
}
