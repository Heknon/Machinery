package me.oriharel.machinery;

import org.bukkit.Location;
import org.bukkit.World;

public class Utils {
    public static long locationToLong(Location location) {
        return ((long) location.getBlockX()) << 38 | (long)location.getBlockZ() << 12 | location.getBlockY();
    }

    public static Location longToLocation(long packed) {
        return new Location(null, packed >> 38, packed & 0xFFF, packed << 26 >> 38);
    }

    public static Location longToLocation(long packed, World world) {
        Location loc = longToLocation(packed);
        loc.setWorld(world);
        return loc;
    }
}
