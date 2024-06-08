package main.utils.modules.arenas;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.bukkit.Bukkit;

import java.util.ArrayDeque;
import java.util.Deque;

import static main.utils.Initializer.RANDOM;
import static main.utils.Initializer.p;

public class Utils {
    private static final WorkloadRunnable WORKLOAD_RUNNABLE = new WorkloadRunnable();
    public static ServerLevel nmsOverworld;
    public static ServerChunkCache chunkSource = null;
    public static ThreadedLevelLightEngine lightEngine = null;

    public static void init() {
        Bukkit.getScheduler().runTaskTimer(p, WORKLOAD_RUNNABLE, 1L, 1L);
    }

    public static void setCuboid(int startX, int startY, int startZ, int endX, int endY, int endZ, Block block, BlockState material) {
        int minY, maxY, minX, maxX, minZ, maxZ;
        LevelChunk chunk;
        LevelChunkSection[] sections;
        LevelChunkSection section;
        BlockPos blockPos;
        int lastChunkX, lastChunkZ;
        minX = Math.min(startX, endX);
        minY = Math.min(startY, endY);
        minZ = Math.min(startZ, endZ);
        maxX = Math.max(startX, endX);
        maxY = Math.max(startY, endY);
        maxZ = Math.max(startZ, endZ);
        int absY15 = minY & 15;
        int lastY = minY;
        chunk = nmsOverworld.getChunk(minX, minZ);
        sections = chunk.getSections();
        section = sections[chunk.getSectionIndex(minY)];
        lastChunkX = minX >> 4;
        lastChunkZ = minZ >> 4;
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    blockPos = new BlockPos(x, y, z);
                    final int sectionX = x >> 4;
                    final int sectionZ = z >> 4;
                    if (lastChunkX != sectionX || lastChunkZ != sectionZ) {
                        lastChunkX = sectionX;
                        lastChunkZ = sectionZ;
                        chunk = nmsOverworld.getChunkAt(blockPos);
                        sections = chunk.getSections();
                        section = sections[chunk.getSectionIndex(absY15)];
                    } else if (lastY != y) {
                        absY15 = y & 15;
                        section = sections[chunk.getSectionIndex(absY15)];
                        lastY = y;
                    }
                    WORKLOAD_RUNNABLE.addWorkload(new main.utils.modules.arenas.Utils.SetTask(blockPos, chunk, section, block, material, x, absY15, z, false));
                }
            }
        }
    }

    public static void setArea(int absY, int startX, int startZ, int endX, int endZ, BlockState material) {
        int minX, maxX, minZ, maxZ;
        LevelChunk chunk;
        LevelChunkSection[] sections;
        LevelChunkSection section;
        BlockPos blockPos;
        int lastChunkX, lastChunkZ;
        int absY15 = absY & 15;
        minX = Math.min(startX, endX);
        minZ = Math.min(startZ, endZ);
        maxX = Math.max(startX, endX);
        maxZ = Math.max(startZ, endZ);
        chunk = nmsOverworld.getChunk(minX, minZ);
        sections = chunk.getSections();
        lastChunkX = minX >> 4;
        lastChunkZ = minZ >> 4;
        section = sections[chunk.getSectionIndex(absY15)];
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                blockPos = new BlockPos(x, absY, z);
                final int sectionX = x >> 4;
                final int sectionZ = z >> 4;
                if (lastChunkX != sectionX ||
                        lastChunkZ != sectionZ) {
                    lastChunkX = sectionX;
                    lastChunkZ = sectionZ;
                    chunk = nmsOverworld.getChunkAt(blockPos);
                    section = chunk.getSections()[chunk.getSectionIndex(absY15)];
                }
                WORKLOAD_RUNNABLE.addWorkload(new main.utils.modules.arenas.Utils.SetTask(blockPos, chunk, section, null, material, x, absY15, z, true));
            }
        }
    }

    public static void setArea(int absY, int startX, int startZ, int endX, int endZ, BlockState[] materials, int size) {
        int minX, maxX, minZ, maxZ;
        LevelChunk chunk;
        LevelChunkSection[] sections;
        LevelChunkSection section;
        BlockPos blockPos;
        int lastChunkX, lastChunkZ;
        int absY15;
        minX = Math.min(startX, endX);
        minZ = Math.min(startZ, endZ);
        maxX = Math.max(startX, endX);
        maxZ = Math.max(startZ, endZ);
        chunk = nmsOverworld.getChunk(minX, minZ);
        sections = chunk.getSections();
        lastChunkX = minX >> 4;
        lastChunkZ = minZ >> 4;
        absY15 = absY & 15;
        section = sections[chunk.getSectionIndex(absY15)];
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                blockPos = new BlockPos(x, absY, z);
                final int sectionX = x >> 4;
                final int sectionZ = z >> 4;
                if (lastChunkX != sectionX || lastChunkZ != sectionZ) {
                    lastChunkX = sectionX;
                    lastChunkZ = sectionZ;
                    chunk = nmsOverworld.getChunkAt(blockPos);
                    section = chunk.getSections()[chunk.getSectionIndex(absY15)];
                }
                WORKLOAD_RUNNABLE.addWorkload(new main.utils.modules.arenas.Utils.SetTask(blockPos, chunk, section, null, materials[RANDOM.nextInt(size)], x, absY15, z, true));
            }
        }
    }

    public static class WorkloadRunnable implements Runnable {
        private final Deque<SetTask> workloadDeque = new ArrayDeque<>();

        public void addWorkload(SetTask workload) {
            workloadDeque.add(workload);
        }

        @Override
        public void run() {
            final long stopTime = System.currentTimeMillis() + 50L;
            SetTask nextLoad;
            while (System.currentTimeMillis() <= stopTime &&
                    (nextLoad = workloadDeque.poll()) != null) {
                nextLoad.compute();
            }
        }
    }

    public static class SetTask {
        private final BlockPos blockPos;
        private final LevelChunk chunk;
        private final LevelChunkSection section;
        private final Block block;
        private final BlockState material;
        private final int x;
        private final int y;
        private final int z;
        private final boolean optimization;

        public SetTask(final BlockPos blockPos,
                       final LevelChunk chunk,
                       final LevelChunkSection section,
                       final Block block,
                       final BlockState material,
                       final int x,
                       final int y,
                       final int z,
                       final boolean optimization) {
            this.blockPos = blockPos;
            this.chunk = chunk;
            this.section = section;
            this.block = block;
            this.material = material;
            this.x = x;
            this.y = y;
            this.z = z;
            this.optimization = optimization;
        }

        public void compute() {
            if (this.optimization) {
                nmsOverworld.capturedTileEntities.remove(blockPos);
                section.setBlockState(x & 15, y, z & 15, material);
                chunkSource.blockChanged(blockPos);
                lightEngine.checkBlock(blockPos);
            } else if (chunk.getBlockState(blockPos).getBlock() != block) {
                section.setBlockState(x & 15, y, z & 15, material);
                chunkSource.blockChanged(blockPos);
            }
        }
    }
}
