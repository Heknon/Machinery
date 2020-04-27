package schematics

import com.google.gson.Gson
import com.google.gson.JsonObject
import net.minecraft.server.v1_15_R1.NBTTagCompound
import net.minecraft.server.v1_15_R1.NBTTagList
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.util.*

/**
 * Utility class to get extra data from NBT
 * @author SamB440
 */
object NBTUtils {
    /**
     * @param c
     * @param position - position of text to read from
     * @return text at the specified position on the sign
     * @throws WrongIdException
     */
    @Throws(WrongIdException::class)
    fun getSignLineFromNBT(c: NBTTagCompound, position: Position): String? {
        if (c.getString("Id") == "minecraft:sign") {
            val s1 = c.getString(position.id)
            val jobj = Gson().fromJson(s1, JsonObject::class.java)
            if (jobj["extra"] != null) {
                val array = jobj["extra"].asJsonArray
                return array[0].asJsonObject["text"].asString
            }
        } else {
            throw WrongIdException("Id of NBT was not a sign, was instead " + c.getString("Id"))
        }
        return null
    }

    /**
     * @param l - blockentities
     * @return a map, with the key as the vector, and the value as a second map with the key as the slot and the value as the item
     * @throws WrongIdException
     */
    @Throws(WrongIdException::class)
    fun getItemsFromNBT(l: NBTTagList): Map<Vector, Map<Int, ItemStack>> {
        val allItems: MutableMap<Vector, Map<Int, ItemStack>> = HashMap()
        for (i in 0..l.size) {
            val c = l.getCompound(i)
            if (c.getString("Id") == "minecraft:chest") {
                val items = c["Items"] as NBTTagList?
                for (i2 in items!!.indices) {
                    val anItem = items.getCompound(i2)
                    val mat = Material.valueOf(anItem.getString("id").replace("minecraft:", "").toUpperCase())
                    val item = ItemStack(mat, anItem.getInt("Count"))
                    val pos = c.getIntArray("Pos")
                    val result: MutableMap<Int, ItemStack> = HashMap()
                    result[anItem.getInt("Slot")] = item
                    allItems[Vector(pos[0], pos[1], pos[2])] = result
                }
            } else {
                throw WrongIdException("Id of NBT was not a chest, was instead " + c.getString("Id"))
            }
        }
        return allItems
    }

    /**
     * Utility class for NBT sign positions
     * @author SamB440
     */
    enum class Position(val id: String) {
        TEXT_ONE("Text1"), TEXT_TWO("Text2"), TEXT_THREE("Text3"), TEXT_FOUR("Text4");

    }
}