package main;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import main.ac.GrimAPI;
import main.ac.events.*;
import main.ac.events.worldreader.PacketWorldReaderEighteen;
import main.ac.player.GrimPlayer;
import main.commands.FastCrystals;
import main.commands.essentials.List;
import main.commands.essentials.*;
import main.commands.tpa.*;
import main.commands.warps.*;
import main.utils.*;
import main.utils.kits.ClaimCommand;
import main.utils.kits.KitCommand;
import main.utils.kits.storage.KitRoomFile;
import main.utils.kits.storage.KitsFile;
import main.utils.npcs.InteractAtNPC;
import main.utils.optimizer.InteractionListeners;
import main.utils.optimizer.LastPacketEvent;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static main.utils.Initializer.*;
import static main.utils.Utils.chunkSource;
import static main.utils.Utils.nmsOverworld;

public class Practice extends JavaPlugin {
    private static final Point point = new Point(0, 0);
    public static File dataFolder;
    public static World d;
    public static World d0;
    public static Map<String, Object2ObjectOpenHashMap<String, Object>> kitMap = new Object2ObjectOpenHashMap<>();
    public static Map<Integer, ItemStack[]> kitRoomMap = new Int2ObjectOpenHashMap<>();
    public static ObjectArrayList<String> editorChecker = ObjectArrayList.of();
    public static ObjectArrayList<String> menuChecker = ObjectArrayList.of();
    public static Map<String, Integer> publicChecker = new Object2IntOpenHashMap<>();
    public static Map<String, Integer> roomChecker = new Object2IntOpenHashMap<>();
    int flatstr = 1;
    int ticked = 0;

