package me.oriharel.machinery.structure

import com.sun.istack.internal.NotNull
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import schematics.Schematic

/**
 * Used to abstract the logic of building a schematic.
 */
class Structure(val schematic: Schematic, val name: String) {

    fun build(@NotNull loc: Location, player: Player?, openGUIBlockMaterial: Material?, callback: (PrintResult?) -> Boolean): List<Location?>? {
        return if (schematic.isNotLoaded) {
            callback(null)
            null
        } else {
            schematic.pasteSchematic(loc, player, 5, { locations: List<Location?> ->
                val printResult = PrintResult(locations, null)

                for (location in locations) {
                    if (location?.block?.type == openGUIBlockMaterial) printResult.openGUIBlockLocation = location
                }

                callback(printResult)
                true
            }, Schematic.Options.REALISTIC)
        }
    }

    data class PrintResult(val placementLocations: List<Location?>, var openGUIBlockLocation: Location?)

}