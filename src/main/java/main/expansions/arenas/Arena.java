package main.expansions.arenas;

import main.utils.Initializer;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

public class Arena {
    public static Map<String, Arena> arenas = new HashMap<>();
    private Location c1;
    private Location c2;
    private List<Section> sections = new ArrayList<>();
    private String name;
    private Material[] keys;

    Arena(String name, Location c1, Location c2) {
        this.name = name;
        this.c1 = c1;
        this.c2 = c2;

        keys = new Material[]{
                Material.AIR,
                Material.DIRT,
                Material.GRASS_BLOCK,
                Material.STONE
        };

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

    public static void createNewArena(String name, Location c1, Location c2, Player player) {
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
        int sectionArea = width * length;
        int sectionsX = 1;
        int sectionsZ = 1;
        boolean x = true;

        while (sectionArea > maxSectionArea) {
            if (x) sectionsX++;
            else sectionsZ++;

            sectionArea = (width / sectionsX) * (length / sectionsZ);

            x = !x;
        }

        List<Location> sectionStarts = new ArrayList<>();
        List<Location> sectionEnds = new ArrayList<>();

        for (int sx = 0; sx < sectionsX; sx++) {
            for (int zx = 0; zx < sectionsZ; zx++) {
                int xStart = (int) (Math.floor(width / sectionsX) * sx);
                int zStart = (int) (Math.floor(length / sectionsZ) * zx);

                int xEnd = (int) (Math.floor(width / sectionsX) * (sx + 1)) - 1;
                int zEnd = (int) (Math.floor(length / sectionsZ) * (zx + 1)) - 1;

                if (sx == sectionsX - 1) xEnd = width - 1;
                if (zx == sectionsZ - 1) zEnd = length - 1;

                Location start = c1.clone().add(xStart, 0, zStart);
                Location end = c1.clone().add(xEnd, height - 1, zEnd);

                sectionStarts.add(start);
                sectionEnds.add(end);
            }
        }

        Arena arena = new Arena(name, c1, c2);

        CreationLoopinData data = new CreationLoopinData();

        data.arena = arena;
        data.sectionStarts = sectionStarts;
        data.sectionEnds = sectionEnds;
        data.sections = new ArrayList<>();
        data.maxBlocks = width * length * height;
        data.lastUpdate = System.currentTimeMillis() - 5000;

        Runnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                arena.getSections().addAll(data.sections);

                Arena.arenas.put(arena.name, arena);
                ArenaIO.saveArena(new File(Initializer.p.getDataFolder(), "/Arenas/" + name + ".json"), arena);

                player.sendMessage("Done! The arena is now ready for use!");
            }
        };

