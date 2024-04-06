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
import main.commands.essentials.*;
import main.commands.essentials.List;
import main.commands.tpa.*;
import main.commands.warps.*;
import main.utils.AutoTotem;
import main.utils.Gui;
import main.utils.Initializer;
import main.utils.TeleportCompleter;
import main.utils.arenas.*;
import main.utils.kits.ClaimCommand;
import main.utils.kits.KitCommand;
import main.utils.kits.storage.KitRoomFile;
import main.utils.kits.storage.KitsFile;
import main.utils.npcs.InteractAtNPC;
import main.utils.optimizer.InteractionListeners;
import main.utils.optimizer.LastPacketEvent;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
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
import static org.bukkit.Bukkit.getMessenger;

public class Practice extends JavaPlugin {
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

    private Location getRandomLoc(World w) {
        Location loc = null;
        while (loc == null) {
            int boundX = Initializer.RANDOM.nextInt(-10000, 10000);
            int boundZ = Initializer.RANDOM.nextInt(-10000, 10000);
            Block b = w.getHighestBlockAt(boundX, boundZ);
            if (b.isSolid()) loc = new Location(w, boundX, b.getY() + 1, boundZ);
            Point point = new Point(0, 0);
            for (Location loc1 : overworldRTP) {
                if (loc1 == null)
                    break;
                int x = loc1.getBlockX();
                int z = loc1.getBlockZ();
                point.setLocation(x, z);
                if (point.distance(boundX, boundZ) < 50) {
                    loc = null;
                    break;
                }
            }
        }
        return loc;
    }

    private void setupArenas() {
        for (File file : new File(dataFolder, "arenas").listFiles()) {
            Arena arena;
            try {
                byte[] readBytes = Files.readAllBytes(file.toPath());
                int firstSectionSplit = ArrayUtils.indexOf(readBytes, (byte) '\u0002');
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
                int keySectionSplit = ArrayUtils.indexOf(readBytes, (byte) '\u0002', firstSectionSplit + 1);
                byte[] keyBytes = Arrays.copyOfRange(readBytes, firstSectionSplit + 1, keySectionSplit);
                java.util.List<Material> blockDataSet = new ArrayList<>();

                for (byte[] key : Utils.split(new byte[]{'\u0003'}, keyBytes)) {
                    String blockData = new String(key, StandardCharsets.US_ASCII);
                    try {
                        blockDataSet.add(Material.valueOf(blockData));
                    } catch (IllegalArgumentException e) {
                        try {
                            blockDataSet.add(Material.valueOf(blockData.split("\\[")[0]));
                        } catch (IllegalArgumentException ignored) {
                            return;
                        }
                    }
                }

                arena = new Arena(name, corner1, corner2);
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
            } catch (Exception ignored) {
                arena = null;
            }
            if (arena != null) Arena.arenas.put(arena.getName(), arena);
        }
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
        this.getCommand("nethpot").setExecutor(new Nethpot());
        this.getCommand("warp").setExecutor(new Warp());
        this.getCommand("setwarp").setExecutor(new SetWarp());
        this.getCommand("acreate").setExecutor(new CreateCommand());
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
        this.getCommand("areset").setExecutor(new ResetCommand());
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
        PacketEvents.getAPI().getEventManager().registerListeners(new InteractionListeners(), new LastPacketEvent(), new AutoTotem(), new InteractAtNPC(),
                new PacketPlayerJoinQuit(),
                new PacketPingListener(),
                new PacketPlayerDigging(),
                new PacketPlayerAttack(),
                new PacketEntityAction(),
                new PacketBlockAction(),
                new PacketSelfMetadataListener(),
                new PacketServerTeleport(),
                new PacketPlayerCooldown(),
                new PacketPlayerRespawn(),
                new CheckManagerListener(),
                new PacketPlayerSteer(),
                new PacketWorldReaderEighteen(),
                new PacketSetWrapperNull());
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
            for (int i = 0; i < 101; i++) {
                if (i == 100) {
                    getCommand("rtp").setExecutor(new RTP());
                    Arena flat = Arena.getArenas().get("flat");
                    Arena ffa = Arena.getArenas().get("ffa");
                    Bukkit.getScheduler().scheduleSyncRepeatingTask(p, () -> {
                        for (Entity ent : d.getEntities()) {
                            if (!(ent instanceof EnderPearl) && !(ent instanceof Player))
                                ent.remove();
                        }
                        ticked++;
                        flat.reset(10000000);
                        if (ticked == 3) {
                            if (flatstr++ == 6) flatstr = 1;
                            Arena.getArenas().get("f_p" + flatstr).reset(10000000);
                            String UNBAN_MSG = "§7You are now unbanned from " + MAIN_COLOR + "/flat!";
                            for (String p : bannedFromflat)
                                Bukkit.getPlayer(p).sendMessage(UNBAN_MSG);
                            bannedFromflat.clear();
                        } else if (ticked == 4) {
                            ticked = 0;
                            ffa.reset(10000000);
                            for (Player k : inFFA) {
                                Location location = k.getLocation();
                                location.setY(94);
                                k.teleport(location);
                            }
                            for (String msg : Initializer.MOTD)
                                for (Player p : Bukkit.getOnlinePlayers())
                                    p.sendMessage(msg);
                        }
                    }, 0L, 2400L);
                    Bukkit.getLogger().warning("Finished RTP population.");
                    return;
                }
                overworldRTP[i] = getRandomLoc(d);
                endRTP[i] = getRandomLoc(d0);
            }
        }, 100L);
        registerCommands();
        setupArenas();
        Gui.init();
        registerPacketListeners();
        Initializer.init();
        setupAnticheat();

        KitsFile.setup();
        KitRoomFile.setup();
        if (KitsFile.get().contains("data"))
            restoreKitMap();
        restoreKitRoom();
        getMessenger().registerOutgoingPluginChannel(this, "hcscr:haram");
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
        this.saveKitMap();
        this.saveKitRoom();
    }
}