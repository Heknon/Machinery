package me.oriharel.machinery;

import me.oriharel.machinery.utilities.Utils;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class Test {
    public static void main(String[] args) {
        Path file = new File("C:\\Users\\Ori\\Desktop").toPath();
        removeMachineCoreLocation(file, new Location(null, 999, 1, 999));

    }

    public static void addMachineCoreLocation(Path folder, Location location) {
        Path file = folder.resolve("machines.dat");
        createFileIfNotExist(file);
        try {
            Files.write(file, ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(Utils.locationToLong(location)).array(), StandardOpenOption.APPEND);
            //Files.copy(file, file.getParent().resolve("machines.bak.dat"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeMachineCoreLocation(Path folder, Location location) {
        long longToFind = Utils.locationToLong(location);
        Path file = folder.resolve("machines.dat");
        try {
            byte[] bytes = Files.readAllBytes(file);
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            LongBuffer longBuffer = buffer.asLongBuffer();
            for (int i = 0; i < longBuffer.capacity(); i++) {
                long position = buffer.getLong(i * 8);
                if (position != longToFind) continue;
                buffer.putLong(i * 8, 0);
                Files.write(file, buffer.array(), StandardOpenOption.WRITE);
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createFileIfNotExist(Path file) {
        if (!file.toFile().exists()) {
            try {
                file.toFile().createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
