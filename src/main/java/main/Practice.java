package main;

import ac.events.*;
import ac.events.worldreader.PacketWorldReaderEighteen;
import ac.manager.TickManager;
import ac.player.GrimPlayer;
import ac.utils.anticheat.PlayerDataManager;
import ac.utils.lists.HookedListWrapper;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.util.reflection.Reflection;
import com.google.common.collect.ImmutableList;
import expansions.moderation.AntiAutoTotem;
import expansions.arenas.Arena;
import expansions.arenas.ArenaIO;
import expansions.arenas.commands.aCreate;
import expansions.duels.commands.Duel;
import expansions.duels.commands.DuelAccept;
import expansions.duels.commands.DuelDeny;
import expansions.duels.commands.Event;
import expansions.kits.commands.Kit;
import expansions.kits.commands.KitClaim;
import expansions.moderation.*;
import expansions.optimizer.AnimationEvent;
import expansions.optimizer.InteractionEvent;
import expansions.optimizer.LastPacketEvent;
import expansions.warps.Ffa;
import expansions.warps.Flat;
import expansions.warps.Nethpot;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import main.commands.List;
import main.commands.*;
import main.utils.Constants;
import main.utils.Instances.CustomPlayerDataHolder;
import main.utils.TeleportCompleter;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import sun.misc.Unsafe;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

import static main.utils.Constants.*;

public class Practice extends JavaPlugin {
    public static final PlayerDataManager PDM = new PlayerDataManager();
    public static final TickManager TM = new TickManager();
    public static FileConfiguration config;
    public static FileConfiguration kitroomconfig;
    public static FileConfiguration kitsconfig;
    public static File dataFolder;
    public static World d;
    public static World d0;
    private static File dataFile;
    private static File kitsdataFile;
    private static File kitroomdataFile;
    int flatstr = 1;
    int ticked = 0;
    boolean alreadySavingData = false;

    private static void tickRelMove() {
        for (GrimPlayer player : PDM.getEntries()) {
            if (player.disableGrim) continue;
            player.checkManager.getEntityReplication().onEndOfTickEvent();
        }
    }

    private static void restoreKitMap() {
        try {
            for (String key : kitsconfig.getConfigurationSection("data").getKeys(false)) {
                kitMap.put(key, new HashMap<>());

                for (String key2 : kitsconfig.getConfigurationSection("data." + key).getKeys(false)) {
                    if (key2.equals("items"))
                        kitMap.get(key).put(key2, (ImmutableList.of(kitsconfig.get("data." + key + "." + key2)).toArray(new ItemStack[0])));
                    else
                        switch (key2) {
                            case "name", "player", "UUID", "public" ->
                                    kitMap.get(key).put(key2, kitsconfig.get("data." + key + "." + key2).toString());
                        }
                }
            }
        } catch (Exception ignored) {
        }
    }

    private static void restoreKitRoom() {
        for (int i = 1; i <= 5; i = i + 1) {
            try {
                ItemStack[] content = (ItemStack[]) ((java.util.List) kitroomconfig.get("data." + i)).toArray(new ItemStack[0]);
                kitRoomMap.put(i, content);
            } catch (Exception e) {
                kitRoomMap.put(i, new ItemStack[45]);
            }
        }
    }

