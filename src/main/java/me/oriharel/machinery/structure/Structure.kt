package me.oriharel.machinery.structure

import com.sun.istack.internal.NotNull
import me.oriharel.machinery.utilities.CallbackP
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import schematics.Schematic
import schematics.SchematicNotLoadedException
import java.util.function.Consumer
import java.util.function.Function

/**
 * Used to abstract the logic of building a schematic.
 */
class Structure(val schematic: Schematic, val name: String) {

    fun build(@NotNull loc: Location, player: Player?, openGUIBlockMaterial: Material?, callback: Function<PrintResult?, Boolean>): List<Location?>? {
        try {
            return schematic.pasteSchematic(loc, player, 5, { locations: List<Location> ->
                val printResult = PrintResult(locations, null)
                locations.forEach(Consumer { l: Location ->
                    val block = l.block
                    if (block.type == openGUIBlockMaterial) printResult.openGUIBlockLocation = block.location
                })
                callback.apply(printResult)
            }, Schematic.Options.REALISTIC)
        } catch (e: SchematicNotLoadedException) {
            e.printStackTrace()
            callback.apply(null)
        }
        return null
    }

    class PrintResult(val placementLocations: List<Location>, var openGUIBlockLocation: Location?)

}