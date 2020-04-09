package me.oriharel.machinery;

import net.minecraft.server.v1_15_R1.BlockPosition;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class LocationSetPersistentData implements PersistentDataType<byte[][], Set> {


    @Override
    public Class<byte[][]> getPrimitiveType() {
        return byte[][].class;
    }

    @Override
    public Class<Set> getComplexType() {
        return Set.class;
    }

    @Override
    public byte[][] toPrimitive(Set locations, PersistentDataAdapterContext persistentDataAdapterContext) {
        return (byte[][]) locations.stream().map(loc -> {
            long longLoc = toLong((Location)loc);
            return longToBytes(longLoc);
        }).toArray();
    }

    @Override
    public Set fromPrimitive(byte[][] bytes, PersistentDataAdapterContext persistentDataAdapterContext) {
        return Arrays.stream(bytes).map(bytes1 -> {
            long longLoc = bytesToLong(bytes1);
            return fromLong(longLoc);
        }).collect(Collectors.toSet());
    }

    public Location fromLong(long packed) {
        return new Location(null, (int) ((packed << 37) >> 37), (int) (packed >>> 54), (int) ((packed << 10) >> 37));
    }

    public long toLong(Location loc) {
        return ((long)loc.getX() & 0x7FFFFFF) | (((long)loc.getZ() & 0x7FFFFFF) << 27) | ((long)loc.getY() << 54);
    }

    public byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    public long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip();//need flip
        return buffer.getLong();
    }
}
