package schematics

import net.minecraft.server.v1_15_R1.NBTCompressedStreamTools
import net.minecraft.server.v1_15_R1.NBTTagCompound
import net.minecraft.server.v1_15_R1.NBTTagList
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.Directional
import org.bukkit.block.data.type.Chest
import org.bukkit.block.data.type.Fence
import org.bukkit.block.data.type.Sign
import org.bukkit.block.data.type.WallSign
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.EnumUtils
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector
import java.io.File
import java.io.FileInputStream
import java.util.*
import java.util.function.Consumer

/**
 * A utility class that previews and pastes schematics block-by-block with asynchronous support.
 * <br></br>
 * **License:**
 * [https://gitlab.com/SamB440/Schematics-Extended/blob/master/LICENSE](https://gitlab.com/SamB440/Schematics-Extended/blob/master/LICENSE)
 *
 * @author SamB440 - Schematic previews, centering and pasting block-by-block, class itself
 * @author brainsynder - 1.13 Palette Schematic Reader
 * @author Math0424 - Rotation calculations
 * @author Jojodmo - Legacy (< 1.12) Schematic Reader
 * @version 2.0.3
 */
class Schematic(private val plugin: JavaPlugin, private val schematic: File) {
    var width: Short = 0
        private set
    var height: Short = 0
        private set
    var length: Short = 0
        private set
    private lateinit var blockDatas: ByteArray
    private val signs: MutableMap<Vector, List<String?>> = HashMap()
    var chests: Map<Vector?, Map<Int?, ItemStack?>?>? = null
        private set
    private val blocks: MutableMap<Int, BlockData> = HashMap()
    private val delayedBlocks: List<Material> = listOf(Material.LAVA,
            Material.WATER,
            Material.GRASS,
            Material.ARMOR_STAND,
            Material.TALL_GRASS,
            Material.BLACK_BANNER,
            Material.BLACK_WALL_BANNER,
            Material.BLUE_BANNER,
            Material.BLUE_WALL_BANNER,
            Material.BROWN_BANNER,
            Material.BROWN_WALL_BANNER,
            Material.CYAN_BANNER,
            Material.CYAN_WALL_BANNER,
            Material.GRAY_BANNER,
            Material.GRAY_WALL_BANNER,
            Material.GREEN_BANNER,
            Material.GREEN_WALL_BANNER,
            Material.LIGHT_BLUE_BANNER,
            Material.LIGHT_BLUE_WALL_BANNER,
            Material.LIGHT_GRAY_BANNER,
            Material.LIGHT_GRAY_WALL_BANNER,
            Material.LIME_BANNER,
            Material.LIME_WALL_BANNER,
            Material.MAGENTA_BANNER,
            Material.MAGENTA_WALL_BANNER,
            Material.ORANGE_BANNER,
            Material.ORANGE_WALL_BANNER,
            Material.PINK_BANNER,
            Material.PINK_WALL_BANNER,
            Material.PURPLE_BANNER,
            Material.PURPLE_WALL_BANNER,
            Material.RED_BANNER,
            Material.RED_WALL_BANNER,
            Material.WHITE_BANNER,
            Material.WHITE_WALL_BANNER,
            Material.YELLOW_BANNER,
            Material.YELLOW_WALL_BANNER,
            Material.GRASS,
            Material.TALL_GRASS,
            Material.SEAGRASS,
            Material.TALL_SEAGRASS,
            Material.FLOWER_POT,
            Material.SUNFLOWER,
            Material.CHORUS_FLOWER,
            Material.OXEYE_DAISY,
            Material.DEAD_BUSH,
            Material.FERN,
            Material.DANDELION,
            Material.POPPY,
            Material.BLUE_ORCHID,
            Material.ALLIUM,
            Material.AZURE_BLUET,
            Material.RED_TULIP,
            Material.ORANGE_TULIP,
            Material.WHITE_TULIP,
            Material.PINK_TULIP,
            Material.BROWN_MUSHROOM,
            Material.RED_MUSHROOM,
            Material.END_ROD,
            Material.ROSE_BUSH,
            Material.PEONY,
            Material.LARGE_FERN,
            Material.REDSTONE,
            Material.REPEATER,
            Material.COMPARATOR,
            Material.LEVER,
            Material.SEA_PICKLE,
            Material.SUGAR_CANE,
            Material.FIRE,
            Material.WHEAT,
            Material.WHEAT_SEEDS,
            Material.CARROTS,
            Material.BEETROOT,
            Material.BEETROOT_SEEDS,
            Material.MELON,
            Material.MELON_STEM,
            Material.MELON_SEEDS,
            Material.POTATOES,
            Material.PUMPKIN,
            Material.PUMPKIN_STEM,
            Material.PUMPKIN_SEEDS,
            Material.TORCH,
            Material.RAIL,
            Material.ACTIVATOR_RAIL,
            Material.DETECTOR_RAIL,
            Material.POWERED_RAIL,
            Material.ACACIA_FENCE,
            Material.ACACIA_FENCE_GATE,
            Material.BIRCH_FENCE,
            Material.BIRCH_FENCE_GATE,
            Material.DARK_OAK_FENCE,
            Material.DARK_OAK_FENCE_GATE,
            Material.JUNGLE_FENCE,
            Material.JUNGLE_FENCE_GATE,
            Material.NETHER_BRICK_FENCE,
            Material.OAK_FENCE,
            Material.OAK_FENCE_GATE,
            Material.SPRUCE_FENCE,
            Material.SPRUCE_FENCE_GATE,
            Material.OAK_DOOR,
            Material.ACACIA_DOOR,
            Material.BIRCH_DOOR,
            Material.DARK_OAK_DOOR,
            Material.JUNGLE_DOOR,
            Material.SPRUCE_DOOR,
            Material.IRON_DOOR)

