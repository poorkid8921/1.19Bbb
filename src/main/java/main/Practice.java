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
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import main.commands.Discord;
import main.commands.Killeffect;
import main.commands.Report;
import main.commands.essentials.*;
import main.commands.warps.*;
import main.utils.AntiAutoTotem;
import main.utils.Gui;
import main.utils.Initializer;
import main.utils.Instances.CustomPlayerDataHolder;
import main.utils.TeleportCompleter;
import main.utils.arenas.Arena;
import main.utils.arenas.ArenaIO;
import main.utils.arenas.CreateCommand;
import main.utils.arenas.ResetCommand;
import main.utils.kits.commands.Kit;
import main.utils.kits.commands.KitClaim;
import main.utils.optimizer.InteractionListeners;
import main.utils.optimizer.LastPacketEvent;
import main.utils.storage.DB;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import sun.misc.Unsafe;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static main.utils.Initializer.*;

public class Practice extends JavaPlugin {
    public static final PlayerDataManager PDM = new PlayerDataManager();
    public static final TickManager TM = new TickManager();
    public static FileConfiguration config;
    public static FileConfiguration kitRoomConfig;
    public static FileConfiguration kitsConfig;
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
            for (String key : kitsConfig.getConfigurationSection("data").getKeys(false)) {
                kitMap.put(key, new HashMap<>());

                for (String key2 : kitsConfig.getConfigurationSection("data." + key).getKeys(false)) {
                    if (key2 == "items")
                        kitMap.get(key).put(key2, (ImmutableList.of(kitsConfig.get("data." + key + "." + key2)).toArray(new ItemStack[0])));
                    else switch (key2) {
                        case "player", "UUID", "public" ->
                                kitMap.get(key).put(key2, kitsConfig.get("data." + key + "." + key2).toString());
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    private Location getRandomLoc(World w) {
        Location loc = null;
        while (loc == null) {
            int boundX = Initializer.RANDOM.nextInt(-10000, 10000);
            int boundZ = Initializer.RANDOM.nextInt(-10000, 10000);
            Block b = w.getHighestBlockAt(boundX, boundZ);
            if (b.isSolid()) loc = new Location(w, boundX, b.getY() + 1, boundZ);
        }
        return loc;
    }

    private void setupArenas() {
        for (File file : new File(dataFolder, "arenas").listFiles()) {
            Arena arena = ArenaIO.loadArena(file);
            if (arena != null) Arena.getArenas().put(arena.getName(), arena);
        }
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
        this.getCommand("stats").setExecutor(new Stats());
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

    private void saveData() throws IOException {
        if (alreadySavingData) return;
        alreadySavingData = true;
        if (!kitRoomMap.isEmpty()) {
            for (Map.Entry<Integer, ItemStack[]> integerEntry : kitRoomMap.entrySet()) {
                kitRoomConfig.set("data." + integerEntry.getKey().toString(), integerEntry.getValue());
            }
        }
        kitRoomConfig.save(kitroomdataFile);

        kitsConfig.set("data", null);
        if (!kitMap.isEmpty()) {
            for (Map.Entry<String, Map<String, Object>> stringHashMapEntry : kitMap.entrySet()) {
                for (Map.Entry<String, Object> var : stringHashMapEntry.getValue().entrySet()) {
                    kitsConfig.set("data." + stringHashMapEntry.getKey() + "." + var.getKey(), var.getValue());
                }
            }
        }
        kitsConfig.save(kitsdataFile);

        config.set("r", null);
        if (!playerData.isEmpty()) {
            for (Map.Entry<String, CustomPlayerDataHolder> entry : playerData.entrySet()) {
                CustomPlayerDataHolder value = entry.getValue();
                String key = entry.getKey();
                config.set("r." + key + ".0", value.getKilleffect());
                config.set("r." + key + ".1", value.getMtoggle());
                config.set("r." + key + ".2", value.getTptoggle());
                config.set("r." + key + ".3", value.getMoney());
                config.set("r." + key + ".4", value.getDeaths());
                config.set("r." + key + ".5", value.getKills());
                config.set("r." + key + ".6", value.getRank());
            }
        }
        config.save(dataFile);
        alreadySavingData = false;
    }

    private void registerPacketListeners() {
        PacketEvents.getAPI().getEventManager().registerListeners(
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
                new PacketSetWrapperNull(),
                new InteractionListeners(),
                new LastPacketEvent(),
                new AntiAutoTotem()
        );
        PacketEvents.getAPI().init();
    }

    private void setupAC() {
        System.setProperty("com.viaversion.handlePingsAsInvAcknowledgements", "true");
        Bukkit.getScheduler().runTaskTimer(this, TM::tickSync, 0, 1L);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, TM::tickAsync, 0, 1L);

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            for (GrimPlayer player : PDM.getEntries()) {
                player.cancelledPackets.set(0);
            }
        }, 1, 20);
    }

    private void setupConfigs() {
        dataFolder = getDataFolder();

        kitroomdataFile = new File(dataFolder, "kitroom.yml");
        kitRoomConfig = YamlConfiguration.loadConfiguration(kitroomdataFile);

        kitsdataFile = new File(dataFolder, "kits.yml");
        kitsConfig = YamlConfiguration.loadConfiguration(kitsdataFile);

        dataFile = new File(dataFolder, "data.yml");
        config = YamlConfiguration.loadConfiguration(dataFile);
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

    @Override
    public void onEnable() {
        setupConfigs();
        p = this;
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
                            if (ent instanceof EnderCrystal) ent.remove();
                        }
                        ticked++;
                        if (ticked == 3) {
                            if (flatstr++ == 6) flatstr = 1;

                            Arena.getArenas().get("p_f" + flatstr).reset(10000);
                            bannedFromflat.clear();
                        } else if (ticked == 6) {
                            ticked = 0;

                            flat.reset(100000);
                            ffa.reset(10000000);
                            for (Player k : inFFA) {
                                if (k.isGliding())
                                    continue;

                                Location location = k.getLocation();
                                location.setY(318);
                                Block b = d.getBlockAt(location);
                                Block b2 = d.getBlockAt(location.add(0, 1, 0));

                                b.setType(Material.AIR, false);
                                b2.setType(Material.AIR, false);
                                location.setY(d.getHighestBlockYAt(location) + 1);
                                b.setType(Material.BARRIER, false);
                                b2.setType(Material.BARRIER, false);
                                k.teleport(location);
                            }
                            for (String msg : ImmutableList.of("§7-------------- | CatSMP | ---------------", Initializer.BC_KITS, "§7-----------------------------------------"))
                                Bukkit.broadcastMessage(msg);
                            try {
                                saveData();
                            } catch (IOException ignored) {
                            }
                        } else Arena.getArenas().get("flat").reset(1000000);
                    }, 0L, 2400L);
                    Bukkit.getLogger().warning("Finished RTP population.");
                    return;
                }
                overworldRTP.add(getRandomLoc(d));
                endRTP.add(getRandomLoc(d0));
            }
        }, 100L);
        registerCommands();
        setupArenas();
        Gui.init();
        setupAC();
        if (kitsConfig.contains("data"))
            restoreKitMap();
        for (int i = 1; i < 6; i++) {
            try {
                kitRoomMap.put(i, (ItemStack[]) ((java.util.List) kitRoomConfig.get("data." + i)).toArray(new ItemStack[0]));
            } catch (Exception e) {
                kitRoomMap.put(i, new ItemStack[45]);
            }
        }
        registerPacketListeners();
        Initializer.init();
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
        try {
            saveData();
        } catch (IOException ignored) {
        }
    }
}