        loopyCreate(data, 1000000, player, runnable);
    }

    private static void loopyCreate(CreationLoopinData data, int amount, Player player, Runnable onFinished) {
        Location start = data.sectionStarts.get(0);
        Location end = data.sectionEnds.get(0);
        int width = end.getBlockX() - start.getBlockX() + 1;
        int height = end.getBlockY() - start.getBlockY() + 1;
        int length = end.getBlockZ() - start.getBlockZ() + 1;
        List<Material> keyList = new ArrayList<>(Arrays.asList(data.arena.keys));

        for (int i = 0; i < amount; i++) {
            Location loc;
            try {
                loc = Arena.getLocationAtIndex(width, length, data.arena.c1.getWorld(), data.index);
            } catch (ArithmeticException e) {
                e.printStackTrace();
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
                if (System.currentTimeMillis() - data.lastUpdate > 10 * 1000)
                    data.lastUpdate = System.currentTimeMillis();
                continue;
            }

            if (data.blockTypes[data.blockTypes.length - 1] == blockKeyIndex) {
                data.blockAmounts[data.blockAmounts.length - 1] = (short) (data.blockAmounts[data.blockAmounts.length - 1] + 1);
                if (keyList.size() > data.arena.keys.length) data.arena.addKeys(keyList);
                data.index++;
                data.totalBlocks++;
                if (System.currentTimeMillis() - data.lastUpdate > 10 * 1000)
                    data.lastUpdate = System.currentTimeMillis();

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

            if (System.currentTimeMillis() - data.lastUpdate > 10 * 1000)
                data.lastUpdate = System.currentTimeMillis();
        }
        if (keyList.size() > data.arena.keys.length) data.arena.addKeys(keyList);
        loopyCreate(data, amount, player, onFinished);
    }

    public static Location getLocationAtIndex(int width, int length, World world, int index) {
        return new Location(world, index % width, index / (length * width), (index / width) % length);
    }

    protected void addKeys(Collection<Material> keys) {
        List<Material> keyList = new ArrayList<>(Arrays.asList(this.keys));
        for (Material data : keys) {
            if (!keyList.contains(data)) keyList.add(data);
        }

        this.keys = keyList.toArray(new Material[keyList.size()]);
    }

    public void reset(int resetSpeed) {
        ResetLoopinData data = new ResetLoopinData();
        data.maxBlocksThisTick = resetSpeed;
        data.speed = resetSpeed;
        for (Section s : getSections()) {
            int sectionAmount = (int) ((double) resetSpeed / (double) (c2.getBlockX() - c1.getBlockX() + 1) * (c2.getBlockY() - c1.getBlockY() + 1) * (c2.getBlockZ() - c1.getBlockZ() + 1) * (double) s.getTotalBlocks());
            if (sectionAmount <= 0) sectionAmount = 1;
            data.sections.put(s.getID(), sectionAmount);
            data.sectionIDs.add(s.getID());
        }

        loopyReset(data);
    }

    private void loopyReset(ResetLoopinData data) {
        data.blocksThisTick = 0;

        for (int sectionsIterated = 0; sectionsIterated < data.sections.size(); sectionsIterated++) {
            int id = data.sectionIDs.get((sectionsIterated + data.currentSectionResetting) % data.sections.size()) % getSections().size();
            Section s = getSections().get(id);
            boolean reset = s.reset(data.sections.get(id));
            if (reset) {
                data.sections.remove(id);
                data.sectionIDs.remove((Object) id);
                sectionsIterated--;

                if (data.sections.size() == 0) break;
                int newTotalAmount = data.sections.keySet().parallelStream().mapToInt((sectionid) -> (getSections().get(sectionid).getTotalBlocks())).sum();

                List<Section> sectionList = data.sections.keySet().parallelStream().map((sectionid) -> getSections().get(sectionid)).toList();
                for (Section s1 : sectionList) {
                    int sectionAmount = (int) ((double) data.speed / (double) newTotalAmount * (double) s.getTotalBlocks());
                    if (sectionAmount <= 0) sectionAmount = 1;
                    data.sections.put(s1.getID(), sectionAmount);
                }
            }
            data.blocksThisTick += s.getBlocksResetThisTick();

            if (data.blocksThisTick > data.maxBlocksThisTick) {
                data.currentSectionResetting = (sectionsIterated + data.currentSectionResetting) % data.sections.size();
                data.blocksThisTick += s.getBlocksResetThisTick();
                break;
            }
        }

        if (data.sections.size() == 0)
            return;

        Bukkit.getScheduler().runTaskLater(Initializer.p, () -> loopyReset(data), 1L);
    }

    public Material[] getKeys() {
        return keys;
    }

    public void setKeys(Collection<Material> keys) {
        this.keys = keys.toArray(new Material[keys.size()]);
    }

    public String getName() {
        return name;
    }

    public Location getc1() {
        return c1;
    }

    public Location getc2() {
        return c2;
    }

    public List<Section> getSections() {
        return sections;
    }

    private static class CreationLoopinData {
        List<Section> sections;
        List<Location> sectionStarts, sectionEnds;
        int index, totalBlocks, maxBlocks;
        long lastUpdate;
        Arena arena;
        short[] blockAmounts, blockTypes = new short[0];
    }

    private static class ResetLoopinData {
        Map<Integer, Integer> sections = new HashMap<>();
        List<Integer> sectionIDs = new ArrayList<>();
        int currentSectionResetting;
        int blocksThisTick = 0;
        int maxBlocksThisTick;
        int speed;
    }
}