package main.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static main.Economy.*;
import static main.Economy.overworld;

public class CompressionUtils {
    public static byte[] compressLong(long value) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        do {
            byte byteValue = (byte) (value & 0x7F);
            value >>= 7;
            if (value != 0) {
                byteValue |= (byte) 0x80;
            }
            bos.write(byteValue);
        } while (value != 0);
        return bos.toByteArray();
    }

    public static long decompressLong(long compressed) {
        long value = 0;
        int shift = 0;
        long mask = 0x7F;
        while (true) {
            value |= (compressed & mask) << shift;
            if ((compressed & 0x8000000000000000L) == 0) {
                break;
            }
            compressed >>>= 7;
            shift += 7;
        }
        return value;
    }

    public static long packLocation(Location location) {
        int x = (int) location.getX();
        int y = (int) location.getY();
        int z = (int) location.getZ();
        int yaw = (int) location.getYaw();
        int pitch = (int) location.getPitch();

        int worldType = switch (location.getWorld().getEnvironment()) {
            case NORMAL -> 0;
            case NETHER -> 1;
            case THE_END -> 2;
            default -> 0;
        };

        long packedX = (long) x & 0x00FFFFFF; // 24
        long packedY = (long) y & 0x00000FFF; // 12
        long packedZ = (long) z & 0x00FFFFFF; // 24

        long packedLocation = (packedX << 36) | (packedY << 24) | packedZ;

        int packedYaw = yaw & 0xFF;
        int packedPitch = pitch & 0xFF;

        long packedYawPitch = (long) packedYaw << 8 | packedPitch;
        long packedWorldType = (long) worldType & 0x03; // 2 bits

        packedLocation |= packedYawPitch << 16 | packedWorldType << 14;
        return packedLocation;
    }

    public static Location unpackLocation(long packedLocation) {
        int worldType = (int) ((packedLocation >> 14) & 0x03);

        int packedYawPitch = (int) ((packedLocation >> 16) & 0xFFFF);
        int yaw = packedYawPitch >> 8;
        int pitch = packedYawPitch & 0xFF;

        long packedX = packedLocation >> 36;
        long packedY = (packedLocation >> 24) & 0x00000FFF;
        long packedZ = packedLocation & 0x00FFFFFF;

        int x = (int) packedX;
        int y = (int) packedY;
        int z = (int) packedZ;

        Location location = new Location(Bukkit.getWorlds().get(0), x, y, z);
        location.setYaw(yaw);
        location.setPitch(pitch);
        location.setWorld(worldType == 0 ? overworld : worldType == 1 ? nether : worldType == 2 ? end : overworld);

        return location;
    }
}
