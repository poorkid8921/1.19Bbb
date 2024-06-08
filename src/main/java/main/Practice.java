package main;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import main.commands.FastCrystals;
import main.commands.essentials.List;
import main.commands.essentials.*;
import main.commands.tpa.*;
import main.commands.warps.*;
import main.utils.Gui;
import main.utils.Initializer;
import main.utils.Instances.CommandHolder;
import main.utils.TeleportCompleter;
import main.utils.modules.anticheat.AutoTotem;
import main.utils.modules.arenas.Utils;
import main.utils.modules.kits.ClaimCommand;
import main.utils.modules.kits.KitCommand;
import main.utils.modules.kits.storage.KitRoomFile;
import main.utils.modules.kits.storage.KitsFile;
import main.utils.modules.npcs.InteractAtNPC;
import main.utils.modules.optimizer.InteractionListeners;
import main.utils.modules.optimizer.LastPacketEvent;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.io.File;
import java.util.Map;

import static main.utils.Initializer.*;
import static main.utils.modules.arenas.Utils.*;

public class Practice extends JavaPlugin {
    public static final Point point = new Point(0, 0);
    public static File dataFolder;
    public static World d;
    public static World d0;
    public static Map<String, Object2ObjectOpenHashMap<String, Object>> kitMap = new Object2ObjectOpenHashMap<>();
    public static Map<Integer, ItemStack[]> kitRoomMap = new Int2ObjectOpenHashMap<>();
    public static ObjectArrayList<String> editorChecker = ObjectArrayList.of();
    public static ObjectArrayList<String> menuChecker = ObjectArrayList.of();
    public static Map<String, Integer> publicChecker = new Object2IntOpenHashMap<>();
    public static Map<String, Integer> roomChecker = new Object2IntOpenHashMap<>();
    short flatstr = 1;
    short ticked = 0;

