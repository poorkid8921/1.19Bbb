package main.managers;

import main.Economy;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;

import java.util.ArrayDeque;
import java.util.Deque;

import static main.Economy.overworld;

public class ArenaManager {
    public final WorkloadRunnable WORKLOAD_RUNNABLE = new WorkloadRunnable();
    public ServerLevel nmsOverworld;
    public ServerChunkCache chunkSource;
    public ThreadedLevelLightEngine lightEngine;

    public ArenaManager() {
        Bukkit.getScheduler().runTaskTimer(Economy.INSTANCE, WORKLOAD_RUNNABLE, 1L, 1L);

        nmsOverworld = ((CraftWorld) overworld).getHandle();
        chunkSource = nmsOverworld.getChunkSource();
        lightEngine = chunkSource.getLightEngine();
    }

    public void setCuboid(short[][] positions, Block block, BlockState material) {
        int minY, maxY, minX, maxX, minZ, maxZ;
        LevelChunk chunk;
        LevelChunkSection[] sections;
        LevelChunkSection section = null;
        BlockPos blockPos;
        int lastChunkX, lastChunkZ;
        int absY15 = 0;

        for (short[] k : positions) {
            minX = Math.min(k[0], k[3]);
            minY = Math.min(k[1], k[4]);
            minZ = Math.min(k[2], k[5]);
            maxX = Math.max(k[0], k[3]);
            maxY = Math.max(k[1], k[4]);
            maxZ = Math.max(k[2], k[5]);
            int lastY = minY;
            chunk = nmsOverworld.getChunk(minX, minZ);
            sections = chunk.getSections();
            lastChunkX = 0;
            lastChunkZ = 0;
            for (int x = minX; x <= maxX; x++)
                for (int y = minY; y <= maxY; y++) {
                    if (lastY != y) {
                        lastY = y;
                        absY15 = lastY & 15;
                        section = sections[chunk.getSectionIndex(y)];
                    }

                    for (int z = minZ; z <= maxZ; z++) {
                        blockPos = new BlockPos(x, y, z);
                        final int sectionX = x >> 4;
                        final int sectionZ = z >> 4;
                        if (lastChunkX != sectionX ||
                                lastChunkZ != sectionZ) {
                            lastChunkX = sectionX;
                            lastChunkZ = sectionZ;
                            chunk = nmsOverworld.getChunkAt(blockPos);
                            sections = chunk.getSections();
                            section = sections[chunk.getSectionIndex(y)];
                        }
                        WORKLOAD_RUNNABLE.addWorkload(new SetTask(blockPos, chunk, section, block, material, x, absY15, z, false));
                    }
                }
        }
    }

    public void setArea(int absY, short[][] positions, BlockState material) {
        int minX, minZ, maxX, maxZ;
        BlockPos blockPos;
        LevelChunk chunk;
        LevelChunkSection[] sections;
        LevelChunkSection section;
        int lastChunkX, lastChunkZ;
        int absY15 = absY & 15;
        for (short[] k : positions) {
            minX = Math.min(k[0], k[2]);
            minZ = Math.min(k[1], k[3]);
            maxX = Math.max(k[0], k[2]);
            maxZ = Math.max(k[1], k[3]);
            chunk = nmsOverworld.getChunk(minX, minZ);
            sections = chunk.getSections();
            section = sections[chunk.getSectionIndex(absY)];
            lastChunkX = minX >> 4;
            lastChunkZ = minZ >> 4;

            for (int x = minX; x <= maxX; x++)
                for (int z = minZ; z <= maxZ; z++) {
                    blockPos = new BlockPos(x, absY, z);
                    final int sectionX = x >> 4;
                    final int sectionZ = z >> 4;
                    if (lastChunkX != sectionX || lastChunkZ != sectionZ) {
                        lastChunkX = sectionX;
                        lastChunkZ = sectionZ;
                        chunk = nmsOverworld.getChunkAt(blockPos);
                        sections = chunk.getSections();
                        section = sections[chunk.getSectionIndex(absY)];
                    }
                    WORKLOAD_RUNNABLE.addWorkload(new SetTask(blockPos, chunk, section, null, material, x, absY15, z, true));
                }
        }
    }

    public class WorkloadRunnable implements Runnable {
        private final Deque<SetTask> workloadDeque = new ArrayDeque<>();

        public void addWorkload(SetTask workload) {
            workloadDeque.add(workload);
        }

        @Override
        public void run() {
            final long stopTime = System.currentTimeMillis() + 50L;
            SetTask nextLoad;
            while (System.currentTimeMillis() <= stopTime && (nextLoad = workloadDeque.poll()) != null) {
                nextLoad.compute();
            }
        }
    }

    public class SetTask {
        private final BlockPos blockPos;
        private final LevelChunk chunk;
        private final LevelChunkSection section;
        private final Block block;
        private final BlockState material;
        private final int x;
        private final int absY15;
        private final int z;
        private final boolean optimization;

        public SetTask(BlockPos blockPos, LevelChunk chunk, LevelChunkSection section, Block block, BlockState material, int x, int absY15, int z, boolean optimization) {
            this.blockPos = blockPos;
            this.chunk = chunk;
            this.section = section;
            this.block = block;
            this.material = material;
            this.x = x;
            this.absY15 = absY15;
            this.z = z;
            this.optimization = optimization;
        }

        public void compute() {
            if (this.optimization) {
                nmsOverworld.capturedTileEntities.remove(blockPos);
                section.setBlockState(x & 15, absY15, z & 15, material);
                chunkSource.blockChanged(blockPos);
                lightEngine.checkBlock(blockPos);
            } else if (chunk.getBlockState(blockPos).getBlock() != block) {
                nmsOverworld.capturedTileEntities.remove(blockPos);
                section.setBlockState(x & 15, absY15, z & 15, material);
                chunkSource.blockChanged(blockPos);
            }
        }
    }
}
