package me.oriharel.machinery.utilities;

import org.bukkit.Location;
import org.bukkit.World;

public class Utils {
    public static long locationToLong(Location location) {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        return ((long)x & 0x7FFFFFF) | (((long)z & 0x7FFFFFF) << 27) | ((long)y << 54);
    }

    public static Location longToLocation(long packed) {
        int x = (int) ((packed << 37) >> 37);
        int y = (int) (packed >>> 54);
        int z = (int) ((packed << 10) >> 37);
        return new Location(null, x, y, z);
    }

    public static Location longToLocation(long packed, World world) {
        Location loc = longToLocation(packed);
        loc.setWorld(world);
        return loc;
    }
}