    public static void restoreKitMap() {
        try {
            for (final String key : KitsFile.get().getConfigurationSection("data").getKeys(false)) {
                kitMap.put(key, new Object2ObjectOpenHashMap<>());
                for (final String key2 : KitsFile.get().getConfigurationSection("data." + key).getKeys(false)) {
                    switch (key2) {
                        case "items" ->
                                kitMap.get(key).put(key2, ((java.util.List) KitsFile.get().get("data." + key + "." + key2)).toArray(new ItemStack[0]));
                        case "player", "UUID", "public" ->
                                kitMap.get(key).put(key2, KitsFile.get().get("data." + key + "." + key2).toString());
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    public static void restoreKitRoom() {
        for (int i = 1; i <= 5; i = i + 1) {
            try {
                kitRoomMap.put(i, (ItemStack[]) ((java.util.List) KitRoomFile.get().get("data." + i)).toArray(new ItemStack[0]));
            } catch (Exception exception) {
                kitRoomMap.put(i, new ItemStack[45]);
            }
        }
    }

    private Location getRandomLoc(World world, Location[] RTP) {
        Location location = null;
        int boundX, boundZ;
        Block block;
        while (location == null) {
            boundX = Initializer.RANDOM.nextInt(-10000, 10000);
            boundZ = Initializer.RANDOM.nextInt(-10000, 10000);
            for (final Location k : RTP) {
                if (k == null) break;
                point.setLocation(k.getBlockX(), k.getBlockZ());
                if (point.distance(boundX, boundZ) < 2000) break;
            }
            block = world.getHighestBlockAt(boundX, boundZ);
            if (block.isSolid()) location = new Location(world, boundX, block.getY() + 1, boundZ);
        }
        return location;
    }

    public void saveKitMap() {
        KitsFile.get().set("data", null);
        if (!kitMap.isEmpty()) {
            for (final Map.Entry<String, Object2ObjectOpenHashMap<String, Object>> stringObject2ObjectOpenHashMapEntry : kitMap.entrySet()) {
                for (final Map.Entry<String, Object> data : stringObject2ObjectOpenHashMapEntry.getValue().entrySet())
                    KitsFile.get().set("data." + stringObject2ObjectOpenHashMapEntry.getKey() + "." + data.getKey(), data.getValue());
            }
        }
        KitsFile.save();
    }

    public void saveKitRoom() {
        if (!kitRoomMap.isEmpty()) {
            for (final Map.Entry<Integer, ItemStack[]> integerEntry : kitRoomMap.entrySet())
                KitRoomFile.get().set("data." + integerEntry.getKey().toString(), integerEntry.getValue());
        }
        KitRoomFile.save();
    }

    private void registerCommands() {
        final ClaimCommand claimCommand = new ClaimCommand();
        final CommandHolder[] ignored = {new CommandHolder("report", new Report()), new CommandHolder("killeffect", new Killeffect()), new CommandHolder("discord", new Discord()), new CommandHolder("back", new Back()), new CommandHolder("tpa", new Tpa()), new CommandHolder("tpaccept", new Tpaccept()), new CommandHolder("tpahere", new Tpahere()), new CommandHolder("tpdeny", new TpDeny()), new CommandHolder("msg", new Msg()), new CommandHolder("reply", new Reply()), new CommandHolder("msglock", new MsgLock()), new CommandHolder("tpalock", new TpaLock()), new CommandHolder("irename", new ItemRename()), new CommandHolder("clear", new Clear()), new CommandHolder("spawn", new Spawn()), new CommandHolder("ffa", new Ffa()), new CommandHolder("flat", new Flat()), new CommandHolder("warp", new Warp()), new CommandHolder("setwarp", new SetWarp()), new CommandHolder("gmc", new GMc()), new CommandHolder("gms", new GMs()), new CommandHolder("gmsp", new GMsp()), new CommandHolder("playtime", new PlayTime()), new CommandHolder("list", new List()), new CommandHolder("unban", new Unban()), new CommandHolder("ban", new Ban()), new CommandHolder("tp", new Teleport()), new CommandHolder("tphere", new TeleportHere()), new CommandHolder("tpall", new TeleportAll()), new CommandHolder("setrank", new SetRank()), new CommandHolder("broadcast", new Broadcast()), new CommandHolder("banip", new BanIP()), new CommandHolder("fastcrystals", new FastCrystals()), new CommandHolder("kit", new KitCommand()), new CommandHolder("kit1", claimCommand), new CommandHolder("kit2", claimCommand), new CommandHolder("kit3", claimCommand)};
        final TeleportCompleter tabCompleter = new TeleportCompleter();
        this.getCommand("tpa").setTabCompleter(tabCompleter);
        this.getCommand("tpaccept").setTabCompleter(tabCompleter);
        this.getCommand("tpahere").setTabCompleter(tabCompleter);
        this.getCommand("tpdeny").setTabCompleter(tabCompleter);
    }

    private void registerPacketListeners() {
        PacketEvents.getAPI().getEventManager().registerListeners(new InteractionListeners(), new LastPacketEvent(), new AutoTotem(), new InteractAtNPC());
        PacketEvents.getAPI().init();
    }

    @Override
    public void onLoad() {
        p = this;
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings().checkForUpdates(false).reEncodeByDefault(false);
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        dataFolder = getDataFolder();
        d = Bukkit.getWorld("world");
        d0 = Bukkit.getWorld("world_the_end");
        nmsOverworld = ((CraftWorld) d).getHandle();
        chunkSource = nmsOverworld.getChunkSource();
        Utils.lightEngine = chunkSource.getLightEngine();

        KitsFile.setup();
        KitRoomFile.setup();
        if (KitsFile.get().contains("data")) restoreKitMap();
        restoreKitRoom();

        registerCommands();
        Gui.init();
        registerPacketListeners();
        Initializer.init();
        Bukkit.getScheduler().runTaskLater(this, () -> {
            for (short i = 0; i < 101; i++) {
                if (i == 100) {
                    point.setLocation(0, 0);
                    getCommand("rtp").setExecutor(new RTP());
                    Bukkit.getScheduler().scheduleSyncRepeatingTask(p, () -> {
                        for (final Entity entity : d.getEntities()) {
                            if (!(entity instanceof EnderPearl) && !(entity instanceof Player)) entity.remove();
                        }
                        ticked++;
                        setCuboid(92, 115, 458, -98, 117, 268, Blocks.AIR, Blocks.AIR.defaultBlockState());
                        if (ticked == 2) {
                            switch (flatstr++) {
                                case 1 -> {
                                    setArea(114, -98, 458, 92, 268, Blocks.NETHERITE_BLOCK.defaultBlockState());
                                }
                                case 2 -> {
                                    setArea(114, -98, 458, 92, 268, new BlockState[]{Blocks.STRIPPED_DARK_OAK_WOOD.defaultBlockState(), Blocks.STRIPPED_OAK_WOOD.defaultBlockState(), Blocks.STRIPPED_BIRCH_WOOD.defaultBlockState(), Blocks.STRIPPED_SPRUCE_WOOD.defaultBlockState()}, 4);
                                }
                                case 3 -> {
                                    setArea(114, -98, 458, 92, 268, new BlockState[]{Blocks.STONE.defaultBlockState(), Blocks.MOSSY_COBBLESTONE.defaultBlockState(), Blocks.MOSS_BLOCK.defaultBlockState(), Blocks.GRASS_BLOCK.defaultBlockState()}, 4);
                                }
                                case 4 -> {
                                    setArea(114, -98, 458, 92, 268, new BlockState[]{Blocks.COBBLESTONE.defaultBlockState(), Blocks.STONE.defaultBlockState(), Blocks.STONE_BRICKS.defaultBlockState(), Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), Blocks.ANDESITE.defaultBlockState()}, 5);
                                }
                                case 5 -> {
                                    setArea(114, -98, 458, 92, 268, new BlockState[]{Blocks.WHITE_CONCRETE.defaultBlockState(), Blocks.SMOOTH_QUARTZ.defaultBlockState(), Blocks.WHITE_CONCRETE_POWDER.defaultBlockState()}, 3);
                                    flatstr = 1;
                                }
                            }
                            final String UNBAN_MSG = "§7You are now unbanned from " + SECOND_COLOR + "/flat!";
                            for (final String k : bannedFromflat) {
                                final Player z = Bukkit.getPlayer(k);
                                if (z != null) z.sendMessage(UNBAN_MSG);
                            }
                            bannedFromflat.clear();
                        } else if (ticked == 4) {
                            ticked = 0;
                            setCuboid(-119, 96, -300, 5, 94, -176, Blocks.AIR, Blocks.AIR.defaultBlockState());
                            setArea(-119, 93, -300, 5, -176, Blocks.GRASS_BLOCK.defaultBlockState());
                            setCuboid(-119, 92, -300, 5, 89, -176, Blocks.DIRT, Blocks.DIRT.defaultBlockState());
                            setCuboid(-119, 88, -300, 5, -63, -176, Blocks.STONE, Blocks.STONE.defaultBlockState());
                            Location location;
                            for (final Player k : inFFA) {
                                location = k.getLocation();
                                location.setY(94);
                                k.teleport(location);
                            }
                        }
                    }, 0L, 6000L);
                    Bukkit.getLogger().warning("Finished RTP population.");
                    return;
                }
                final short finalI = i;
                Bukkit.getScheduler().runTaskLater(this, () -> {
                    overworldRTP[finalI] = getRandomLoc(d, overworldRTP);
                    endRTP[finalI] = getRandomLoc(d0, endRTP);
                }, 1L);
            }
        }, 100L);
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
        this.saveKitMap();
        this.saveKitRoom();
    }
}