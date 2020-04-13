package me.oriharel.machinery;

import me.oriharel.machinery.utilities.Utils;
import org.bukkit.Location;

public class Test {
    public static void main(String[] args) {
        Location loc = new org.bukkit.Location(null, 70, 50, -999999);
        System.out.println("Original Location: " + loc);
        System.out.println("Original location to long: " + Utils.locationToLong(loc));
        System.out.println("Converted from long location " + Utils.longToLocation(Utils.locationToLong(loc)));
    }
}
