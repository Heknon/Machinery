package me.oriharel.machinery.structure;

import com.sun.istack.internal.NotNull;
import net.islandearth.schematics.extended.Schematic;
import net.islandearth.schematics.extended.SchematicNotLoadedException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;
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

    public List<Location> build(@NotNull Location loc, Player player, Material openGUIBlockMaterial, Function<PrintResult, Boolean> callback) {
        try {
            return schematic.pasteSchematic(loc, player, 5, (locations) -> {
                PrintResult printResult = new PrintResult(locations, null);
                locations.forEach(l -> {
                    Block block = l.getBlock();
                    if (block.getType().equals(openGUIBlockMaterial)) printResult.setOpenGUIBlockLocation(block.getLocation());
                });
                callback.apply(printResult);
            }, Schematic.Options.REALISTIC);
        } catch (SchematicNotLoadedException e) {
            e.printStackTrace();
            callback.apply(null);
        }
        return null;
    }

    public static class PrintResult {
        private List<Location> placementLocations;
        private Location openGUIBlockLocation;

        public PrintResult(List<Location> placementLocations, Location openGUIBlockLocation) {
            this.placementLocations = placementLocations;
            this.openGUIBlockLocation = openGUIBlockLocation;
        }

        public List<Location> getPlacementLocations() {
            return placementLocations;
        }

        public Location getOpenGUIBlockLocation() {
            return openGUIBlockLocation;
        }

        public void setOpenGUIBlockLocation(Location openGUIBlockLocation) {
            this.openGUIBlockLocation = openGUIBlockLocation;
        }
    }
}
