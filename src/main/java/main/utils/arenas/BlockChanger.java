package main.utils.arenas;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

public class BlockChanger {
    private static final Map<Material, Object> NMS_BLOCK_MATERIALS = new Object2ObjectOpenHashMap<>();
    private static final Map<String, Object> NMS_BLOCK_NAMES = new Object2ObjectOpenHashMap<>();
    private static final MethodHandle WORLD_GET_HANDLE;
    private static final MethodHandle NMS_ITEM_STACK_COPY;
    private static final MethodHandle NMS_BLOCK_FROM_ITEM;
    private static final MethodHandle NMS_BLOCK_NAME;
    private static final MethodHandle NMS_ITEM_STACK_TO_ITEM;
    private static final MethodHandle ITEM_TO_BLOCK_DATA;
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
    private static final BlockPositionConstructor BLOCK_POSITION_CONSTRUCTOR;
    private static final BlockDataRetriever BLOCK_DATA_GETTER;
    private static final TileEntityManager TILE_ENTITY_MANAGER;
    private static final Object AIR_BLOCK_DATA;
    private static final Object NMS_OVERWORLD;
    private static final World OVERWORLD;

    static {
        Class<?> worldServer = ReflectionUtils.getNMSClass("server.level", "WorldServer");
        Class<?> world = ReflectionUtils.getNMSClass("world.level", "World");
        Class<?> craftWorld = ReflectionUtils.getCraftClass("CraftWorld");
        Class<?> craftBlock = ReflectionUtils.getCraftClass("block.CraftBlock");
        Class<?> blockPosition = ReflectionUtils.getNMSClass("core", "BlockPosition");
        Class<?> blocks = ReflectionUtils.getNMSClass("world.level.block", "Blocks");
        Class<?> mutableBlockPosition = ReflectionUtils.getNMSClass("core", "BlockPosition$MutableBlockPosition");
        Class<?> blockData = ReflectionUtils.getNMSClass("world.level.block.state", "IBlockData");
        Class<?> craftItemStack = ReflectionUtils.getCraftClass("inventory.CraftItemStack");
        Class<?> worldItemStack = ReflectionUtils.getNMSClass("world.item", "ItemStack");
        Class<?> item = ReflectionUtils.getNMSClass("world.item", "Item");
        Class<?> block = ReflectionUtils.getNMSClass("world.level.block", "Block");
        Class<?> chunk = ReflectionUtils.getNMSClass("world.level.chunk", "Chunk");
        Class<?> chunkSection = ReflectionUtils.getNMSClass("world.level.chunk", "ChunkSection");
        Class<?> levelHeightAccessor = ReflectionUtils.getNMSClass("world.level.LevelHeightAccessor");
        Class<?> craftBlockEntityState = ReflectionUtils.getCraftClass("block.CraftBlockEntityState");
        MethodHandles.Lookup lookup = MethodHandles.lookup();

        Object airBlockData = null;
        try {
            airBlockData = lookup.findStatic(block, "a", MethodType.methodType(blockData, int.class)).invoke(0);
        } catch (Throwable ignored) {
        }
        AIR_BLOCK_DATA = airBlockData;

        MethodHandle worldGetHandle = null;
        MethodHandle nmsItemStackCopy = null;
        MethodHandle blockFromItem = null;
        MethodHandle blockName = null;
        MethodHandle nmsItemStackToItem = null;
        MethodHandle itemToBlockData = null;
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
        String asBlock = "a";
        String blockGetName = "h";
        String getBlockData = "o";
        String getItem = "c";
        String getChunkAt = "d";
        String chunkSetType = "a";
        String notify = "a";
        String getSections = "d";
        String sectionSetType = "a";
        String setXYZ = "d";
        String getBlockData2 = "getNMS";
        String removeTileEntity = "n";
        MethodType notifyMethodType = MethodType.methodType(void.class, blockPosition, blockData, blockData, int.class);
        MethodType chunkSetTypeMethodType = MethodType.methodType(blockData, blockPosition, blockData, boolean.class);
        MethodType chunkSectionSetTypeMethodType = MethodType.methodType(blockData, int.class, int.class, int.class, blockData);
        MethodType removeTileEntityMethodType = MethodType.methodType(void.class, blockPosition);
        BlockPositionConstructor blockPositionConstructor = null;
        try {
            worldGetHandle = lookup.findVirtual(craftWorld, "getHandle", MethodType.methodType(worldServer));
            worldGetChunk = lookup.findVirtual(worldServer, getChunkAt, MethodType.methodType(chunk, int.class, int.class));
            nmsItemStackCopy = lookup.findStatic(craftItemStack, "asNMSCopy", MethodType.methodType(worldItemStack, ItemStack.class));
            blockFromItem = lookup.findStatic(block, asBlock, MethodType.methodType(block, item));
            blockName = lookup.findVirtual(block, blockGetName, MethodType.methodType(String.class));
            mutableBlockPositionXYZ = lookup.findConstructor(mutableBlockPosition, MethodType.methodType(void.class, int.class, int.class, int.class));
            itemToBlockData = lookup.findVirtual(block, getBlockData, MethodType.methodType(blockData));
            mutableBlockPositionSet = lookup.findVirtual(mutableBlockPosition, setXYZ, MethodType.methodType(mutableBlockPosition, int.class, int.class, int.class));
            blockPositionConstructor = new BlockPositionNormal(mutableBlockPositionXYZ, mutableBlockPositionSet);
            nmsItemStackToItem = lookup.findVirtual(worldItemStack, getItem, MethodType.methodType(item));
            chunkSetTypeM = lookup.findVirtual(chunk, chunkSetType, chunkSetTypeMethodType);
            blockNotify = lookup.findVirtual(worldServer, notify, notifyMethodType);
            chunkGetSections = lookup.findVirtual(chunk, getSections, MethodType.methodType(ReflectionUtils.toArrayClass(chunkSection)));
            chunkSectionSetType = lookup.findVirtual(chunkSection, sectionSetType, chunkSectionSetTypeMethodType);
            getLevelHeightAccessor = lookup.findVirtual(chunk, "z", MethodType.methodType(levelHeightAccessor));
            getSectionIndex = lookup.findVirtual(levelHeightAccessor, "e", MethodType.methodType(int.class, int.class));
            nmsBlockGetBlockData = lookup.findVirtual(craftBlock, getBlockData2, MethodType.methodType(blockData));
            worldRemoveTileEntity = lookup.findVirtual(world, removeTileEntity, removeTileEntityMethodType);
            worldCapturedTileEntities = lookup.findGetter(world, "capturedTileEntities", Map.class);
            capturedTileEntitiesContainsKey = lookup.findVirtual(Map.class, "containsKey", MethodType.methodType(boolean.class, Object.class));
            Method getTileEntityMethod = craftBlockEntityState.getDeclaredMethod("getTileEntity");
            Method getSnapshotMethod = craftBlockEntityState.getDeclaredMethod("getSnapshot");
            getTileEntityMethod.setAccessible(true);
            getSnapshotMethod.setAccessible(true);
        } catch (NoSuchMethodException | IllegalAccessException | NoSuchFieldException ignored) {
        }
        WORLD_GET_HANDLE = worldGetHandle;
        WORLD_GET_CHUNK = worldGetChunk;
        NMS_ITEM_STACK_COPY = nmsItemStackCopy;
        NMS_BLOCK_FROM_ITEM = blockFromItem;
        NMS_BLOCK_NAME = blockName;
        NMS_ITEM_STACK_TO_ITEM = nmsItemStackToItem;
        ITEM_TO_BLOCK_DATA = itemToBlockData;
        CHUNK_SET_TYPE = chunkSetTypeM;
        BLOCK_NOTIFY = blockNotify;
        CHUNK_GET_SECTIONS = chunkGetSections;
        CHUNK_SECTION_SET_TYPE = chunkSectionSetType;
        GET_LEVEL_HEIGHT_ACCESSOR = getLevelHeightAccessor;
        GET_SECTION_INDEX = getSectionIndex;
        BLOCK_POSITION_CONSTRUCTOR = blockPositionConstructor;
        CRAFT_BLOCK_GET_NMS_BLOCK = craftBlockGetNMSBlock;
        NMS_BLOCK_GET_BLOCK_DATA = nmsBlockGetBlockData;
        WORLD_REMOVE_TILE_ENTITY = worldRemoveTileEntity;
        WORLD_CAPTURED_TILE_ENTITIES = worldCapturedTileEntities;
        IS_TILE_ENTITY = capturedTileEntitiesContainsKey;
        BLOCK_DATA_GETTER = new BlockDataGetter();
        BLOCK_UPDATER = new BlockUpdaterLatest(BLOCK_NOTIFY, CHUNK_SET_TYPE, GET_SECTION_INDEX, GET_LEVEL_HEIGHT_ACCESSOR);
        TILE_ENTITY_MANAGER = new TileEntityManagerSupported();
        Arrays.stream(Material.values()).filter(Material::isBlock).forEach(BlockChanger::addNMSBlockData);
        NMS_BLOCK_MATERIALS.put(Material.AIR, AIR_BLOCK_DATA);
        Arrays.stream(blocks.getDeclaredFields()).filter(field -> field.getType() == block).map(field -> {
            try {
                return field.get(block);
            } catch (IllegalArgumentException | IllegalAccessException ignored) {
            }
            return null;
        }).forEach(nmsBlock -> {
            try {
                String name = (String) NMS_BLOCK_NAME.invoke(nmsBlock);
                name = name.substring(name.lastIndexOf(".") + 1).toUpperCase();
                NMS_BLOCK_NAMES.put(name, nmsBlock);
            } catch (Throwable ignored) {
            }
        });
        OVERWORLD = Bukkit.getWorld("world");
        NMS_OVERWORLD = getNMSWorld();
    }

