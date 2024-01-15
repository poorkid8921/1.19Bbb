package main;

import com.github.retrooper.packetevents.PacketEvents;
import expansions.moderation.*;
import expansions.warps.*;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import main.commands.*;
import expansions.AntiCheat;
import expansions.arenas.Arena;
import expansions.arenas.ArenaIO;
import expansions.arenas.commands.CreateCommand;
import expansions.duels.commands.Duel;
import expansions.duels.commands.DuelAccept;
import expansions.duels.commands.DuelDeny;
import expansions.duels.commands.Event;
import expansions.optimizer.AnimationEvent;
import expansions.optimizer.InteractionEvent;
import expansions.optimizer.LastPacketEvent;
import main.utils.Constants;
import main.utils.Instances.CustomPlayerDataHolder;
import main.utils.TabTPA;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import static main.utils.Constants.*;

public class Practice extends JavaPlugin {
    public static FileConfiguration config;
    public static File dataFolder;
    public static World d;
    public static World d0;
    private static File dataFile;
    int flatstr = 1;
    int ticked = 0;
    boolean alreadySavingData = false;

    Location getRandomLoc(World w) {
        Location loc = null;
        while (loc == null) {
            int boundX = Constants.RANDOM.nextInt(-10000, 10000);
            int boundZ = Constants.RANDOM.nextInt(-10000, 10000);
            Block b = w.getHighestBlockAt(boundX, boundZ);
            if (b.isSolid())
                loc = new Location(w, boundX, b.getY() + 1, boundZ);
        }
        return loc;
    }

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings()
                .checkForUpdates(false)
                .reEncodeByDefault(false);
        PacketEvents.getAPI().load();
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
        this.getCommand("rtp").setExecutor(new RTP());
        this.getCommand("irename").setExecutor(new ItemRename());
        this.getCommand("clear").setExecutor(new Clear());
        this.getCommand("stats").setExecutor(new Stats());
        this.getCommand("spawn").setExecutor(new Spawn());
        this.getCommand("ffa").setExecutor(new Ffa());
        this.getCommand("flat").setExecutor(new Flat());
        this.getCommand("nethpot").setExecutor(new Nethpot());
        this.getCommand("warp").setExecutor(new Warp());
        this.getCommand("setwarp").setExecutor(new Setwarp());
        this.getCommand("acreate").setExecutor(new CreateCommand());
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

        this.getCommand("msg").setTabCompleter(new TabMSG());
        this.getCommand("tpa").setTabCompleter(new TabTPA());
        this.getCommand("tpaccept").setTabCompleter(new TabTPA());
        this.getCommand("tpahere").setTabCompleter(new TabTPA());
    }

    public void registerPacketListeners() {
        PacketEvents.getAPI().getEventManager().registerListener(new AnimationEvent());
        PacketEvents.getAPI().getEventManager().registerListener(new InteractionEvent());
        PacketEvents.getAPI().getEventManager().registerListener(new LastPacketEvent());
        PacketEvents.getAPI().getEventManager().registerListener(new AntiCheat());
        PacketEvents.getAPI().init();
    }

    public void setupWarps() {
        File arenasFolder = new File(dataFolder, "arenas");
        if (!arenasFolder.exists()) arenasFolder.mkdirs();
        Arena.arenas.clear();
        Arrays.stream(arenasFolder.listFiles()).parallel().forEach(result -> {
            try {
                Arena arena = ArenaIO.loadArena(result);
                if (arena != null)
                    Arena.arenas.put(arena.getName(), arena);
            } catch (Exception ignored) {
            }
        });
        File warpsFolder = new File(dataFolder, "warps");
        if (!warpsFolder.exists()) warpsFolder.mkdirs();
    }

    void saveData() {
        if (alreadySavingData)
            return;

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

    @Override
    public void onEnable() {
        dataFolder = getDataFolder();
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
                d.getEntities().stream()
                        .filter(result -> result instanceof EnderCrystal)
                        .forEach(Entity::remove);

                ticked++;
                if (ticked == 3) {
                    if (flatstr++ == 6)
                        flatstr = 1;

                    Arena.arenas.get("p_f" + flatstr).reset(10000);
                    bannedFromflat.clear();
                }
                else if (ticked == 6) {
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
                } else
                    Arena.arenas.get("flat").reset(100000);
            }, 0L, 2400L);
        }, 2400L);
        registerCommands();
        setupWarps();
        expansions.guis.Utils.init();
        registerPacketListeners();
        Bukkit.getPluginManager().registerEvents(new Events(), this);
        Constants.init();
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
        for (File file : new File(d
                .getWorldFolder()
                .getAbsolutePath() + "/entities/").listFiles()) {
            file.delete();
        }

        for (File file : new File(d0
                .getWorldFolder()
                .getAbsolutePath() + "/DIM1/").listFiles()) {
            file.delete();
        }

        long curTime = new Date().getTime();
        int accountsRemoved = 0;

        for (File file : new File(d
                .getWorldFolder()
                .getAbsolutePath() + "/stats/").listFiles()) {
            if (curTime - file.lastModified() > 6.048e+8) {
                accountsRemoved++;
                file.delete();
            }
        }

        int regionsRemoved = 0;
        for (File file : new File(d
                .getWorldFolder()
                .getAbsolutePath() + "/region/").listFiles()) {
            if (curTime - file.lastModified() > 6.048e+8) {
                regionsRemoved++;
                file.delete();
            }
        }
        Bukkit.getLogger().warning("Successfully purged " + accountsRemoved + " accounts & " + regionsRemoved + " regions.");
        saveData();
    }
}