    public static void restoreKitMap() {
        try {
            for (String key : KitsFile.get().getConfigurationSection("data").getKeys(false)) {
                kitMap.put(key, new Object2ObjectOpenHashMap<>());
                for (String key2 : KitsFile.get().getConfigurationSection("data." + key).getKeys(false)) {
                    if (key2.equals("items")) {
                        ItemStack[] items = (ItemStack[]) ((java.util.List) KitsFile.get().get("data." + key + "." + key2)).toArray(new ItemStack[0]);
                        kitMap.get(key).put(key2, items);
                    } else switch (key2) {
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
                ItemStack[] content = (ItemStack[]) ((java.util.List) KitRoomFile.get().get("data." + i)).toArray(new ItemStack[0]);
                kitRoomMap.put(i, content);
            } catch (Exception var3) {
                kitRoomMap.put(i, new ItemStack[45]);
            }
        }
    }

    private Location getRandomLoc(World w,  Location[] RTP) {
        Location loc = null;
        while (loc == null) {
            int boundX = Initializer.RANDOM.nextInt(-10000, 10000);
            int boundZ = Initializer.RANDOM.nextInt(-10000, 10000);
            for (Location loc1 : RTP) {
                if (loc1 == null)
                    break;
                point.setLocation(loc1.getBlockX(), loc1.getBlockZ());
                if (point.distance(boundX, boundZ) < 1000)
                    break;
            }
            Block b = w.getHighestBlockAt(boundX, boundZ);
            if (b.isSolid())
                loc = new Location(w, boundX, b.getY() + 1, boundZ);
        }
        return loc;
    }

    public void saveKitMap() {
        KitsFile.get().set("data", null);
        if (!kitMap.isEmpty()) {
            for (Map.Entry<String, Object2ObjectOpenHashMap<String, Object>> stringObject2ObjectOpenHashMapEntry : kitMap.entrySet()) {
                for (Map.Entry<String, Object> data : stringObject2ObjectOpenHashMapEntry.getValue().entrySet())
                    KitsFile.get().set("data." + stringObject2ObjectOpenHashMapEntry.getKey() + "." + data.getKey(), data.getValue());
            }
        }
        KitsFile.save();
    }

    public void saveKitRoom() {
        if (!kitRoomMap.isEmpty()) {
            for (Map.Entry<Integer, ItemStack[]> integerEntry : kitRoomMap.entrySet())
                KitRoomFile.get().set("data." + integerEntry.getKey().toString(), integerEntry.getValue());
        }
        KitRoomFile.save();
    }

    private void registerCommands() {
        this.getCommand("report").setExecutor(new Report());
        this.getCommand("killeffect").setExecutor(new Killeffect());
        this.getCommand("discord").setExecutor(new Discord());
        this.getCommand("back").setExecutor(new Back());
        this.getCommand("help").setExecutor(new Help());
        this.getCommand("tpa").setExecutor(new Tpa());
        this.getCommand("tpaccept").setExecutor(new Tpaccept());
        this.getCommand("tpahere").setExecutor(new Tpahere());
        this.getCommand("tpdeny").setExecutor(new Tpdeny());
        this.getCommand("msg").setExecutor(new Msg());
        this.getCommand("reply").setExecutor(new Reply());
        this.getCommand("msglock").setExecutor(new MsgLock());
        this.getCommand("tpalock").setExecutor(new TpaLock());
        this.getCommand("irename").setExecutor(new ItemRename());
        this.getCommand("clear").setExecutor(new Clear());
        this.getCommand("spawn").setExecutor(new Spawn());
        this.getCommand("ffa").setExecutor(new Ffa());
        this.getCommand("flat").setExecutor(new Flat());
        this.getCommand("warp").setExecutor(new Warp());
        this.getCommand("setwarp").setExecutor(new SetWarp());
        this.getCommand("gmc").setExecutor(new GMc());
        this.getCommand("gms").setExecutor(new GMs());
        this.getCommand("gmsp").setExecutor(new GMsp());
        this.getCommand("playtime").setExecutor(new PlayTime());
        this.getCommand("list").setExecutor(new List());
        this.getCommand("unban").setExecutor(new Unban());
        this.getCommand("ban").setExecutor(new Ban());
        this.getCommand("tp").setExecutor(new Teleport());
        this.getCommand("tphere").setExecutor(new TeleportHere());
        this.getCommand("tpall").setExecutor(new TeleportAll());
        this.getCommand("setrank").setExecutor(new SetRank());
        this.getCommand("broadcast").setExecutor(new Broadcast());
        this.getCommand("settings").setExecutor(new Settings());
        this.getCommand("banip").setExecutor(new BanIP());
        this.getCommand("fastcrystals").setExecutor(new FastCrystals());

        TeleportCompleter tabCompleter = new TeleportCompleter();
        this.getCommand("tpa").setTabCompleter(tabCompleter);
        this.getCommand("tpaccept").setTabCompleter(tabCompleter);
        this.getCommand("tpahere").setTabCompleter(tabCompleter);
        this.getCommand("tpdeny").setTabCompleter(tabCompleter);

        ClaimCommand claimCommand = new ClaimCommand();
        this.getCommand("kit").setExecutor(new KitCommand());
        this.getCommand("kit1").setExecutor(claimCommand);
        this.getCommand("kit2").setExecutor(claimCommand);
        this.getCommand("kit3").setExecutor(claimCommand);
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

    private void setupAnticheat() {
        System.setProperty("com.viaversion.handlePingsAsInvAcknowledgements", "true");
        Bukkit.getScheduler().runTaskTimer(Initializer.p, () -> GrimAPI.INSTANCE.getTickManager().tickSync(), 0, 1);
        Bukkit.getScheduler().runTaskTimerAsynchronously(Initializer.p, () -> GrimAPI.INSTANCE.getTickManager().tickAsync(), 0, 1);
        Bukkit.getScheduler().runTaskTimerAsynchronously(Initializer.p, () -> {
            for (GrimPlayer player : GrimAPI.INSTANCE.getPlayerDataManager().getEntries()) {
                player.cancelledPackets.set(0);
            }
        }, 1, 20L);
    }

    @Override
    public void onEnable() {
        dataFolder = getDataFolder();
        Bukkit.getScheduler().runTaskLater(this, () -> {
            d = Bukkit.getWorld("world");
            d0 = Bukkit.getWorld("world_the_end");
            nmsOverworld = ((CraftWorld) d).getHandle();
            chunkSource = nmsOverworld.getChunkSource();
            main.utils.Utils.lightEngine = chunkSource.getLightEngine();
            for (short i = 0; i < 101; i++) {
                if (i == 100) {
                    getCommand("rtp").setExecutor(new RTP());
                    Bukkit.getScheduler().scheduleSyncRepeatingTask(p, () -> {
                        for (Entity ent : d.getEntities()) {
                            if (!(ent instanceof EnderPearl) && !(ent instanceof Player)) ent.remove();
                        }
                        ticked++;
                        main.utils.Utils.setCuboid(92, 115, 268, -98, 117, 458, Blocks.AIR, Blocks.AIR.defaultBlockState());
                        if (ticked == 3) {
                            switch (flatstr++) {
                                case 1 -> {
                                    main.utils.Utils.setArea(114, -98, 458, 92, 268, Blocks.NETHERITE_BLOCK.defaultBlockState());
                                }
                                case 2 -> {
                                    main.utils.Utils.setArea(114, -98, 458, 92, 268,
                                            new BlockState[]{
                                                    Blocks.STRIPPED_DARK_OAK_WOOD.defaultBlockState(),
                                                    Blocks.STRIPPED_OAK_WOOD.defaultBlockState(),
                                                    Blocks.STRIPPED_BIRCH_WOOD.defaultBlockState(),
                                                    Blocks.STRIPPED_SPRUCE_WOOD.defaultBlockState()
                                            },
                                            5);
                                }
                                case 3 -> {
                                    main.utils.Utils.setArea(114, -98, 458, 92, 268,
                                            new BlockState[]{
                                                    Blocks.STONE.defaultBlockState(),
                                                    Blocks.MOSSY_COBBLESTONE.defaultBlockState(),
                                                    Blocks.MOSS_BLOCK.defaultBlockState(),
                                                    Blocks.GRASS_BLOCK.defaultBlockState()
                                            },
                                            5);
                                }
                                case 4 -> {
                                    main.utils.Utils.setArea(114, -98, 458, 92, 268, Blocks.STONE_BRICKS.defaultBlockState());
                                }
                                case 5 -> {
                                    main.utils.Utils.setArea(114, -98, 458, 92, 268,
                                            new BlockState[]{
                                                    Blocks.STONE.defaultBlockState(),
                                                    Blocks.SMOOTH_QUARTZ.defaultBlockState()
                                            },
                                            3);
                                    flatstr = 1;
                                }
                            }
                            String UNBAN_MSG = "§7You are now unbanned from " + SECOND_COLOR + "/flat!";
                            for (String p : bannedFromflat)
                                Bukkit.getPlayer(p).sendMessage(UNBAN_MSG);
                            bannedFromflat.clear();
                        } else if (ticked == 4) {
                            ticked = 0;
                            main.utils.Utils.setCuboid(-119, 96, -300, 5, 94, -176,
                                    Blocks.AIR,
                                    Blocks.AIR.defaultBlockState());
                            main.utils.Utils.setArea(93, -119, -300, 5, -176, Blocks.GRASS_BLOCK.defaultBlockState());
                            main.utils.Utils.setCuboid(-119, 92, -300, 5, 89, -176,
                                    Blocks.DIRT,
                                    Blocks.DIRT.defaultBlockState());
                            main.utils.Utils.setCuboid(-119, 88, -300, 5, -63, -176,
                                    Blocks.STONE,
                                    Blocks.STONE.defaultBlockState());
                            Location location;
                            for (Player k : inFFA) {
                                location = k.getLocation();
                                location.setY(94);
                                k.teleport(location);
                            }
                            for (Player p : Bukkit.getOnlinePlayers())
                                for (String msg : Initializer.MOTD)
                                    p.sendMessage(msg);
                        }
                    }, 0L, 2400L);
                    Bukkit.getLogger().warning("Finished RTP population.");
                    return;
                }
                short finalI = i;
                Bukkit.getScheduler().runTaskLater(this, () -> {
                    overworldRTP[finalI] = getRandomLoc(d, overworldRTP);
                    endRTP[finalI] = getRandomLoc(d0, endRTP);
                }, 1L);
            }
        }, 100L);
        registerCommands();
        Gui.init();
        registerPacketListeners();
        Initializer.init();

        KitsFile.setup();
        KitRoomFile.setup();
        if (KitsFile.get().contains("data")) restoreKitMap();
        restoreKitRoom();
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
        this.saveKitMap();
        this.saveKitRoom();
    }
}