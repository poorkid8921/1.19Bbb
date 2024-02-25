package main.utils.arenas;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import main.Economy;
import main.utils.Initializer;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static main.utils.arenas.ArenaIO.KEY_SPLIT;
import static main.utils.arenas.ArenaIO.SECTION_SPLIT;

public class Arena {
    public static Map<String, Arena> arenas = new Object2ObjectOpenHashMap<>();
    private final Location c1;
    private final Location c2;
    private final ObjectArrayList<Section> sections = ObjectArrayList.of();
    private final String name;
    private Material[] keys;

    Arena(String name, Location c1, Location c2) {
        this.name = name;
        this.c1 = c1;
        this.c2 = c2;
        keys = new Material[]{Material.AIR, Material.DIRT, Material.GRASS_BLOCK, Material.STONE};

        int x1 = c1.getBlockX();
        int x2 = c2.getBlockX();
        int y1 = c1.getBlockY();
        int y2 = c2.getBlockY();
        int z1 = c1.getBlockZ();
        int z2 = c2.getBlockZ();
        if (x1 > x2) {
            int temp = x2;
            x2 = x1;
            x1 = temp;
        }
        if (y1 > y2) {
            int temp = y2;
            y2 = y1;
            y1 = temp;
        }
        if (z1 > z2) {
            int temp = z2;
            z2 = z1;
            z1 = temp;
        }

        c1.setX(x1);
        c2.setX(x2);
        c1.setY(y1);
        c2.setY(y2);
        c1.setZ(z1);
        c2.setZ(z2);
    }

    public static Map<String, Arena> getArenas() {
        return arenas;
    }