    private static void addNMSBlockData(Material material) {
        ItemStack itemStack = new ItemStack(material);
        Object nmsData = getNMSBlockData(itemStack);
        if (nmsData != null) NMS_BLOCK_MATERIALS.put(material, nmsData);
    }

    public static void setChunkBlockAsynchronously(Location location, Object blockData, boolean physics) {
        Object blockPosition = newMutableBlockPosition(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        new ChunkSetWorkload(NMS_OVERWORLD, blockPosition, blockData, location, physics).compute();
    }

    public static void setChunkBlockAsynchronouslyUpdate(Location location, Object blockData, boolean physics) {
        Object blockPosition = newMutableBlockPosition(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        new ChunkSetWorkload(NMS_OVERWORLD, blockPosition, blockData, location, physics).updateCompute();
    }

    public static void setSectionBlockAsynchronously(Location location, Object blockData, boolean physics) {
        Object blockPosition = newMutableBlockPosition(OVERWORLD, 0, 0, 0);
        new SectionSetWorkload(NMS_OVERWORLD, blockPosition, blockData, location, physics);
    }

    private static Object[] getSections(Object nmsChunk) {
        try {
            return (Object[]) CHUNK_GET_SECTIONS.invoke(nmsChunk);
        } catch (Throwable ignored) {
        }
        return null;
    }

    private static void setTypeChunkSection(Object chunkSection, int x, int y, int z, Object blockData) {
        try {
            CHUNK_SECTION_SET_TYPE.invoke(chunkSection, x, y, z, blockData);
        } catch (Throwable ignored) {
        }
    }

    private static void setType(Object chunk, Object blockPosition, Object blockData, boolean physics) {
        BLOCK_UPDATER.setType(chunk, blockPosition, blockData, physics);
    }

    private static Object getChunkAt(Object world, int x, int z) {
        try {
            return WORLD_GET_CHUNK.invoke(world, x >> 4, z >> 4);
        } catch (Throwable ignored) {
        }
        return null;
    }

    private static Object getNMSWorld() {
        try {
            return WORLD_GET_HANDLE.invoke(BlockChanger.OVERWORLD);
        } catch (Throwable ignored) {
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
            return ITEM_TO_BLOCK_DATA.invoke(block);
        } catch (Throwable ignored) {
        }
        return null;
    }

    private static boolean isTileEntity(Object nmsWorld, Object blockPosition) {
        return TILE_ENTITY_MANAGER.isTileEntity(nmsWorld, blockPosition);
    }

    private static void removeIfTileEntity(Object nmsWorld, Object blockPosition) {
        if (isTileEntity(nmsWorld, blockPosition)) TILE_ENTITY_MANAGER.destroyTileEntity(nmsWorld, blockPosition);
    }

    public static void updateBlock(Object world, Object blockPosition, Object blockData, boolean physics) {
        BLOCK_UPDATER.update(world, blockPosition, blockData, physics ? 3 : 2);
    }

    public static Object newMutableBlockPosition(@Nullable Object world, Object x, Object y, Object z) {
        try {
            return BLOCK_POSITION_CONSTRUCTOR.newMutableBlockPosition(world, x, y, z);
        } catch (Throwable ignored) {
        }
        return null;
    }

    public static void setBlockPosition(Object mutableBlockPosition, Object x, Object y, Object z) {
        try {
            BLOCK_POSITION_CONSTRUCTOR.set(mutableBlockPosition, x, y, z);
        } catch (Throwable ignored) {
        }
    }

    public static @Nonnull Object getBlockData(@Nonnull ItemStack itemStack) {
        Object blockData = BLOCK_DATA_GETTER.fromItemStack(itemStack);
        if (blockData == null) throw new IllegalArgumentException("Couldn't convert specified itemstack to block data");
        return blockData;
    }

    public static @Nullable Object getBlockData(@Nullable Material material) {
        return NMS_BLOCK_MATERIALS.get(material);
    }

    public static @Nonnull Object getBlockData(Block block) {
        Object blockData = BLOCK_DATA_GETTER.fromBlock(block);
        return blockData != null ? blockData : AIR_BLOCK_DATA;
    }

    private interface TileEntityManager {
        default Object getCapturedTileEntities(Object nmsWorld) {
            try {
                return WORLD_CAPTURED_TILE_ENTITIES.invoke(nmsWorld);
            } catch (Throwable ignored) {
            }
            return null;
        }

        default boolean isTileEntity(Object nmsWorld, Object blockPosition) {
            try {
                return (boolean) IS_TILE_ENTITY.invoke(getCapturedTileEntities(nmsWorld), blockPosition);
            } catch (Throwable ignored) {
            }
            return false;
        }

        default void destroyTileEntity(Object nmsWorld, Object blockPosition) {
            try {
                WORLD_REMOVE_TILE_ENTITY.invoke(nmsWorld, blockPosition);
            } catch (Throwable ignored) {
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

        default Object fromBlock(Block block) {
            try {
                Object nmsBlock = CRAFT_BLOCK_GET_NMS_BLOCK.invoke(block);
                return NMS_BLOCK_GET_BLOCK_DATA.invoke(nmsBlock);
            } catch (Throwable ignored) {
            }
            return null;
        }

        Object fromItemStack(ItemStack itemStack);
    }

    private interface Workload {
        void compute();

        void updateCompute();
    }

    interface BlockPositionConstructor {
        Object newMutableBlockPosition(Object world, Object x, Object y, Object z);

        Object set(Object mutableBlockPosition, Object x, Object y, Object z);
    }

    interface BlockUpdater {
        void setType(Object chunk, Object blockPosition, Object blockData, boolean physics);

        void update(Object world, Object blockPosition, Object blockData, int physics);

        Object getSection(Object nmsChunk, Object[] sections, int y);

        int getSectionIndex(Object nmsChunk, int y);
    }

    private static class TileEntityManagerSupported implements TileEntityManager {
    }

    private static class BlockDataGetter implements BlockDataRetriever {
        @Override
        public Object fromItemStack(ItemStack itemStack) {
            try {
                Object nmsItem = getNMSItem(itemStack);
                Object block = nmsItem != null ? NMS_BLOCK_FROM_ITEM.invoke(nmsItem) : NMS_BLOCK_NAMES.get(itemStack.getType().name());
                return ITEM_TO_BLOCK_DATA.invoke(block);
            } catch (Throwable ignored) {
            }
            return null;
        }

        @Override
        public Object fromBlock(Block block) {
            try {
                return NMS_BLOCK_GET_BLOCK_DATA.invoke(block);
            } catch (Throwable ignored) {
            }
            return null;
        }
    }

    private static class ChunkSetWorkload implements Workload {
        private final Object nmsWorld;
        private final Object blockPosition;
        private final Object blockData;
        private final Location location;
        private final boolean physics;

        public ChunkSetWorkload(Object nmsWorld, Object blockPosition, Object blockData, Location location, boolean physics) {
            this.nmsWorld = nmsWorld;
            this.blockPosition = blockPosition;
            this.blockData = blockData;
            this.location = location;
            this.physics = physics;
        }

        @Override
        public void compute() {
            BlockChanger.setBlockPosition(blockPosition, location.getBlockX(), location.getBlockY(), location.getBlockZ());
            Object chunk = BlockChanger.getChunkAt(nmsWorld, location.getBlockX(), location.getBlockZ());
            BlockChanger.removeIfTileEntity(nmsWorld, blockPosition);
            BlockChanger.setType(chunk, blockPosition, blockData, physics);
        }

        @Override
        public void updateCompute() {
            BlockChanger.setBlockPosition(blockPosition, location.getBlockX(), location.getBlockY(), location.getBlockZ());
            Object chunk = BlockChanger.getChunkAt(nmsWorld, location.getBlockX(), location.getBlockZ());
            BlockChanger.removeIfTileEntity(nmsWorld, blockPosition);
            BlockChanger.setType(chunk, blockPosition, blockData, physics);
            BlockChanger.updateBlock(nmsWorld, blockPosition, blockData, physics);
        }
    }

    private static class SectionSetWorkload implements Workload {
        private final Object nmsWorld;
        private final Object blockPosition;
        private final Object blockData;
        private final Location location;
        private final boolean physics;

        public SectionSetWorkload(Object nmsWorld, Object blockPosition, Object blockData, Location location, boolean physics) {
            this.nmsWorld = nmsWorld;
            this.blockPosition = blockPosition;
            this.blockData = blockData;
            this.location = location;
            this.physics = physics;
        }

        @Override
        public void compute() {
            BlockChanger.setBlockPosition(blockPosition, location.getBlockX(), location.getBlockY(), location.getBlockZ());
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();
            Object nmsChunk = BlockChanger.getChunkAt(nmsWorld, x, z);
            int j = x & 15;
            int k = y & 15;
            int l = z & 15;
            Object[] sections = BlockChanger.getSections(nmsChunk);
            Object section = BLOCK_UPDATER.getSection(nmsChunk, sections, y);
            BlockChanger.removeIfTileEntity(nmsWorld, blockPosition);
            BlockChanger.setTypeChunkSection(section, j, k, l, blockData);
        }

        @Override
        public void updateCompute() {
            BlockChanger.setBlockPosition(blockPosition, location.getBlockX(), location.getBlockY(), location.getBlockZ());
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();
            Object nmsChunk = BlockChanger.getChunkAt(nmsWorld, x, z);
            int j = x & 15;
            int k = y & 15;
            int l = z & 15;
            Object[] sections = BlockChanger.getSections(nmsChunk);
            Object section = BLOCK_UPDATER.getSection(nmsChunk, sections, y);
            BlockChanger.removeIfTileEntity(nmsWorld, blockPosition);
            BlockChanger.setTypeChunkSection(section, j, k, l, blockData);
            BlockChanger.updateBlock(nmsWorld, blockPosition, blockData, physics);
        }
    }

    static class BlockPositionNormal implements BlockPositionConstructor {
        private final MethodHandle mutableBlockPositionConstructor;
        private final MethodHandle mutableBlockPositionSet;

        public BlockPositionNormal(MethodHandle mutableBlockPositionXYZ, MethodHandle mutableBlockPositionSet) {
            this.mutableBlockPositionConstructor = mutableBlockPositionXYZ;
            this.mutableBlockPositionSet = mutableBlockPositionSet;
        }

        @Override
        public Object newMutableBlockPosition(Object world, Object x, Object y, Object z) {
            try {
                return mutableBlockPositionConstructor.invoke(x, y, z);
            } catch (Throwable ignored) {
            }
            return null;
        }

        @Override
        public Object set(Object mutableBlockPosition, Object x, Object y, Object z) {
            try {
                return mutableBlockPositionSet.invoke(mutableBlockPosition, x, y, z);
            } catch (Throwable ignored) {
            }
            return null;
        }
    }

    static class BlockUpdaterLatest implements BlockUpdater {
        private final MethodHandle blockNotify;
        private final MethodHandle chunkSetType;
        private final MethodHandle sectionIndexGetter;
        private final MethodHandle levelHeightAccessorGetter;

        public BlockUpdaterLatest(MethodHandle blockNotify, MethodHandle chunkSetType, MethodHandle sectionIndexGetter, MethodHandle levelHeightAccessorGetter) {
            this.blockNotify = blockNotify;
            this.chunkSetType = chunkSetType;
            this.sectionIndexGetter = sectionIndexGetter;
            this.levelHeightAccessorGetter = levelHeightAccessorGetter;
        }

        @Override
        public void update(Object world, Object blockPosition, Object blockData, int physics) {
            try {
                blockNotify.invoke(world, blockPosition, blockData, blockData, physics);
            } catch (Throwable ignored) {
            }
        }

        @Override
        public void setType(Object chunk, Object blockPosition, Object blockData, boolean physics) {
            try {
                chunkSetType.invoke(chunk, blockPosition, blockData, physics);
            } catch (Throwable ignored) {
            }
        }

        @Override
        public Object getSection(Object nmsChunk, Object[] sections, int y) {
            return sections[getSectionIndex(nmsChunk, y)];
        }

        public Object getLevelHeightAccessor(Object nmsChunk) {
            try {
                return levelHeightAccessorGetter.invoke(nmsChunk);
            } catch (Throwable ignored) {
            }
            return null;
        }

        @Override
        public int getSectionIndex(Object nmsChunk, int y) {
            Object levelHeightAccessor = getLevelHeightAccessor(nmsChunk);
            try {
                return (int) sectionIndexGetter.invoke(levelHeightAccessor, y);
            } catch (Throwable ignored) {
            }
            return -1;
        }
    }

    static final class ReflectionUtils {
        public static final String NMS_VERSION;

        static {
            String found = null;
            for (Package pack : Package.getPackages()) {
                String name = pack.getName();
                if (name.startsWith("org.bukkit.craftbukkit.v")) {
                    found = pack.getName().split("\\.")[3];
                    try {
                        Class.forName("org.bukkit.craftbukkit." + found + ".entity.CraftPlayer");
                        break;
                    } catch (ClassNotFoundException e) {
                        found = null;
                    }
                }
            }
            if (found == null) throw new IllegalArgumentException(
                    "Failed to parse server version. Could not find any package starting with name: 'org.bukkit.craftbukkit.v'");
            NMS_VERSION = found;
        }
        public static final String CRAFTBUKKIT_PACKAGE = "org.bukkit.craftbukkit." + NMS_VERSION + '.', NMS_PACKAGE = v(17, "net.minecraft.").orElse("net.minecraft.server.v_1_19_R3" + '.');

        public static <T> VersionHandler<T> v(int version, T handle) {
            return new VersionHandler<>(version, handle);
        }

        public static @NotNull Class<?> getNMSClass(@Nullable String packageName, @Nonnull String name) {
            if (packageName != null) name = packageName + '.' + name;
            try {
                return Class.forName(NMS_PACKAGE + name);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }

        public static @NotNull Class<?> getNMSClass(@Nonnull String name) {
            return getNMSClass(null, name);
        }

        public static @NotNull Class<?> getCraftClass(@Nonnull String name) {
            try {
                return Class.forName(CRAFTBUKKIT_PACKAGE + name);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }

        public static Class<?> toArrayClass(Class<?> clazz) {
            try {
                return Class.forName("[L" + clazz.getName() + ';');
            } catch (ClassNotFoundException e) {
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
                this.version = version;
                this.patch = patch;
                this.handle = handle;
            }

            public VersionHandler<T> v(int version, T handle) {
                return v(version, 0, handle);
            }

            public VersionHandler<T> v(int version, int patch, T handle) {
                if (version == this.version && patch == this.patch)
                    throw new IllegalArgumentException("Cannot have duplicate version handles for version: " + version + '.' + patch);
                if (version > this.version && patch >= this.patch) {
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
    }
}