    /**
     * Pastes a schematic, with a specified time
     *
     * @param paster player pasting
     * @param time   time in ticks to paste blocks
     * @return list of locations where schematic blocks will be pasted, null if schematic locations will replace blocks
     * @throws SchematicNotLoadedException when schematic has not yet been loaded
     * @see .loadSchematic
     */
    @Throws(SchematicNotLoadedException::class)
    fun pasteSchematic(loc: Location,
                       paster: Player?,
                       time: Int,
                       callback: (List<Location?>) -> Boolean,
                       option: Options?): List<Location?>? {
        try {
            if (width.toInt() == 0 || height.toInt() == 0 || length.toInt() == 0 || blocks.isEmpty()) {
                throw SchematicNotLoadedException("Data has not been loaded yet")
            }

            val options: List<Options?> = listOf(option)
            val tracker = Data()
            val indexes: MutableList<Int> = ArrayList()
            val locations: MutableList<Location?> = ArrayList()
            val otherindex: MutableList<Int> = ArrayList()
            val otherloc: MutableList<Location?> = ArrayList()
            val nbtData: MutableMap<Int, Any?> = HashMap()
            val face = getDirection(paster)

            /*
             * Loop through all the blocks within schematic size.
             */
            for (x in 0 until width) {
                for (y in 0 until height) {
                    for (z in 0 until length) {
                        val index = y * width * length + z * width + x
                        val point = Vector(x, y, z)
                        var location: Location? = null

                        when (face) {
                            BlockFace.NORTH -> location = Location(loc.world, x * -1 + loc.x + width.toInt() / 2, y + loc.y, z + loc.z + length.toInt() / 2)
                            BlockFace.EAST -> location = Location(loc.world, -z + loc.x - length.toInt() / 2, y + loc.y,
                                    -x - 1 + (width + loc.z) - width.toInt() / 2)
                            BlockFace.SOUTH -> location = Location(loc.world, x + loc.x - width.toInt() / 2, y + loc.y, z * -1 + loc.z - length.toInt() / 2)
                            BlockFace.WEST -> location = Location(loc.world, z + loc.x + length.toInt() / 2, y + loc.y,
                                    x + 1 - (width - loc.z) + width.toInt() / 2)
                            else -> {}
                        }

                        val data = blocks[blockDatas[index].toInt()]

                        /*
                         * Ignore blocks that aren't air. Change this if you want the air to destroy blocks too.
                         * Add items to delayedBlocks if you want them placed last, or if they get broken.
                         */
                        val material = data!!.material

                        if (material != Material.AIR) {
                            if (!delayedBlocks.contains(material)) {
                                indexes.add(index)
                                locations.add(location)
                            } else {
                                otherindex.add(index)
                                otherloc.add(location)
                            }
                        }

                        if (signs.containsKey(point)) {
                            nbtData[index] = signs[point]
                        }

                        if (chests != null) {
                            if (chests!!.containsKey(point)) {
                                nbtData[index] = chests!![point]
                            }
                        }
                    }
                }
            }

            /*
             * Make sure liquids are placed last.
             */
            indexes.addAll(otherindex)
            otherindex.clear()
            locations.addAll(otherloc)
            otherloc.clear()

            /*
             * ---------------------------
             * Delete this section of code if you want schematics to be pasted anywhere
             */
            val validated = true
            val validatedBlocks: MutableSet<Block> = HashSet() // Stores previous blocks before placing green glass
            for (validate in locations) {
                if (validate!!.block.type != Material.AIR && validate.block.type != Material.GRASS || validate.clone().subtract(0.0, 1.0, 0.0).block.type == Material.WATER || Location(validate.world, validate.x, loc.y - 1, validate.z).block.type == Material.AIR) {
                    /*
                     * Show fake block where block is interfering with schematic
                     */
                    validatedBlocks.forEach(Consumer { block: Block -> paster!!.sendBlockChange(block.location, block.blockData) }) // remove validated location blocks
                    return null
                } else {
                    /*
                     * Show fake block for air
                     */
                    validatedBlocks.add(validate.block)
                    paster!!.sendBlockChange(validate.block.location, Material.GREEN_STAINED_GLASS.createBlockData())
                    if (!options.contains(Options.PREVIEW)) {
                        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, Runnable {
                            if (validate.block.type == Material.AIR || validate.block.type == Material.GRASS) paster.sendBlockChange(validate.block.location,
                                    Material.AIR.createBlockData())
                        }, 60)
                    }
                }
            }
            if (options.contains(Options.PREVIEW)) return locations
            if (!validated) return null

            /*
             * ---------------------------
             */if (options.contains(Options.REALISTIC)) {
                //TODO
            }

            /*
             * Start pasting each block every tick
             */
            val scheduler = Scheduler()
            tracker.trackCurrentBlock = 0
            val validData: MutableList<Material?> = ArrayList()
            validData.add(Material.LADDER)
            validData.add(Material.TORCH)
            validData.add(Material.CHEST)
            validData.add(Material.BLACK_STAINED_GLASS_PANE)
            validData.add(Material.BLUE_STAINED_GLASS_PANE)
            validData.add(Material.BROWN_STAINED_GLASS_PANE)
            validData.add(Material.CYAN_STAINED_GLASS_PANE)
            validData.add(Material.GLASS_PANE)
            validData.add(Material.WHITE_STAINED_GLASS_PANE)
            validData.add(Material.GREEN_STAINED_GLASS_PANE)
            validData.add(Material.GRAY_STAINED_GLASS_PANE)
            validData.add(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
            validData.add(Material.LIGHT_GRAY_STAINED_GLASS_PANE)
            validData.add(Material.LIME_STAINED_GLASS_PANE)
            validData.add(Material.MAGENTA_STAINED_GLASS_PANE)
            validData.add(Material.ORANGE_STAINED_GLASS_PANE)
            validData.add(Material.PINK_STAINED_GLASS_PANE)
            validData.add(Material.PURPLE_STAINED_GLASS_PANE)
            validData.add(Material.RED_STAINED_GLASS_PANE)
            validData.add(Material.YELLOW_STAINED_GLASS_PANE)
            validData.add(Material.DISPENSER)
            validData.add(Material.DROPPER)
            validData.add(Material.TORCH)
            validData.addAll(ExtraTags.FENCE_GATES.materials)
            validData.addAll(Tag.SIGNS.values)
            validData.addAll(Tag.BANNERS.values)
            validData.addAll(Tag.STAIRS.values)

            /*
             * List of block faces to update *after* the schematic is done pasting.
             */
            val toUpdate: MutableList<Block> = ArrayList()
            for (i in locations.indices) {
                val block = locations[i]!!.block
                val data = blocks[blockDatas[indexes[i]].toInt()]
                if (Tag.FENCES.values.contains(data!!.material)) {
                    toUpdate.add(block)
                }
            }

            scheduler.task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, {


                /*
                 * Get the block, set the type, data, and then update the state.
                 */
                val block = locations[tracker.trackCurrentBlock]!!.block
                val data = blocks[blockDatas[indexes[tracker.trackCurrentBlock]].toInt()]
                block.type = data!!.material
                block.blockData = data
                when (data.material) {
                    Material.SPRUCE_SIGN, Material.DARK_OAK_SIGN, Material.ACACIA_SIGN, Material.BIRCH_SIGN, Material.JUNGLE_SIGN, Material.OAK_SIGN -> {
                        val signData = data as Sign?
                        block.blockData = signData!!
                        if (nbtData.containsKey(indexes[tracker.trackCurrentBlock])) {
                            val lines = nbtData[indexes[tracker.trackCurrentBlock]] as List<String>?
                            val sign = block.state as org.bukkit.block.Sign
                            sign.setLine(0, lines!![0])
                            if (lines.size >= 2) sign.setLine(1, lines[1])
                            if (lines.size >= 3) sign.setLine(2, lines[2])
                            if (lines.size >= 4) sign.setLine(3, lines[3])
                            sign.update()
                        }
                    }
                    Material.SPRUCE_WALL_SIGN, Material.DARK_OAK_WALL_SIGN, Material.ACACIA_WALL_SIGN, Material.BIRCH_WALL_SIGN, Material.JUNGLE_WALL_SIGN, Material.OAK_WALL_SIGN -> {
                        val signData = data as WallSign?
                        block.blockData = signData!!
                        if (nbtData.containsKey(indexes[tracker.trackCurrentBlock])) {
                            val lines = nbtData[indexes[tracker.trackCurrentBlock]] as List<String>?
                            val sign = block.state as org.bukkit.block.Sign
                            sign.setLine(0, lines!![0])
                            if (lines.size >= 2) sign.setLine(1, lines[1])
                            if (lines.size >= 3) sign.setLine(2, lines[2])
                            if (lines.size >= 4) sign.setLine(3, lines[3])
                            sign.update()
                        }
                    }
                    Material.CHEST, Material.TRAPPED_CHEST -> {
                        val chestData = data as Chest?
                        block.blockData = chestData!!
                        if (nbtData.containsKey(indexes[tracker.trackCurrentBlock])) {
                            val items = nbtData[indexes[tracker.trackCurrentBlock]] as Map<Int, ItemStack>?
                            val chest = block.state as org.bukkit.block.Chest
                            for (location in items!!.keys) {
                                chest.blockInventory.setItem(location, items[location])
                            }
                        }
                    }
                    else -> {
                    }
                }
                block.state.update(true, false)
                if (validData.contains(data.material)) {
                    val facing = block.state.blockData as Directional
                    when (face) {
                        BlockFace.NORTH -> when (facing.facing) {
                            BlockFace.NORTH -> facing.facing = BlockFace.NORTH
                            BlockFace.SOUTH -> facing.facing = BlockFace.SOUTH
                            BlockFace.EAST -> facing.facing = BlockFace.WEST
                            BlockFace.WEST -> facing.facing = BlockFace.EAST
                            else -> {
                            }
                        }
                        BlockFace.EAST -> when (facing.facing) {
                            BlockFace.NORTH -> facing.facing = BlockFace.EAST
                            BlockFace.SOUTH -> facing.facing = BlockFace.WEST
                            BlockFace.EAST -> facing.facing = BlockFace.NORTH
                            BlockFace.WEST -> facing.facing = BlockFace.SOUTH
                            else -> {
                            }
                        }
                        BlockFace.SOUTH -> when (facing.facing) {
                            BlockFace.NORTH -> facing.facing = BlockFace.SOUTH
                            BlockFace.SOUTH -> facing.facing = BlockFace.NORTH
                            BlockFace.EAST -> facing.facing = BlockFace.EAST
                            BlockFace.WEST -> facing.facing = BlockFace.WEST
                            else -> {
                            }
                        }
                        BlockFace.WEST -> when (facing.facing) {
                            BlockFace.NORTH -> facing.facing = BlockFace.WEST
                            BlockFace.SOUTH -> facing.facing = BlockFace.EAST
                            BlockFace.EAST -> facing.facing = BlockFace.SOUTH
                            BlockFace.WEST -> facing.facing = BlockFace.NORTH
                            else -> {
                            }
                        }
                        else -> {
                        }
                    }
                    block.blockData = facing
                }
                block.state.update(true, false)

                /*
                 * Play block effects, change to what you want
                 */block.location.world!!.spawnParticle(Particle.CLOUD, block.location, 6)
                block.location.world!!.playEffect(block.location, Effect.STEP_SOUND, block.type)
                tracker.trackCurrentBlock++
                if (tracker.trackCurrentBlock >= locations.size || tracker.trackCurrentBlock >= indexes.size) {
                    scheduler.cancel()
                    tracker.trackCurrentBlock = 0
                    toUpdate.forEach(Consumer { b: Block ->
                        val state = b.state
                        if (Tag.FENCES.values.contains(b.type)) {
                            val fence = state.blockData as Fence
                            for (bf in BlockFace.values()) {
                                val rel = b.getRelative(bf)
                                if (!fence.allowedFaces.contains(bf)) continue
                                if (rel.type == Material.AIR || rel.type.toString().contains("SLAB")
                                        || rel.type.toString().contains("STAIRS")) {
                                    if (fence.hasFace(bf)) fence.setFace(bf, false)
                                } else {
                                    if (!rel.type.toString().contains("SLAB")
                                            && !rel.type.toString().contains("STAIRS")
                                            && !ExtraTags.ANVILS.materials.contains(rel.type)
                                            && rel.type.isSolid
                                            && rel.type.isBlock) {
                                        if (!fence.hasFace(bf)) fence.setFace(bf, true)
                                    }
                                }
                            }
                            state.blockData = fence
                            state.update(true, false)
                        }
                    })
                    toUpdate.forEach(Consumer { b: Block -> b.state.update(true, false) })
                    callback(locations)
                }
            }, 0, time.toLong())
            return locations
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * Pastes a schematic, with the time defaulting to 1 block per second
     *
     * @param location location to paste from
     * @param paster   player pasting
     * @param options  options to apply to this paste
     * @return list of locations where schematic blocks will be pasted, null if schematic locations will replace blocks
     * @throws SchematicNotLoadedException when schematic has not yet been loaded
     * @see .loadSchematic
     */
    @Throws(SchematicNotLoadedException::class)
    fun pasteSchematic(location: Location, paster: Player?, callback: (List<Location?>) -> Boolean, options: Options?): List<Location?>? {
        return pasteSchematic(location, paster, 20, callback, options)
    }

    /**
     * Loads the schematic file. This should **always** be used before pasting a schematic.
     *
     * @return schematic (self)
     */
    fun loadSchematic(): Schematic {
        try {
            val tracker = Data()

            /*
             * Read the schematic file. Get the width, height, length, blocks, and block data.
             */
            val fis = FileInputStream(schematic)
            val nbt: NBTTagCompound
            nbt = NBTCompressedStreamTools.a(fis)
            width = nbt.getShort("Width")
            height = nbt.getShort("Height")
            length = nbt.getShort("Length")
            blockDatas = nbt.getByteArray("BlockData")
            val palette = nbt.getCompound("Palette")
            val tiles = nbt["BlockEntities"] as NBTTagList?
            tracker.trackCurrentTile = 0

            /*
             * Load NBT data
             */if (tiles != null) {
                tiles.forEach(Consumer {
                    if (!tiles.getCompound(tracker.trackCurrentTile).isEmpty) {
                        val c = tiles.getCompound(tracker.trackCurrentTile)
                        if (EnumUtils.isValidEnum(NBTMaterial::class.java, c.getString("Id").replace("minecraft:", "").toUpperCase())) {
                            when (NBTMaterial.valueOf(c.getString("Id").replace("minecraft:", "").toUpperCase())) {
                                NBTMaterial.SIGN -> {
                                    try {
                                        val lines: MutableList<String?> = ArrayList()
                                        lines.add(NBTUtils.getSignLineFromNBT(c, NBTUtils.Position.TEXT_ONE))
                                        lines.add(NBTUtils.getSignLineFromNBT(c, NBTUtils.Position.TEXT_TWO))
                                        lines.add(NBTUtils.getSignLineFromNBT(c, NBTUtils.Position.TEXT_THREE))
                                        lines.add(NBTUtils.getSignLineFromNBT(c, NBTUtils.Position.TEXT_FOUR))
                                        val pos = c.getIntArray("Pos")
                                        if (!lines.isEmpty()) signs[Vector(pos[0], pos[1], pos[2])] = lines
                                        tiles.removeAt(tracker.trackCurrentTile)
                                    } catch (e: WrongIdException) {
                                        //it wasn't a sign
                                    }
                                }
                                else -> {
                                }
                            }
                        }
                    }
                    tracker.trackCurrentTile = tracker.trackCurrentTile + 1
                })
                try {
                    chests = NBTUtils.getItemsFromNBT(tiles)
                } catch (e: WrongIdException) {
                    //it wasn't a chest
                }
            }

            /*
             * 	Explanation:
             *    The "Palette" is setup like this
             *      "block_data": id (the ID is a Unique ID that WorldEdit gives that corresponds to an index in the BlockDatas Array)
             *    So I loop through all the Keys in the "Palette" Compound
             *    and store the custom ID and BlockData in the palette Map
             */palette.keys.forEach(Consumer { rawState: String? ->
                val id = palette.getInt(rawState)
                val blockData = Bukkit.createBlockData(rawState!!)
                blocks[id] = blockData
            })
            fis.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return this
    }

    /**
     * @param player player to get direction they are facing
     * @return blockface of cardinal direction player is facing
     */
    private fun getDirection(player: Player?): BlockFace {
        var yaw = player!!.location.yaw
        if (yaw < 0) {
            yaw += 360f
        }
        if (yaw >= 315 || yaw < 45) {
            return BlockFace.SOUTH
        } else if (yaw < 135) {
            return BlockFace.WEST
        } else if (yaw < 225) {
            return BlockFace.NORTH
        } else if (yaw < 315) {
            return BlockFace.EAST
        }
        return BlockFace.NORTH
    }

    /**
     * An enum of options to apply whilst previewing/pasting a schematic.
     */
    enum class Options {
        /**
         * Previews schematic
         */
        PREVIEW,

        /**
         * A realistic building method. Builds from the ground up, instead of in the default slices.
         * <hr></hr>
         * ***WIP, CURRENTLY DOES NOTHING***
         */
        REALISTIC
    }

    /**
     * Hacky method to avoid "final".
     */
    private class Data {
        var trackCurrentTile = 0
        var trackCurrentBlock = 0
    }

}