    static void createNewArena(String name, Location c1, Location c2) {
        int x1 = c1.getBlockX();
        int x2 = c2.getBlockX();
        int y1 = c1.getBlockY();
        int y2 = c2.getBlockY();
        int z1 = c1.getBlockZ();
        int z2 = c2.getBlockZ();

        if (x1 > x2) {
            int temp = x2;
            x2 = x1;
            x1 = temp;
        }
        if (y1 > y2) {
            int temp = y2;
            y2 = y1;
            y1 = temp;
        }
        if (z1 > z2) {
            int temp = z2;
            z2 = z1;
            z1 = temp;
        }

        c1.setX(x1);
        c2.setX(x2);
        c1.setY(y1);
        c2.setY(y2);
        c1.setZ(z1);
        c2.setZ(z2);

        int width = c2.getBlockX() - c1.getBlockX() + 1;
        int length = c2.getBlockZ() - c1.getBlockZ() + 1;
        int height = c2.getBlockY() - c1.getBlockY() + 1;
        int maxSectionArea = 1024 / height;
        int sectionArea = width * length * height;
        int sectionsX = 1;
        int sectionsY = 1;
        int sectionsZ = 1;
        boolean x = true;
        boolean y = true;
        while (sectionArea > maxSectionArea) {
            if (x) {
                sectionsX++;
                sectionArea = (width / sectionsX) * (length / sectionsZ) * (height / sectionsY);
            } else if (y) {
                sectionsY++;
                sectionArea = (width / sectionsX) * (length / sectionsZ) * (height / sectionsY);
                y = false;
            } else {
                sectionsZ++;
                sectionArea = (width / sectionsX) * (length / sectionsZ) * (height / sectionsY);
            }
            x = !x;
        }

        ObjectArrayList<Location> sectionStarts = ObjectArrayList.of();
        ObjectArrayList<Location> sectionEnds = ObjectArrayList.of();
        for (int sx = 0; sx < sectionsX; sx++) {
            for (int sy = 0; sy < sectionsY; sy++) {
                for (int zx = 0; zx < sectionsZ; zx++) {
                    int xStart = Math.floorDiv(width, sectionsX) * sx;
                    int yStart = Math.floorDiv(height, sectionsY) * sy;
                    int zStart = Math.floorDiv(length, sectionsZ) * zx;
                    int xEnd = Math.floorDiv(width, sectionsX) * (sx + 1) - 1;
                    int yEnd = Math.floorDiv(height, sectionsY) * (sy + 1) - 1;
                    int zEnd = Math.floorDiv(length, sectionsZ) * (zx + 1) - 1;
                    if (sx == sectionsX - 1) xEnd = width - 1;
                    if (sy == sectionsY - 1) yEnd = height - 1;
                    if (zx == sectionsZ - 1) zEnd = length - 1;
                    Location start = c1.clone().add(xStart, yStart, zStart);
                    Location end = c1.clone().add(xEnd, yEnd, zEnd);
                    sectionStarts.add(start);
                    sectionEnds.add(end);
                }
            }
        }
        Arena arena = new Arena(name, c1, c2);
        CreationLoopinData data = new CreationLoopinData();

        data.arena = arena;
        data.sectionStarts = sectionStarts;
        data.sectionEnds = sectionEnds;
        data.sections = ObjectArrayList.of();
        data.maxBlocks = width * length * height;
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                arena.getSections().addAll(data.sections);
                Arena.getArenas().put(arena.name, arena);
                File file = new File(Initializer.p.getDataFolder(), "/arenas/" + name + ".json");
                try {
                    FileOutputStream stream = new FileOutputStream(file);
                    Location l = arena.getc1();
                    Location l2 = arena.getc2();

                    String header = arena.getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + "," + l2.getBlockX() + "," + l2.getBlockY() + "," + l2.getBlockZ();
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
                        ByteBuffer ib = ByteBuffer.allocate(28 + (section.getBlockAmounts().length * 4));
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
                } catch (IOException ignored) {
                }
            }
        };
        loopyCreate(data, 500000, runnable);
    }

    private static void loopyCreate(CreationLoopinData data, final int amount, Runnable onFinished) {
        Location start = data.sectionStarts.get(0);
        Location end = data.sectionEnds.get(0);
        int width = end.getBlockX() - start.getBlockX() + 1;
        int height = end.getBlockY() - start.getBlockY() + 1;
        int length = end.getBlockZ() - start.getBlockZ() + 1;
        ObjectArrayList<Material> keyList = ObjectArrayList.of(data.arena.keys);

        for (int i = 0; i < amount; i++) {
            Location loc;
            try {
                loc = Arena.getLocationAtIndex(width, length, data.index);
            } catch (ArithmeticException ignored) {
                return;
            }

            loc = start.clone().add(loc);
            if (data.index >= width * height * length) {
                data.sections.add(new Section(data.arena, data.sections.size(), start, end, data.blockTypes, data.blockAmounts));
                data.blockAmounts = new short[0];
                data.blockTypes = new short[0];
                data.sectionStarts.remove(0);
                data.sectionEnds.remove(0);
                if (keyList.size() > data.arena.keys.length) data.arena.addKeys(keyList);
                if (data.sectionStarts.size() == 0) {
                    onFinished.run();
                    return;
                }

                start = data.sectionStarts.get(0);
                end = data.sectionEnds.get(0);
                width = end.getBlockX() - start.getBlockX() + 1;
                height = end.getBlockY() - start.getBlockY() + 1;
                length = end.getBlockZ() - start.getBlockZ() + 1;
                data.index = 0;
                continue;
            }

            Material t = loc.getBlock().getType();
            if (!keyList.contains(t)) keyList.add(t);
            short blockKeyIndex = (short) keyList.indexOf(t);

            if (data.blockTypes.length == 0) {
                data.blockAmounts = new short[]{1};
                data.blockTypes = new short[]{blockKeyIndex};
                if (keyList.size() > data.arena.keys.length) data.arena.addKeys(keyList);
                data.totalBlocks++;
                data.index++;
                continue;
            }

            if (data.blockTypes[data.blockTypes.length - 1] == blockKeyIndex) {
                data.blockAmounts[data.blockAmounts.length - 1] = (short) (data.blockAmounts[data.blockAmounts.length - 1] + 1);
                if (keyList.size() > data.arena.keys.length) data.arena.addKeys(keyList);
                data.index++;
                data.totalBlocks++;
                if (data.blockAmounts[data.blockAmounts.length - 1] == Short.MAX_VALUE) {
                    data.blockTypes = ArrayUtils.add(data.blockTypes, blockKeyIndex);
                    data.blockAmounts = ArrayUtils.add(data.blockAmounts, (short) 0);
                }
                continue;
            }

            data.blockAmounts = ArrayUtils.add(data.blockAmounts, (short) 1);
            data.blockTypes = ArrayUtils.add(data.blockTypes, blockKeyIndex);

            data.index++;
            data.totalBlocks++;
        }
        if (keyList.size() > data.arena.keys.length) data.arena.addKeys(keyList);
        loopyCreate(data, amount, onFinished);
    }

    static Location getLocationAtIndex(int width, int length, int index) {
        return new Location(Economy.d, index % width, index / (length * width), (index / width) % length);
    }

    protected void addKeys(Collection<Material> keys) {
        ObjectOpenHashSet<Material> keySet = ObjectOpenHashSet.of(this.keys);
        keySet.addAll(keys);
        this.keys = keySet.toArray(new Material[keySet.size()]);
    }

    public void reset(int speed) {
        ResetLoopinData data = new ResetLoopinData();
        data.speed = speed;
        int reverseTotalBlocks = (c2.getBlockY() - c1.getBlockY() + 1) * (c2.getBlockZ() - c1.getBlockZ() + 1);
        for (Section s : getSections()) {
            int sectionAmount = (int) ((double) speed / (double) (c2.getBlockX() - c1.getBlockX() + 1) * reverseTotalBlocks * (double) s.getTotalBlocks());
            if (sectionAmount <= 0) sectionAmount = 1;
            data.sections.put(s.getID(), sectionAmount);
            data.sectionIDs.add(s.getID());
        }
        loopyReset(data);
    }

    private void loopyReset(ResetLoopinData data) {
        for (int sectionsIterated = 0; sectionsIterated < data.sections.size(); sectionsIterated++) {
            int id = data.sectionIDs.get(sectionsIterated % data.sections.size()) % getSections().size();
            Section s = sections.get(id);
            if (s.reset(data.sections.get(id))) {
                data.sections.remove(id);
                data.sectionIDs.remove((Object) id);
                sectionsIterated--;

                if (data.sections.size() == 0) break;
                int newTotalAmount = data.sections.keySet().parallelStream().mapToInt((sectionid) -> (getSections().get(sectionid).getTotalBlocks())).sum();
                for (Section s1 : data.sections.keySet().parallelStream().map((sectionid) -> getSections().get(sectionid)).toList()) {
                    int sectionAmount = (int) ((double) data.speed / (double) newTotalAmount * (double) s.getTotalBlocks());
                    if (sectionAmount <= 0) sectionAmount = 1;
                    data.sections.put(s1.getID(), sectionAmount);
                }
            }
        }

        if (data.sections.size() == 0) return;
        Bukkit.getScheduler().runTaskLater(Initializer.p, () -> loopyReset(data), 1L);
    }

    Material[] getKeys() {
        return keys;
    }

    void setKeys(Collection<Material> keys) {
        this.keys = keys.toArray(new Material[keys.size()]);
    }

    public String getName() {
        return name;
    }

    private Location getc1() {
        return c1;
    }

    private Location getc2() {
        return c2;
    }

    ObjectArrayList<Section> getSections() {
        return sections;
    }

    private static class CreationLoopinData {
        ObjectArrayList<Section> sections;
        ObjectArrayList<Location> sectionStarts, sectionEnds;
        int index, totalBlocks, maxBlocks;
        Arena arena;
        short[] blockAmounts, blockTypes = new short[0];
    }

    private static class ResetLoopinData {
        private final Map<Integer, Integer> sections = new Object2ObjectOpenHashMap<>();
        private final ObjectArrayList<Integer> sectionIDs = ObjectArrayList.of();
        private int speed;
    }
}