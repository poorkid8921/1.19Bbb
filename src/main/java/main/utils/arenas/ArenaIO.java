package main.utils.arenas;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class ArenaIO {
    static final byte SECTION_SPLIT = '\u0002';
    static final byte KEY_SPLIT = '\u0003';

    public static Arena loadArena(File file) {
        try {
            byte[] readBytes = null;
            byte[] bytes = Files.readAllBytes(file.toPath());
            Inflater decompresser = new Inflater();
            decompresser.setInput(bytes);

            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream(bytes.length);
                decompresser.setInput(bytes);
                byte[] buffer = new byte[1024];
                while (!decompresser.finished()) {
                    int count = decompresser.inflate(buffer);
                    outputStream.write(buffer, 0, count);
                }
                readBytes = outputStream.toByteArray();
            } catch (DataFormatException ignored) {
            }

            int firstSectionSplit = ArrayUtils.indexOf(readBytes, SECTION_SPLIT);
            byte[] header = Arrays.copyOfRange(readBytes, 0, firstSectionSplit);
            String headerString = new String(header, StandardCharsets.US_ASCII);
            String name = headerString.split(",")[0];
            int xx1 = Integer.parseInt(headerString.split(",")[1]);
            int yy1 = Integer.parseInt(headerString.split(",")[2]);
            int zz1 = Integer.parseInt(headerString.split(",")[3]);

            World w = Bukkit.getWorld("world");
            Location corner1 = new Location(w, xx1, yy1, zz1);

            int xx2 = Integer.parseInt(headerString.split(",")[4]);
            int yy2 = Integer.parseInt(headerString.split(",")[5]);
            int zz2 = Integer.parseInt(headerString.split(",")[6]);

            Location corner2 = new Location(w, xx2, yy2, zz2);
            int keySectionSplit = ArrayUtils.indexOf(readBytes, SECTION_SPLIT, firstSectionSplit + 1);
            byte[] keyBytes = Arrays.copyOfRange(readBytes, firstSectionSplit + 1, keySectionSplit);
            List<Material> blockDataSet = new ArrayList<>();

            for (byte[] key : Utils.split(new byte[]{KEY_SPLIT}, keyBytes)) {
                String blockData = new String(key, StandardCharsets.US_ASCII);
                try {
                    blockDataSet.add(Material.valueOf(blockData));
                } catch (IllegalArgumentException e) {
                    try {
                        blockDataSet.add(Material.valueOf(blockData.split("\\[")[0]));
                    } catch (IllegalArgumentException ignored) {
                        return null;
                    }
                }
            }

            Arena arena = new Arena(name, corner1, corner2);
            arena.setKeys(blockDataSet);
            byte[] blockBytes = Arrays.copyOfRange(readBytes, keySectionSplit + 1, readBytes.length);

            ByteBuffer bb = ByteBuffer.allocate(2);
            bb.put(blockBytes[0]);
            bb.put(blockBytes[1]);
            short sectionCount = bb.getShort(0);
            short currentSection = 0;
            blockBytes = Arrays.copyOfRange(blockBytes, 2, blockBytes.length);

            ByteBuffer buffer = ByteBuffer.allocate(blockBytes.length);
            buffer.put(blockBytes);
            buffer.position(0);

            while (currentSection < sectionCount) {
                int x1 = buffer.getInt();
                int y1 = buffer.getInt();
                int z1 = buffer.getInt();
                int x2 = buffer.getInt();
                int y2 = buffer.getInt();
                int z2 = buffer.getInt();
                Location start = new Location(corner1.getWorld(), x1, y1, z1);
                Location end = new Location(corner1.getWorld(), x2, y2, z2);
                int left = buffer.getInt();
                int numLeft = left / 2;

                short[] amounts = new short[numLeft];
                short[] types = new short[numLeft];

                for (int i = 0; i < numLeft; i++) {
                    amounts[i] = buffer.getShort();
                    types[i] = buffer.getShort();
                }

                arena.getSections().add(new Section(arena, currentSection, start, end, types, amounts));
                currentSection++;
            }
            return arena;
        } catch (Exception ignored) {
        }
        return null;
    }
}
