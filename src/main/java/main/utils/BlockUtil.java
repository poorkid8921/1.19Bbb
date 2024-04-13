package main.utils;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlockUtil {
    private static final Map<Material, Object> NMS_BLOCK_MATERIALS = new Object2ObjectOpenHashMap<>();
    private static final Map<String, Object> NMS_BLOCK_NAMES = new Object2ObjectOpenHashMap<>();
    private static final Map<World, Object> NMS_WORLDS = new Object2ObjectOpenHashMap<>();
    private static final Map<String, Object> NMS_WORLD_NAMES = new Object2ObjectOpenHashMap<>();
    private static final MethodHandle WORLD_GET_HANDLE;
    private static final MethodHandle NMS_ITEM_STACK_COPY;
    private static final MethodHandle NMS_BLOCK_FROM_ITEM;
    private static final MethodHandle NMS_BLOCK_NAME;
    private static final MethodHandle NMS_ITEM_STACK_TO_ITEM;
    private static final MethodHandle ITEM_TO_BLOCK_DATA;
    private static final MethodHandle SET_TYPE_AND_DATA;
    private static final MethodHandle WORLD_GET_CHUNK;
    private static final MethodHandle CHUNK_GET_SECTIONS;
    private static final MethodHandle CHUNK_SECTION_SET_TYPE;
    private static final MethodHandle GET_LEVEL_HEIGHT_ACCESSOR;
    private static final MethodHandle GET_SECTION_INDEX;
    private static final MethodHandle CHUNK_SET_TYPE;
    private static final MethodHandle BLOCK_NOTIFY;
    private static final MethodHandle CRAFT_BLOCK_GET_NMS_BLOCK;
    private static final MethodHandle NMS_BLOCK_GET_BLOCK_DATA;
    private static final MethodHandle WORLD_CAPTURED_TILE_ENTITIES;
    private static final MethodHandle IS_TILE_ENTITY;
    private static final MethodHandle WORLD_REMOVE_TILE_ENTITY;
    private static final BlockUpdater BLOCK_UPDATER;
    private static final BlockPosition BLOCK_POSITION_CONSTRUCTOR;
    private static final BlockDataRetriever BLOCK_DATA_GETTER;
    private static final TileEntityManager TILE_ENTITY_MANAGER;
    private static final WorkloadRunnable WORKLOAD_RUNNABLE;
    private static final JavaPlugin PLUGIN;
    private static final Object AIR_BLOCK_DATA;

    static {

        Class<?> worldServer = ReflectionUtils.getNMSClass("server.level", "WorldServer");
        Class<?> world = ReflectionUtils.getNMSClass("world.level", "World");
        Class<?> craftWorld = ReflectionUtils.getCraftClass("CraftWorld");
        Class<?> craftBlock = ReflectionUtils.getCraftClass("block.CraftBlock");
        Class<?> blockPosition = ReflectionUtils.supports(8) ? ReflectionUtils.getNMSClass("core", "BlockPosition") : null;
        Class<?> blocks = ReflectionUtils.getNMSClass("world.level.block", "Blocks");
        Class<?> mutableBlockPosition = ReflectionUtils.supports(8) ? ReflectionUtils.getNMSClass("core", "BlockPosition$MutableBlockPosition") : null;
        Class<?> blockData = ReflectionUtils.supports(8) ? ReflectionUtils.getNMSClass("world.level.block.state", "IBlockData") : null;
        Class<?> craftItemStack = ReflectionUtils.getCraftClass("inventory.CraftItemStack");
        Class<?> worldItemStack = ReflectionUtils.getNMSClass("world.item", "ItemStack");
        Class<?> item = ReflectionUtils.getNMSClass("world.item", "Item");
        Class<?> block = ReflectionUtils.getNMSClass("world.level.block", "Block");
        Class<?> chunk = ReflectionUtils.getNMSClass("world.level.chunk", "Chunk");
        Class<?> chunkSection = ReflectionUtils.getNMSClass("world.level.chunk", "ChunkSection");
        Class<?> levelHeightAccessor = ReflectionUtils.supports(17) ? ReflectionUtils.getNMSClass("world.level.LevelHeightAccessor") : null;
        Class<?> blockDataReference = ReflectionUtils.supports(13) ? craftBlock : block;
        Class<?> craftBlockEntityState = ReflectionUtils.supports(12) ? ReflectionUtils.getCraftClass("block.CraftBlockEntityState") : ReflectionUtils.getCraftClass("block.CraftBlockState");

        Method getNMSBlockMethod = null;

        if (ReflectionUtils.MINOR_NUMBER <= 12) {
            try {
                getNMSBlockMethod = craftBlock.getDeclaredMethod("getNMSBlock");
                getNMSBlockMethod.setAccessible(true);
            } catch (NoSuchMethodException | SecurityException e2) {
                e2.printStackTrace();
            }
        }

        MethodHandles.Lookup lookup = MethodHandles.lookup();

        Object airBlockData = null;
        try {
            airBlockData = lookup.findStatic(block, ReflectionUtils.supports(18) ? "a" : "getByCombinedId", MethodType.methodType(blockData, int.class)).invoke(0);
        } catch (Throwable e1) {
            e1.printStackTrace();
        }
        AIR_BLOCK_DATA = airBlockData;

        MethodHandle worldGetHandle = null;
        MethodHandle nmsItemStackCopy = null;
        MethodHandle blockFromItem = null;
        MethodHandle blockName = null;
        MethodHandle nmsItemStackToItem = null;
        MethodHandle itemToBlockData = null;
        MethodHandle setTypeAndData = null;
        MethodHandle worldGetChunk = null;
        MethodHandle chunkSetTypeM = null;
        MethodHandle blockNotify = null;
        MethodHandle chunkGetSections = null;
        MethodHandle chunkSectionSetType = null;
        MethodHandle getLevelHeightAccessor = null;
        MethodHandle getSectionIndex = null;
        MethodHandle mutableBlockPositionSet;
        MethodHandle mutableBlockPositionXYZ;
        MethodHandle craftBlockGetNMSBlock = null;
        MethodHandle nmsBlockGetBlockData = null;
        MethodHandle worldRemoveTileEntity = null;
        MethodHandle worldCapturedTileEntities = null;
        MethodHandle capturedTileEntitiesContainsKey = null;

        // Method names
        String asBlock = ReflectionUtils.supports(18) || ReflectionUtils.MINOR_NUMBER < 8 ? "a" : "asBlock";
        String blockGetName = ReflectionUtils.supports(20) ? ReflectionUtils.supportsPatch(4) ? "h" : "f" : ReflectionUtils.supports(18) ? "h" : "a";
        String getBlockData = ReflectionUtils.supports(20) ? ReflectionUtils.supportsPatch(4) ? "o" : "n" : ReflectionUtils.supports(19) ? ReflectionUtils.supportsPatch(3) ? "o" : "m" : ReflectionUtils.supports(18) ? "n" : "getBlockData";
        String getItem = ReflectionUtils.supports(20) ? "d" : ReflectionUtils.supports(18) ? "c" : "getItem";
        String setType = ReflectionUtils.supports(18) ? "a" : "setTypeAndData";
        String getChunkAt = ReflectionUtils.supports(18) ? "d" : "getChunkAt";
        String chunkSetType = ReflectionUtils.supports(18) ? "a" : ReflectionUtils.MINOR_NUMBER < 8 ? "setTypeId" : ReflectionUtils.MINOR_NUMBER <= 12 ? "a" : "setType";
        String notify = ReflectionUtils.supports(18) ? "a" : "notify";
        String getSections = ReflectionUtils.supports(18) ? "d" : "getSections";
        String sectionSetType = ReflectionUtils.supports(18) ? "a" : ReflectionUtils.MINOR_NUMBER < 8 ? "setTypeId" : "setType";
        String setXYZ = ReflectionUtils.supports(13) ? "d" : "c";
        String getBlockData2 = ReflectionUtils.supports(13) ? "getNMS" : "getBlockData";
        String removeTileEntity = ReflectionUtils.supports(20) && ReflectionUtils.supportsPatch(4) ? "o" : ReflectionUtils.supports(19) ? "n" : ReflectionUtils.supports(18) ? "m" : ReflectionUtils.supports(14) ? "removeTileEntity" : ReflectionUtils.supports(13) ? "n" : ReflectionUtils.supports(9) ? "s" : ReflectionUtils.supports(8) ? "t" : "p";
        MethodType notifyMethodType = ReflectionUtils.MINOR_NUMBER >= 14 ? MethodType.methodType(void.class, blockPosition, blockData, blockData, int.class) : ReflectionUtils.MINOR_NUMBER < 8 ? MethodType.methodType(void.class, int.class, int.class, int.class) : ReflectionUtils.MINOR_NUMBER == 8 ? MethodType.methodType(void.class, blockPosition) : MethodType.methodType(void.class, blockPosition, blockData, blockData, int.class);
        MethodType chunkSetTypeMethodType = ReflectionUtils.MINOR_NUMBER <= 12 ? ReflectionUtils.MINOR_NUMBER >= 8 ? MethodType.methodType(blockData, blockPosition, blockData) : MethodType.methodType(boolean.class, int.class, int.class, int.class, block, int.class) : MethodType.methodType(blockData, blockPosition, blockData, boolean.class);
        MethodType chunkSectionSetTypeMethodType = ReflectionUtils.MINOR_NUMBER >= 14 ? MethodType.methodType(blockData, int.class, int.class, int.class, blockData) : ReflectionUtils.MINOR_NUMBER < 8 ? MethodType.methodType(void.class, int.class, int.class, int.class, block) : MethodType.methodType(void.class, int.class, int.class, int.class, blockData);
        MethodType removeTileEntityMethodType = ReflectionUtils.supports(8) ? MethodType.methodType(void.class, blockPosition) : MethodType.methodType(void.class, int.class, int.class, int.class);
        BlockPosition BlockPosition = null;

        try {
            worldGetHandle = lookup.findVirtual(craftWorld, "getHandle", MethodType.methodType(worldServer));
            worldGetChunk = lookup.findVirtual(worldServer, getChunkAt, MethodType.methodType(chunk, int.class, int.class));
            nmsItemStackCopy = lookup.findStatic(craftItemStack, "asNMSCopy", MethodType.methodType(worldItemStack, ItemStack.class));
            blockFromItem = lookup.findStatic(block, asBlock, MethodType.methodType(block, item));

            blockName = lookup.findVirtual(block, blockGetName, MethodType.methodType(String.class));
            mutableBlockPositionXYZ = lookup.findConstructor(mutableBlockPosition, MethodType.methodType(void.class, int.class, int.class, int.class));
            itemToBlockData = lookup.findVirtual(block, getBlockData, MethodType.methodType(blockData));
            setTypeAndData = lookup.findVirtual(worldServer, setType, MethodType.methodType(boolean.class, blockPosition, blockData, int.class));
            mutableBlockPositionSet = lookup.findVirtual(mutableBlockPosition, setXYZ, MethodType.methodType(mutableBlockPosition, int.class, int.class, int.class));
            BlockPosition = new BlockPosition(mutableBlockPositionXYZ, mutableBlockPositionSet);
            nmsItemStackToItem = lookup.findVirtual(worldItemStack, getItem, MethodType.methodType(item));
            chunkSetTypeM = lookup.findVirtual(chunk, chunkSetType, chunkSetTypeMethodType);
            blockNotify = lookup.findVirtual(worldServer, notify, notifyMethodType);
            chunkGetSections = lookup.findVirtual(chunk, getSections, MethodType.methodType(ReflectionUtils.toArrayClass(chunkSection)));
            chunkSectionSetType = lookup.findVirtual(chunkSection, sectionSetType, chunkSectionSetTypeMethodType);
            if (ReflectionUtils.supports(18)) {
                getLevelHeightAccessor = lookup.findVirtual(chunk, "z", MethodType.methodType(levelHeightAccessor));
                getSectionIndex = lookup.findVirtual(levelHeightAccessor, "e", MethodType.methodType(int.class, int.class));
            } else if (ReflectionUtils.supports(17)) {
                getSectionIndex = lookup.findVirtual(chunk, "getSectionIndex", MethodType.methodType(int.class, int.class));
            }
            craftBlockGetNMSBlock = ReflectionUtils.MINOR_NUMBER <= 12 ? lookup.unreflect(getNMSBlockMethod) : null;
            nmsBlockGetBlockData = lookup.findVirtual(blockDataReference, getBlockData2, MethodType.methodType(blockData));
            worldRemoveTileEntity = lookup.findVirtual(world, removeTileEntity, removeTileEntityMethodType);
            worldCapturedTileEntities = ReflectionUtils.supports(8) ? lookup.findGetter(world, "capturedTileEntities", Map.class) : null;
            capturedTileEntitiesContainsKey = ReflectionUtils.supports(8) ? lookup.findVirtual(Map.class, "containsKey", MethodType.methodType(boolean.class, Object.class)) : null;
            Method getTileEntityMethod = craftBlockEntityState.getDeclaredMethod("getTileEntity");
            Method getSnapshotMethod = ReflectionUtils.supports(12) ? craftBlockEntityState.getDeclaredMethod("getSnapshot") : null;
            if (getTileEntityMethod != null) getTileEntityMethod.setAccessible(true);
            if (getSnapshotMethod != null) getSnapshotMethod.setAccessible(true);
        } catch (NoSuchMethodException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        WORLD_GET_HANDLE = worldGetHandle;
        WORLD_GET_CHUNK = worldGetChunk;
        NMS_ITEM_STACK_COPY = nmsItemStackCopy;
        NMS_BLOCK_FROM_ITEM = blockFromItem;
        NMS_BLOCK_NAME = blockName;
        NMS_ITEM_STACK_TO_ITEM = nmsItemStackToItem;
        ITEM_TO_BLOCK_DATA = itemToBlockData;
        SET_TYPE_AND_DATA = setTypeAndData;
        CHUNK_SET_TYPE = chunkSetTypeM;
        BLOCK_NOTIFY = blockNotify;
        CHUNK_GET_SECTIONS = chunkGetSections;
        CHUNK_SECTION_SET_TYPE = chunkSectionSetType;
        GET_LEVEL_HEIGHT_ACCESSOR = getLevelHeightAccessor;
        GET_SECTION_INDEX = getSectionIndex;
        BLOCK_POSITION_CONSTRUCTOR = BlockPosition;
        CRAFT_BLOCK_GET_NMS_BLOCK = craftBlockGetNMSBlock;
        NMS_BLOCK_GET_BLOCK_DATA = nmsBlockGetBlockData;
        WORLD_REMOVE_TILE_ENTITY = worldRemoveTileEntity;
        WORLD_CAPTURED_TILE_ENTITIES = worldCapturedTileEntities;
        IS_TILE_ENTITY = capturedTileEntitiesContainsKey;

        BLOCK_DATA_GETTER = new BlockDataGetter();
        BLOCK_UPDATER = new BlockUpdater(BLOCK_NOTIFY, GET_SECTION_INDEX, GET_LEVEL_HEIGHT_ACCESSOR);
        TILE_ENTITY_MANAGER = ReflectionUtils.supports(8) ? new TileEntityManagerSupported() : new TileEntityManagerDummy();
        Arrays.stream(Material.values()).filter(Material::isBlock).forEach(BlockUtil::addNMSBlockData);
        NMS_BLOCK_MATERIALS.put(Material.AIR, AIR_BLOCK_DATA);
        Arrays.stream(blocks.getDeclaredFields()).filter(field -> field.getType() == block).map(field -> {
            try {
                return field.get(block);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }).forEach(nmsBlock -> {
            try {
                String name = (String) NMS_BLOCK_NAME.invoke(nmsBlock);
                name = name.substring(name.lastIndexOf(".") + 1).toUpperCase();
                NMS_BLOCK_NAMES.put(name, nmsBlock);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });

        Bukkit.getWorlds().forEach(BlockUtil::addNMSWorld);
        WORKLOAD_RUNNABLE = new WorkloadRunnable();
        PLUGIN = JavaPlugin.getProvidingPlugin(BlockUtil.class);
        Bukkit.getScheduler().runTaskTimer(PLUGIN, WORKLOAD_RUNNABLE, 1, 1);
    }

    private static void addNMSBlockData(Material material) {
        ItemStack itemStack = new ItemStack(material);
        Object nmsData = getNMSBlockData(itemStack);
        if (nmsData != null) NMS_BLOCK_MATERIALS.put(material, nmsData);
    }

    private static void addNMSWorld(World world) {
        if (world == null) return;
        Object nmsWorld = getNMSWorld(world);
        if (nmsWorld != null) {
            NMS_WORLDS.put(world, nmsWorld);
            NMS_WORLD_NAMES.put(world.getName(), nmsWorld);
        }
    }

    public static CompletableFuture<Void> setSectionCuboidAsynchronously(Location loc1, Location loc2, ItemStack itemStack) {
        World world = loc1.getWorld();
        Object nmsWorld = getWorld(world);
        Object blockData = getBlockData(itemStack);
        int x1 = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int y1 = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int z1 = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int x2 = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int y2 = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int z2 = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
        int sizeX = Math.abs(x2 - x1) + 1;
        int sizeY = Math.abs(y2 - y1) + 1;
        int sizeZ = Math.abs(z2 - z1) + 1;
        int x3 = 0, y3 = 0, z3 = 0;
        Location location = new Location(loc1.getWorld(), x1 + x3, y1 + y3, z1 + z3);
        Object blockPosition = newMutableBlockPosition(location);
        CompletableFuture<Void> workloadFinishFuture = new CompletableFuture<>();
        WorkloadRunnable workloadRunnable = new WorkloadRunnable();
        BukkitTask workloadTask = Bukkit.getScheduler().runTaskTimer(PLUGIN, workloadRunnable, 1L, 3L);
        for (int i = 0; i < sizeX * sizeY * sizeZ; i++) {
            SectionSetWorkload workload = new SectionSetWorkload(nmsWorld, blockPosition, blockData, location.clone());
            if (++x3 >= sizeX) {
                x3 = 0;
                if (++y3 >= sizeY) {
                    y3 = 0;
                    ++z3;
                }
            }
            location.setX(x1 + x3);
            location.setY(y1 + y3);
            location.setZ(z1 + z3);
            workloadRunnable.addWorkload(workload);
        }
        workloadRunnable.whenComplete(() -> {
            workloadFinishFuture.complete(null);
            workloadTask.cancel();
        });
        return workloadFinishFuture;
    }

    private static Object getSection(Object nmsChunk, Object[] sections, int y) {
        return BLOCK_UPDATER.getSection(nmsChunk, sections, y);
    }

    private static Object[] getSections(Object nmsChunk) {
        try {
            return (Object[]) CHUNK_GET_SECTIONS.invoke(nmsChunk);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void setTypeChunkSection(Object chunkSection, int x, int y, int z, Object blockData) {
        try {
            CHUNK_SECTION_SET_TYPE.invoke(chunkSection, x, y, z, blockData);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static Object getChunkAt(Object world, int x, int z) {
        try {
            return WORLD_GET_CHUNK.invoke(world, x >> 4, z >> 4);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Object getNMSWorld(@Nonnull World world) {
        try {
            return WORLD_GET_HANDLE.invoke(world);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private static @Nullable Object getNMSBlockData(@Nullable ItemStack itemStack) {
        try {
            if (itemStack == null) return null;
            Object nmsItemStack = NMS_ITEM_STACK_COPY.invoke(itemStack);
            if (nmsItemStack == null) return null;
            Object nmsItem = NMS_ITEM_STACK_TO_ITEM.invoke(nmsItemStack);
            Object block = NMS_BLOCK_FROM_ITEM.invoke(nmsItem);
            if (ReflectionUtils.MINOR_NUMBER < 8) return block;
            return ITEM_TO_BLOCK_DATA.invoke(block);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void removeIfTileEntity(Object nmsWorld, Object blockPosition) {
        if (TILE_ENTITY_MANAGER.isTileEntity(nmsWorld, blockPosition))
            TILE_ENTITY_MANAGER.destroyTileEntity(nmsWorld, blockPosition);
    }

    public static void updateBlock(Object world, Object blockPosition, Object blockData) {
        BLOCK_UPDATER.update(world, blockPosition, blockData);
    }

    public static Object newMutableBlockPosition(Location location) {
        try {
            return BLOCK_POSITION_CONSTRUCTOR.newMutableBlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object setBlockPosition(Object mutableBlockPosition, Object x, Object y, Object z) {
        try {
            return BLOCK_POSITION_CONSTRUCTOR.set(mutableBlockPosition, x, y, z);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static @Nonnull Object getBlockData(@Nonnull ItemStack itemStack) {
        Object blockData = BLOCK_DATA_GETTER.fromItemStack(itemStack);
        if (blockData == null) throw new IllegalArgumentException("Couldn't convert specified itemstack to block data");
        return blockData;
    }

    public static Object getWorld(World world) {
        return NMS_WORLDS.get(world);
    }

    public static Object getWorld(String worldName) {
        return NMS_WORLD_NAMES.get(worldName);
    }

    private interface TileEntityManager {
        default Object getCapturedTileEntities(Object nmsWorld) {
            try {
                return WORLD_CAPTURED_TILE_ENTITIES.invoke(nmsWorld);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        }

        default boolean isTileEntity(Object nmsWorld, Object blockPosition) {
            try {
                return (boolean) IS_TILE_ENTITY.invoke(getCapturedTileEntities(nmsWorld), blockPosition);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return false;
        }

        default void destroyTileEntity(Object nmsWorld, Object blockPosition) {
            try {
                WORLD_REMOVE_TILE_ENTITY.invoke(nmsWorld, blockPosition);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private interface BlockDataRetriever {
        default Object getNMSItem(ItemStack itemStack) throws Throwable {
            if (itemStack == null) throw new NullPointerException("ItemStack is null!");
            if (itemStack.getType() == Material.AIR) return null;
            if (NMS_BLOCK_NAMES.containsKey(itemStack.getType().name())) return null;
            Object nmsItemStack = NMS_ITEM_STACK_COPY.invoke(itemStack);
            if (nmsItemStack == null) return null;
            return NMS_ITEM_STACK_TO_ITEM.invoke(nmsItemStack);
        }

        // 1.7-1.12 requires 2 methods to get block data
        default Object fromBlock(Block block) {
            try {
                Object nmsBlock = CRAFT_BLOCK_GET_NMS_BLOCK.invoke(block);
                return NMS_BLOCK_GET_BLOCK_DATA.invoke(nmsBlock);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        }

        Object fromItemStack(ItemStack itemStack);
    }

    private interface Workload {
        void compute();
    }

    private static class TileEntityManagerSupported implements TileEntityManager {
    }

    private static class TileEntityManagerDummy implements TileEntityManager {
        @Override
        public Object getCapturedTileEntities(Object nmsWorld) {
            return null;
        }

        @Override
        public boolean isTileEntity(Object nmsWorld, Object blockPosition) {
            return false;
        }

        @Override
        public void destroyTileEntity(Object nmsWorld, Object blockPosition) {
        }
    }

    // 1.13+ or 1.8+ without data support
    private static class BlockDataGetter implements BlockDataRetriever {
        @Override
        public Object fromItemStack(ItemStack itemStack) {
            try {
                Object nmsItem = getNMSItem(itemStack);
                Object block = nmsItem != null ? NMS_BLOCK_FROM_ITEM.invoke(nmsItem) : NMS_BLOCK_NAMES.get(itemStack.getType().name());
                return ITEM_TO_BLOCK_DATA.invoke(block);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        }

        // 1.13+ one method to get block data (getNMS())
        @Override
        public Object fromBlock(Block block) {
            try {
                return NMS_BLOCK_GET_BLOCK_DATA.invoke(block);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    private static class WorkloadRunnable implements Runnable {
        private static final double MAX_MILLIS_PER_TICK = 50.0;
        private static final int MAX_NANOS_PER_TICK = (int) (MAX_MILLIS_PER_TICK * 1E6);

        private final Deque<Workload> workloadDeque = new ArrayDeque<>();

        public void addWorkload(Workload workload) {
            this.workloadDeque.add(workload);
        }

        public void whenComplete(Runnable runnable) {
            this.workloadDeque.add(new WhenCompleteWorkload(runnable));
        }

        @Override
        public void run() {
            long stopTime = System.nanoTime() + MAX_NANOS_PER_TICK;
            Workload nextLoad;
            while (System.nanoTime() <= stopTime && (nextLoad = this.workloadDeque.poll()) != null) {
                nextLoad.compute();
            }
        }
    }

    private static class SectionSetWorkload implements Workload {
        private final Object nmsWorld;
        private final Object blockPosition;
        private final Object blockData;
        private final Location location;

        public SectionSetWorkload(Object nmsWorld, Object blockPosition, Object blockData, Location location) {
            this.nmsWorld = nmsWorld;
            this.blockPosition = blockPosition;
            this.blockData = blockData;
            this.location = location;
        }

        @Override
        public void compute() {
            BlockUtil.setBlockPosition(blockPosition, location.getBlockX(), location.getBlockY(), location.getBlockZ());
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();
            Object nmsChunk = BlockUtil.getChunkAt(nmsWorld, x, z);
            int j = x & 15;
            int k = y & 15;
            int l = z & 15;
            BlockUtil.removeIfTileEntity(nmsWorld, blockPosition);
            BlockUtil.setTypeChunkSection(BlockUtil.getSection(nmsChunk, BlockUtil.getSections(nmsChunk), y), j, k, l, blockData);
            BlockUtil.updateBlock(nmsWorld, blockPosition, blockData);
        }
    }

    private static class WhenCompleteWorkload implements Workload {
        private final Runnable runnable;

        public WhenCompleteWorkload(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void compute() {
            runnable.run();
        }
    }
}

class BlockPosition {
    private final MethodHandle mutableBlockPosition;
    private final MethodHandle mutableBlockPositionSet;

    public BlockPosition(MethodHandle mutableBlockPositionXYZ, MethodHandle mutableBlockPositionSet) {
        this.mutableBlockPosition = mutableBlockPositionXYZ;
        this.mutableBlockPositionSet = mutableBlockPositionSet;
    }

    public Object newMutableBlockPosition(Object x, Object y, Object z) {
        try {
            return mutableBlockPosition.invoke(x, y, z);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object set(Object mutableBlockPosition, Object x, Object y, Object z) {
        try {
            return mutableBlockPositionSet.invoke(mutableBlockPosition, x, y, z);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}

class BlockUpdater {

    private final MethodHandle blockNotify;
    private final MethodHandle sectionIndexGetter;
    private final MethodHandle levelHeightAccessorGetter;

    public BlockUpdater(MethodHandle blockNotify, MethodHandle sectionIndexGetter, MethodHandle levelHeightAccessorGetter) {
        this.blockNotify = blockNotify;
        this.sectionIndexGetter = sectionIndexGetter;
        this.levelHeightAccessorGetter = levelHeightAccessorGetter;
    }

    public void update(Object world, Object blockPosition, Object blockData) {
        try {
            blockNotify.invoke(world, blockPosition, blockData, blockData, 2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public Object getSection(Object nmsChunk, Object[] sections, int y) {
        return sections[getSectionIndex(nmsChunk, y)];
    }

    public Object getLevelHeightAccessor(Object nmsChunk) {
        try {
            return levelHeightAccessorGetter.invoke(nmsChunk);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getSectionIndex(Object nmsChunk, int y) {
        Object levelHeightAccessor = getLevelHeightAccessor(nmsChunk);
        try {
            return (int) sectionIndexGetter.invoke(levelHeightAccessor, y);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return -1;
    }
}

final class ReflectionUtils {
    public static final String NMS_VERSION;

    static {
        String found = null;
        for (Package pack : Package.getPackages()) {
            String name = pack.getName();

            // .v because there are other packages.
            if (name.startsWith("org.bukkit.craftbukkit.v")) {
                found = pack.getName().split("\\.")[3];

                // Just a final guard to make sure it finds this important class.
                // As a protection for forge+bukkit implementation that tend to mix versions.
                // The real CraftPlayer should exist in the package.
                // Note: Doesn't seem to function properly. Will need to separate the version
                // handler for NMS and CraftBukkit for softwares like catmc.
                try {
                    Class.forName("org.bukkit.craftbukkit." + found + ".entity.CraftPlayer");
                    break;
                } catch (ClassNotFoundException e) {
                    found = null;
                }
            }
        }
        if (found == null)
            throw new IllegalArgumentException("Failed to parse server version. Could not find any package starting with name: 'org.bukkit.craftbukkit.v'");
        NMS_VERSION = found;
    }

    /**
     * The raw minor version number.
     * E.g. {@code v1_17_R1} to {@code 17}
     *
     * @see #supports(int)
     * @since 4.0.0
     */
    public static final int MINOR_NUMBER;
    /**
     * The raw patch version number. Refers to the
     * <a href="https://en.wikipedia.org/wiki/Software_versioning">major.minor.patch
     * version scheme</a>.
     * E.g.
     * <ul>
     * <li>{@code v1.20.4} to {@code 4}</li>
     * <li>{@code v1.18.2} to {@code 2}</li>
     * <li>{@code v1.19.1} to {@code 1}</li>
     * </ul>
     * <p>
     * I'd not recommend developers to support individual patches at all. You should
     * always support the latest patch.
     * For example, between v1.14.0, v1.14.1, v1.14.2, v1.14.3 and v1.14.4 you
     * should only support v1.14.4
     * <p>
     * This can be used to warn server owners when your plugin will break on older
     * patches.
     *
     * @see #supportsPatch(int)
     * @since 7.0.0
     */
    public static final int PATCH_NUMBER;

    static {
        String[] split = NMS_VERSION.substring(1).split("_");
        if (split.length < 1) {
            throw new IllegalStateException("Version number division error: " + Arrays.toString(split) + ' ' + getVersionInformation());
        }

        String minorVer = split[1];
        try {
            MINOR_NUMBER = Integer.parseInt(minorVer);
            if (MINOR_NUMBER < 0)
                throw new IllegalStateException("Negative minor number? " + minorVer + ' ' + getVersionInformation());
        } catch (Throwable ex) {
            throw new RuntimeException("Failed to parse minor number: " + minorVer + ' ' + getVersionInformation(), ex);
        }

        // Bukkit.getBukkitVersion() = "1.12.2-R0.1-SNAPSHOT"
        Matcher bukkitVer = Pattern.compile("^\\d+\\.\\d+\\.(\\d+)").matcher(Bukkit.getBukkitVersion());
        if (bukkitVer.find()) { // matches() won't work, we just want to match the start using "^"
            try {
                // group(0) gives the whole matched string, we just want the captured group.
                PATCH_NUMBER = Integer.parseInt(bukkitVer.group(1));
            } catch (Throwable ex) {
                throw new RuntimeException("Failed to parse minor number: " + bukkitVer + ' ' + getVersionInformation(), ex);
            }
        } else {
            // 1.8-R0.1-SNAPSHOT
            PATCH_NUMBER = 0;
        }
    }

    /**
     * Gets the full version information of the server. Useful for including in
     * errors.
     *
     * @since 7.0.0
     */
    public static String getVersionInformation() {
        return "(NMS: " + NMS_VERSION + " | " + "Minecraft: " + Bukkit.getVersion() + " | " + "Bukkit: " + Bukkit.getBukkitVersion() + ')';
    }

    /**
     * Gets the latest known patch number of the given minor version.
     * For example: 1.14 -> 4, 1.17 -> 10
     * The latest version is expected to get newer patches, so make sure to account
     * for unexpected results.
     *
     * @param minorVersion the minor version to get the patch number of.
     * @return the patch number of the given minor version if recognized, otherwise
     * null.
     * @since 7.0.0
     */
    public static Integer getLatestPatchNumberOf(int minorVersion) {
        if (minorVersion <= 0) throw new IllegalArgumentException("Minor version must be positive: " + minorVersion);

        // https://minecraft.wiki/w/Java_Edition_version_history
        // There are many ways to do this, but this is more visually appealing.
        int[] patches = { /* 1 */ 1, /* 2 */ 5, /* 3 */ 2, /* 4 */ 7, /* 5 */ 2, /* 6 */ 4, /* 7 */ 10, /* 8 */ 8, // I
                // don't
                // think
                // they
                // released
                // a
                // server
                // version
                // for
                // 1.8.9
                /* 9 */ 4,

                /* 10 */ 2, // ,_ _ _,
                /* 11 */ 2, // \o-o/
                /* 12 */ 2, // ,(.-.),
                /* 13 */ 2, // _/ |) (| \_
                /* 14 */ 4, // /\=-=/\
                /* 15 */ 2, // ,| \=/ |,
                /* 16 */ 5, // _/ \ | / \_
                /* 17 */ 1, // \_!_/
                /* 18 */ 2, /* 19 */ 4, /* 20 */ 4,};

        if (minorVersion > patches.length) return null;
        return patches[minorVersion - 1];
    }

    /**
     * Mojang remapped their NMS in 1.17: <a href=
     * "https://www.spigotmc.org/threads/spigot-bungeecord-1-17.510208/#post-4184317">Spigot
     * Thread</a>
     */
    public static final String CRAFTBUKKIT_PACKAGE = "org.bukkit.craftbukkit." + NMS_VERSION + '.', NMS_PACKAGE = v(17, "net.minecraft.").orElse("net.minecraft.server." + NMS_VERSION + '.');
    /**
     * A nullable public accessible field only available in {@code EntityPlayer}.
     * This can be null if the player is offline.
     */
    private static final MethodHandle PLAYER_CONNECTION;
    /**
     * Responsible for getting the NMS handler {@code EntityPlayer} object for the
     * player.
     * {@code CraftPlayer} is simply a wrapper for {@code EntityPlayer}.
     * Used mainly for handling packet related operations.
     * <p>
     * This is also where the famous player {@code ping} field comes from!
     */
    private static final MethodHandle GET_HANDLE;
    /**
     * Sends a packet to the player's client through a {@code NetworkManager} which
     * is where {@code ProtocolLib} controls packets by injecting channels!
     */
    private static final MethodHandle SEND_PACKET;

    static {
        Class<?> entityPlayer = getNMSClass("server.level", "EntityPlayer");
        Class<?> craftPlayer = getCraftClass("entity.CraftPlayer");
        Class<?> playerConnection = getNMSClass("server.network", "PlayerConnection");
        Class<?> playerCommonConnection;
        if (supports(20) && supportsPatch(2)) {
            // The packet send method has been abstracted from ServerGamePacketListenerImpl
            // to ServerCommonPacketListenerImpl in 1.20.2
            playerCommonConnection = getNMSClass("server.network", "ServerCommonPacketListenerImpl");
        } else {
            playerCommonConnection = playerConnection;
        }

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle sendPacket = null, getHandle = null, connection = null;

        try {
            connection = lookup.findGetter(entityPlayer, v(20, "c").v(17, "b").orElse("playerConnection"), playerConnection);
            getHandle = lookup.findVirtual(craftPlayer, "getHandle", MethodType.methodType(entityPlayer));
            sendPacket = lookup.findVirtual(playerCommonConnection, v(20, 2, "b").v(18, "a").orElse("sendPacket"), MethodType.methodType(void.class, getNMSClass("network.protocol", "Packet")));
        } catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
        }

        PLAYER_CONNECTION = connection;
        SEND_PACKET = sendPacket;
        GET_HANDLE = getHandle;
    }

    private ReflectionUtils() {
    }

    /**
     * Gives the {@code handle} object if the server version is equal or greater
     * than the given version.
     * This method is purely for readability and should be always used with
     * {@link VersionHandler#orElse(Object)}.
     *
     * @see #v(int, int, Object)
     * @see VersionHandler#orElse(Object)
     * @since 5.0.0
     */
    public static <T> VersionHandler<T> v(int version, T handle) {
        return new VersionHandler<>(version, handle);
    }

    /**
     * Overload for {@link #v(int, T)} that supports patch versions
     *
     * @since 9.5.0
     */
    public static <T> VersionHandler<T> v(int version, int patch, T handle) {
        return new VersionHandler<>(version, patch, handle);
    }

    public static <T> CallableVersionHandler<T> v(int version, Callable<T> handle) {
        return new CallableVersionHandler<>(version, handle);
    }

    /**
     * Checks whether the server version is equal or greater than the given version.
     *
     * @param minorNumber the version to compare the server version with.
     * @return true if the version is equal or newer, otherwise false.
     * @see #MINOR_NUMBER
     * @since 4.0.0
     */
    public static boolean supports(int minorNumber) {
        return MINOR_NUMBER >= minorNumber;
    }

    /**
     * Checks whether the server version is equal or greater than the given version.
     *
     * @param minorNumber the minor version to compare the server version with.
     * @param patchNumber the patch number to compare the server version with.
     * @return true if the version is equal or newer, otherwise false.
     * @see #MINOR_NUMBER
     * @see #PATCH_NUMBER
     * @since 7.1.0
     */
    public static boolean supports(int minorNumber, int patchNumber) {
        return MINOR_NUMBER == minorNumber ? supportsPatch(patchNumber) : supports(minorNumber);
    }

    /**
     * Checks whether the server version is equal or greater than the given version.
     *
     * @param patchNumber the version to compare the server version with.
     * @return true if the version is equal or newer, otherwise false.
     * @see #PATCH_NUMBER
     * @since 7.0.0
     */
    public static boolean supportsPatch(int patchNumber) {
        return PATCH_NUMBER >= patchNumber;
    }

    /**
     * Get a NMS (net.minecraft.server) class which accepts a package for 1.17
     * compatibility.
     *
     * @param packageName the 1.17+ package name of this class.
     * @param name        the name of the class.
     * @return the NMS class or null if not found.
     * @since 4.0.0
     */
    @Nullable
    public static Class<?> getNMSClass(@Nullable String packageName, @Nonnull String name) {
        if (packageName != null && supports(17)) name = packageName + '.' + name;

        try {
            return Class.forName(NMS_PACKAGE + name);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Get a NMS {@link #NMS_PACKAGE} class.
     *
     * @param name the name of the class.
     * @return the NMS class or null if not found.
     * @since 1.0.0
     */
    @Nullable
    public static Class<?> getNMSClass(@Nonnull String name) {
        return getNMSClass(null, name);
    }

    /**
     * Sends a packet to the player asynchronously if they're online.
     * Packets are thread-safe.
     *
     * @param player  the player to send the packet to.
     * @param packets the packets to send.
     * @return the async thread handling the packet.
     * @see #sendPacketSync(Player, Object...)
     * @since 1.0.0
     */
    @Nonnull
    public static CompletableFuture<Void> sendPacket(@Nonnull Player player, @Nonnull Object... packets) {
        return CompletableFuture.runAsync(() -> sendPacketSync(player, packets)).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    /**
     * Sends a packet to the player synchronously if they're online.
     *
     * @param player  the player to send the packet to.
     * @param packets the packets to send.
     * @see #sendPacket(Player, Object...)
     * @since 2.0.0
     */
    public static void sendPacketSync(@Nonnull Player player, @Nonnull Object... packets) {
        try {
            Object handle = GET_HANDLE.invoke(player);
            Object connection = PLAYER_CONNECTION.invoke(handle);

            // Checking if the connection is not null is enough. There is no need to check
            // if the player is online.
            if (connection != null) {
                for (Object packet : packets) SEND_PACKET.invoke(connection, packet);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Nullable
    public static Object getHandle(@Nonnull Player player) {
        Objects.requireNonNull(player, "Cannot get handle of null player");
        try {
            return GET_HANDLE.invoke(player);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static Object getConnection(@Nonnull Player player) {
        Objects.requireNonNull(player, "Cannot get connection of null player");
        try {
            Object handle = GET_HANDLE.invoke(player);
            return PLAYER_CONNECTION.invoke(handle);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    /**
     * Get a CraftBukkit (org.bukkit.craftbukkit) class.
     *
     * @param name the name of the class to load.
     * @return the CraftBukkit class or null if not found.
     * @since 1.0.0
     */
    @Nullable
    public static Class<?> getCraftClass(@Nonnull String name) {
        try {
            return Class.forName(CRAFTBUKKIT_PACKAGE + name);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @deprecated Use {@link #toArrayClass(Class)} instead.
     */
    @Deprecated
    public static Class<?> getArrayClass(String clazz, boolean nms) {
        clazz = "[L" + (nms ? NMS_PACKAGE : CRAFTBUKKIT_PACKAGE) + clazz + ';';
        try {
            return Class.forName(clazz);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Gives an array version of a class. For example if you wanted
     * {@code EntityPlayer[]} you'd use:
     *
     * <pre>{@code
     * Class EntityPlayer = ReflectionUtils.getNMSClass("...", "EntityPlayer");
     * Class EntityPlayerArray = ReflectionUtils.toArrayClass(EntityPlayer);
     * }</pre>
     *
     * @param clazz the class to get the array version of. You could use for
     *              multi-dimensions arrays too.
     */
    public static Class<?> toArrayClass(Class<?> clazz) {
        try {
            return Class.forName("[L" + clazz.getName() + ';');
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static final class VersionHandler<T> {
        private int version, patch;
        private T handle;

        private VersionHandler(int version, T handle) {
            this(version, 0, handle);
        }

        private VersionHandler(int version, int patch, T handle) {
            if (supports(version) && supportsPatch(patch)) {
                this.version = version;
                this.patch = patch;
                this.handle = handle;
            }
        }

        public VersionHandler<T> v(int version, T handle) {
            return v(version, 0, handle);
        }

        public VersionHandler<T> v(int version, int patch, T handle) {
            if (version == this.version && patch == this.patch)
                throw new IllegalArgumentException("Cannot have duplicate version handles for version: " + version + '.' + patch);
            if (version > this.version && supports(version) && patch >= this.patch && supportsPatch(patch)) {
                this.version = version;
                this.patch = patch;
                this.handle = handle;
            }
            return this;
        }

        /**
         * If none of the previous version checks matched, it'll return this object.
         */
        public T orElse(T handle) {
            return this.version == 0 ? handle : this.handle;
        }
    }

    public static final class CallableVersionHandler<T> {
        private int version;
        private Callable<T> handle;

        private CallableVersionHandler(int version, Callable<T> handle) {
            if (supports(version)) {
                this.version = version;
                this.handle = handle;
            }
        }

        public CallableVersionHandler<T> v(int version, Callable<T> handle) {
            if (version == this.version)
                throw new IllegalArgumentException("Cannot have duplicate version handles for version: " + version);
            if (version > this.version && supports(version)) {
                this.version = version;
                this.handle = handle;
            }
            return this;
        }

        public T orElse(Callable<T> handle) {
            try {
                return (this.version == 0 ? handle : this.handle).call();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}