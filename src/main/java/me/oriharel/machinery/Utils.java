package me.oriharel.machinery;

import org.bukkit.Location;
import org.bukkit.World;

public class Utils {
    public static long locationToLong(Location location) {
        return ((long) location.getX() & 0x7FFFFFF) | (((long) location.getY() & 0x7FFFFFF) << 27) | ((long) location.getZ() << 54);
    }

    public static Location longToLocation(long packed, World world) {
        return new Location(world, (int) ((packed << 37) >> 37), (int) (packed >>> 54), (int) ((packed << 10) >> 37));
    }

    public static Location longToLocation(long packed) {
        return new Location(null, (int) ((packed << 37) >> 37), (int) (packed >>> 54), (int) ((packed << 10) >> 37));
    }
}
