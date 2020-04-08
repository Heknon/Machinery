package me.oriharel.machinery.deprecated;

import me.oriharel.machinery.TestMachine;
import me.oriharel.machinery.machine.Machine;
import net.minecraft.server.v1_15_R1.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

class MachineTileEntity extends TileEntity implements ITickable {

    private final TestMachine machine;
    private TileEntity parent = null;


    public MachineTileEntity(TileEntityTypes types, TestMachine machine) {
        super(types);
        this.machine = machine;

    }

    public static <Tile extends TileEntity> TileEntityTypes<Tile> registerTileEntityType(String id, Supplier<Tile> provider, Set<Block> blocks) {
        try {
            Method a = TileEntityTypes.class.getDeclaredMethod("a", String.class, TileEntityTypes.a.class);
            Constructor constructor = TileEntityTypes.a.class.getDeclaredConstructor(Supplier.class, Set.class);
            constructor.setAccessible(true);
            a.setAccessible(true);
            return (TileEntityTypes<Tile>) a.invoke(provider, id, constructor.newInstance(provider, blocks));
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException("bad");
        }
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        return super.save(nbttagcompound);
    }

    @Override
    public NBTTagCompound b() {
        return parent.b();
    }

    @Override
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return parent.getUpdatePacket();
    }

    @Override
    public void setPosition(BlockPosition blockposition) {
        setLocation(world, position);
    }

    //This is the remove method.
    @Override
    public void ab_() {
        if (parent == null) super.ab_();
        else world.setTileEntity(position, parent);
    }

    //This is the enable method.
    @Override public void r() {
        setLocation(world, position);
        super.r();
    }

    @Override
    public void setLocation(World world, BlockPosition blockposition) {
        if (parent != null) this.world.setTileEntity(this.position, parent); //Restore
        parent = world.getTileEntity(position);
        this.world = world;
        this.position = position.immutableCopy();
    }

    @Override
    public void tick() {

    }
}