    Location getRandomLoc(World w) {
        Location loc = null;
        while (loc == null) {
            int boundX = Constants.RANDOM.nextInt(-10000, 10000);
            int boundZ = Constants.RANDOM.nextInt(-10000, 10000);
            Block b = w.getHighestBlockAt(boundX, boundZ);
            if (b.isSolid()) loc = new Location(w, boundX, b.getY() + 1, boundZ);
        }
        return loc;
    }

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings().checkForUpdates(false).reEncodeByDefault(false);
        PacketEvents.getAPI().load();
        try {
            Object connection = SpigotReflectionUtil.getMinecraftServerConnectionInstance();
            Field connectionsList = Reflection.getField(connection.getClass(), java.util.List.class, 1);
            java.util.List<Object> endOfTickObject = (java.util.List<Object>) connectionsList.get(connection);

            java.util.List<?> wrapper = Collections.synchronizedList(new HookedListWrapper<>(endOfTickObject) {
                @Override
                public void onIterator() {
                    tickRelMove();
                }
            });

            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            Unsafe unsafe = (Unsafe) unsafeField.get(null);
            unsafe.putObject(connection, unsafe.objectFieldOffset(connectionsList), wrapper);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
        }
    }

    public void registerCommands() {
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
        this.getCommand("duel").setExecutor(new Duel());
        this.getCommand("duelaccept").setExecutor(new DuelAccept());
        this.getCommand("dueldeny").setExecutor(new DuelDeny());
        this.getCommand("event").setExecutor(new Event());
        this.getCommand("msglock").setExecutor(new MsgLock());
        this.getCommand("tpalock").setExecutor(new TpaLock());
        this.getCommand("irename").setExecutor(new ItemRename());
        this.getCommand("clear").setExecutor(new Clear());
        this.getCommand("stats").setExecutor(new Stats());
        this.getCommand("spawn").setExecutor(new Spawn());
        this.getCommand("ffa").setExecutor(new Ffa());
        this.getCommand("flat").setExecutor(new Flat());
        this.getCommand("nethpot").setExecutor(new Nethpot());
        this.getCommand("warp").setExecutor(new Warp());
        this.getCommand("setwarp").setExecutor(new Setwarp());
        this.getCommand("acreate").setExecutor(new aCreate());
        this.getCommand("gmc").setExecutor(new GMc());
        this.getCommand("gms").setExecutor(new GMs());
        this.getCommand("gmsp").setExecutor(new GMsp());
        this.getCommand("playtime").setExecutor(new Playtime());
        this.getCommand("list").setExecutor(new List());
        this.getCommand("unban").setExecutor(new Unban());
        this.getCommand("ban").setExecutor(new Ban());
        this.getCommand("tp").setExecutor(new Teleport());
        this.getCommand("tphere").setExecutor(new TeleportHere());
        this.getCommand("tpall").setExecutor(new TeleportAll());

        TeleportCompleter tabCompleter = new TeleportCompleter();
        this.getCommand("tpa").setTabCompleter(tabCompleter);
        this.getCommand("tpaccept").setTabCompleter(tabCompleter);
        this.getCommand("tpahere").setTabCompleter(tabCompleter);

        KitClaim claim = new KitClaim();
        this.getCommand("ckit1").setExecutor(claim);
        this.getCommand("ckit2").setExecutor(claim);
        this.getCommand("ckit3").setExecutor(claim);
        this.getCommand("ckit").setExecutor(new Kit());
    }

    void saveData() {
        if (alreadySavingData) return;
        alreadySavingData = true;
        config.set("r", null);
        if (!playerData.isEmpty()) {
            for (Map.Entry<String, CustomPlayerDataHolder> entry : playerData.entrySet()) {
                CustomPlayerDataHolder value = entry.getValue();
                String key = entry.getKey();
                config.set("r." + key + ".0", value.getWins());
                config.set("r." + key + ".1", value.getLosses());
                config.set("r." + key + ".2", value.getKilleffect());
                config.set("r." + key + ".3", value.getMtoggle());
                config.set("r." + key + ".4", value.getTptoggle());
                config.set("r." + key + ".5", value.getMoney());
                config.set("r." + key + ".6", value.getElo());
                config.set("r." + key + ".7", value.getDeaths());
                config.set("r." + key + ".8", value.getKills());
            }
        }
        try {
            config.save(dataFile);
        } catch (IOException ignored) {
        }
        alreadySavingData = false;
    }

    private void registerPacketListeners() {
        PacketEvents.getAPI().getEventManager().registerListener(new PacketPlayerJoinQuit());
        PacketEvents.getAPI().getEventManager().registerListener(new PacketPingListener());
        PacketEvents.getAPI().getEventManager().registerListener(new PacketPlayerDigging());
        PacketEvents.getAPI().getEventManager().registerListener(new PacketPlayerAttack());
        PacketEvents.getAPI().getEventManager().registerListener(new PacketEntityAction());
        PacketEvents.getAPI().getEventManager().registerListener(new PacketBlockAction());
        PacketEvents.getAPI().getEventManager().registerListener(new PacketSelfMetadataListener());
        PacketEvents.getAPI().getEventManager().registerListener(new PacketServerTeleport());
        PacketEvents.getAPI().getEventManager().registerListener(new PacketPlayerCooldown());
        PacketEvents.getAPI().getEventManager().registerListener(new PacketPlayerRespawn());
        PacketEvents.getAPI().getEventManager().registerListener(new CheckManagerListener());
        PacketEvents.getAPI().getEventManager().registerListener(new PacketPlayerSteer());
        PacketEvents.getAPI().getEventManager().registerListener(new PacketWorldReaderEighteen());
        PacketEvents.getAPI().getEventManager().registerListener(new PacketSetWrapperNull());

        PacketEvents.getAPI().getEventManager().registerListener(new AnimationEvent());
        PacketEvents.getAPI().getEventManager().registerListener(new InteractionEvent());
        PacketEvents.getAPI().getEventManager().registerListener(new LastPacketEvent());
        PacketEvents.getAPI().getEventManager().registerListener(new AntiAutoTotem());
        PacketEvents.getAPI().init();
    }

    private void setupAC() {
        System.setProperty("com.viaversion.handlePingsAsInvAcknowledgements", "true");
        Bukkit.getScheduler().runTaskTimer(this, TM::tickSync, 0, 1);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, TM::tickAsync, 0, 1);

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            for (GrimPlayer player : PDM.getEntries()) {
                player.cancelledPackets.set(0);
            }
        }, 1, 20);
    }

    private void saveKitMap() {
        kitsconfig.set("data", null);
        if (!kitMap.isEmpty()) {
            for (Map.Entry<String, Map<String, Object>> stringHashMapEntry : kitMap.entrySet()) {
                for (Object o : ((HashMap) ((Map.Entry<String, HashMap<String, Object>>) (Map.Entry) stringHashMapEntry).getValue()).entrySet()) {
                    Map.Entry<String, Object> data = (Map.Entry) o;
                    kitsconfig.set("data." + ((Map.Entry<String, HashMap<String, Object>>) (Map.Entry) stringHashMapEntry).getKey() + "." + data.getKey(), data.getValue());
                }
            }
        }
        try {
            kitsconfig.save(kitsdataFile);
        } catch (IOException ignored) {

        }
    }

    private void saveKitRoom() {
        if (!kitRoomMap.isEmpty()) {
            for (Map.Entry<Integer, ItemStack[]> integerEntry : kitRoomMap.entrySet()) {
                kitroomconfig.set("data." + integerEntry.getKey().toString(), integerEntry.getValue());
            }
        }
        try {
            kitroomconfig.save(kitroomdataFile);
        } catch (IOException ignored) {

        }
    }

    private void setupKits() {
        if (kitsconfig.contains("data"))
            restoreKitMap();
        restoreKitRoom();
    }

    @Override
    public void onEnable() {
        dataFolder = getDataFolder();

        kitroomdataFile = new File(dataFolder, "kitroom.yml");
        kitroomconfig = YamlConfiguration.loadConfiguration(kitroomdataFile);

        kitsdataFile = new File(dataFolder, "kits.yml");
        kitsconfig = YamlConfiguration.loadConfiguration(kitsdataFile);

        dataFile = new File(dataFolder, "data.yml");
        config = YamlConfiguration.loadConfiguration(dataFile);

        chat = getServer().getServicesManager().getRegistration(Chat.class).getProvider();
        p = this;

        Bukkit.getScheduler().runTaskLater(this, () -> {
            Bukkit.getLogger().warning("Started population of RTPs...");
            new BukkitRunnable() {
                int i = 0;

                public void run() {
                    if (i++ == 101) {
                        getCommand("rtp").setExecutor(new RTP());
                        Bukkit.getLogger().warning("Finished RTP population.");
                        this.cancel();
                        return;
                    }
                    overworldRTP.add(getRandomLoc(d));
                    endRTP.add(getRandomLoc(d0));
                }
            }.runTaskTimer(this, 0L, 20L);
            Arena flat = Arena.arenas.get("flat");
            Arena ffa = Arena.arenas.get("ffa");
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
                for (Entity ent : d.getEntities()) {
                    if (ent instanceof EnderCrystal) ent.remove();
                }
                ticked++;
                if (ticked == 3) {
                    if (flatstr++ == 6) flatstr = 1;

                    Arena.arenas.get("p_f" + flatstr).reset(10000);
                    bannedFromflat.clear();
                } else if (ticked == 6) {
                    ticked = 0;

                    flat.reset(100000);
                    ffa.reset(10000000);
                    inFFA.stream().filter(s -> !s.isGliding()).forEach(player -> {
                        Location location = player.getLocation();
                        location.setY(318);
                        Block b = d.getBlockAt(location);
                        Block b2 = d.getBlockAt(location.add(0, 1, 0));

                        b.setType(Material.AIR, false);
                        b2.setType(Material.AIR, false);
                        location.setY(d.getHighestBlockYAt(location) + 1);
                        player.teleportAsync(location).thenAccept(reason -> {
                            b.setType(Material.BARRIER, false);
                            b2.setType(Material.BARRIER, false);
                        });
                    });
                    saveData();
                } else Arena.arenas.get("flat").reset(100000);
                for (String msg : ImmutableList.of("§7-------------- | CatSMP | --------------", Constants.BC_KITS, "§7--------------------------------------"))
                    Bukkit.broadcastMessage(msg);
            }, 0L, 2400L);
        }, 2400L);
        registerCommands();
        Arrays.stream(new File(dataFolder, "arenas").listFiles()).forEach(result -> {
            try {
                Arena arena = ArenaIO.loadArena(result);
                if (arena != null) Arena.arenas.put(arena.getName(), arena);
            } catch (Exception ignored) {
            }
        });

        expansions.guis.Utils.init();
        setupAC();
        setupKits();
        registerPacketListeners();
        Constants.init();
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().warning(playerData.toString());
        PacketEvents.getAPI().terminate();
        for (File[] file : ImmutableList.of(new File(d.getWorldFolder().getAbsolutePath() + "/entities/").listFiles(), new File(d0.getWorldFolder().getAbsolutePath() + "/DIM1/").listFiles())) {
            for (File value : file) {
                value.delete();
            }
        }

        long curTime = new Date().getTime();
        int accountsRemoved = 0;

        for (File file : new File(d.getWorldFolder().getAbsolutePath() + "/stats/").listFiles()) {
            if (curTime - file.lastModified() > 6.048e+8) {
                accountsRemoved++;
                file.delete();
            }
        }

        int regionsRemoved = 0;
        for (File file : new File(d.getWorldFolder().getAbsolutePath() + "/region/").listFiles()) {
            if (curTime - file.lastModified() > 6.048e+8) {
                regionsRemoved++;
                file.delete();
            }
        }
        Bukkit.getLogger().warning("Successfully purged " + accountsRemoved + " accounts & " + regionsRemoved + " regions.");
        saveData();
        saveKitRoom();
        saveKitMap();
    }
}