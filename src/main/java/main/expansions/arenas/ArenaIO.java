package main.expansions.arenas;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArenaIO {
    private static byte SECTION_SPLIT = '\u0002';
    private static byte KEY_SPLIT = '\u0003';

    public static void saveArena(File file, Arena arena, Runnable... callback) {
        Location corner1 = arena.getc1();
        Location corner2 = arena.getc2();

        if (corner1.getWorld() != corner2.getWorld()) return;

        try {
            FileOutputStream stream = new FileOutputStream(file);
            Location l = arena.getc1();
            Location l2 = arena.getc2();

            String header = arena.getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + "," +
                    l2.getBlockX() + "," + l2.getBlockY() + "," + l2.getBlockZ();
            byte[] headerBytes = header.getBytes(StandardCharsets.US_ASCII);

            byte[] keyBytes = new byte[0];

            for (Material data : arena.getKeys()) {
                keyBytes = ArrayUtils.addAll(keyBytes, data.name().getBytes(StandardCharsets.US_ASCII));
                keyBytes = ArrayUtils.add(keyBytes, KEY_SPLIT);
            }

            keyBytes = ArrayUtils.remove(keyBytes, keyBytes.length - 1);

            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

            short sections = (short) arena.getSections().size();

            ByteBuffer sb = ByteBuffer.allocate(2);
            sb.putShort(sections);

            byteStream.write(sb.array());

            for (int s = 0; s < arena.getSections().size(); s++) {
                Section section = arena.getSections().get(s);

                ByteBuffer ib = ByteBuffer.allocate((7 * 4) + (section.getBlockAmounts().length * 4));

                ib.putInt(section.getStart().getBlockX());
                ib.putInt(section.getStart().getBlockY());
                ib.putInt(section.getStart().getBlockZ());
                ib.putInt(section.getEnd().getBlockX());
                ib.putInt(section.getEnd().getBlockY());
                ib.putInt(section.getEnd().getBlockZ());

                ib.putInt(section.getBlockTypes().length * 2);

                for (int i = 0; i < section.getBlockAmounts().length; i++) {
                    ib.putShort(section.getBlockAmounts()[i]);
                    ib.putShort(section.getBlockTypes()[i]);
                }

                byteStream.write(ib.array());
            }

            byte[] blockBytes = byteStream.toByteArray();

            byte[] totalBytes = new byte[0];
            totalBytes = ArrayUtils.addAll(totalBytes, headerBytes);
            totalBytes = ArrayUtils.add(totalBytes, SECTION_SPLIT);
            totalBytes = ArrayUtils.addAll(totalBytes, keyBytes);
            totalBytes = ArrayUtils.add(totalBytes, SECTION_SPLIT);
            totalBytes = Utils.compress(ArrayUtils.addAll(totalBytes, blockBytes));

            stream.write(totalBytes);
            stream.close();

            for (Runnable r : callback) r.run();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Arena loadArena(File file) {
        try {
            byte[] readBytes = Utils.decompress(Files.readAllBytes(file.toPath()));

            int firstSectionSplit = ArrayUtils.indexOf(readBytes, SECTION_SPLIT);
            byte[] header = Arrays.copyOfRange(readBytes, 0, firstSectionSplit);
            String headerString = new String(header, StandardCharsets.US_ASCII);

            String name;

            name = headerString.split(",")[0];
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
                    } catch (IllegalArgumentException e2) {
                        e.printStackTrace();
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

                Section section = new Section(arena, currentSection, start, end, types, amounts);
                arena.getSections().add(section);
                currentSection++;
            }

            return arena;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
