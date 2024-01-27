package main;

import com.github.retrooper.packetevents.PacketEvents;
import expansions.AntiAutoTotem;
import expansions.arenas.Arena;
import expansions.arenas.ArenaIO;
import expansions.arenas.commands.CreateCommand;
import expansions.economy.Balance;
import expansions.Gui;
import expansions.optimizer.AnimationEvent;
import expansions.optimizer.InteractionEvent;
import expansions.optimizer.LastPacketEvent;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import main.commands.*;
import main.utils.Constants;
import main.utils.Instances.CustomPlayerDataHolder;
import main.utils.TeleportCompleter;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
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
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static main.utils.Constants.*;

public class Economy extends JavaPlugin {
    public static File dataFolder;
    public static World d;
    public static FileConfiguration config;
    private static boolean alreadySavingData;
    private File dataFile;
    private int arena = 1;

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

    void registerCommands() {
        this.getCommand("msg").setExecutor(new Msg());
        this.getCommand("reply").setExecutor(new Reply());
        this.getCommand("tpa").setExecutor(new Tpa());
        this.getCommand("tpaall").setExecutor(new TpaAll());
        this.getCommand("tpaccept").setExecutor(new Tpaccept());
        this.getCommand("tpahere").setExecutor(new Tpahere());
        this.getCommand("tpdeny").setExecutor(new TpDeny());
        this.getCommand("report").setExecutor(new Report());
        this.getCommand("msglock").setExecutor(new MsgLock());
        this.getCommand("tpalock").setExecutor(new TpaLock());
        this.getCommand("stats").setExecutor(new Stats());
        this.getCommand("acreate").setExecutor(new CreateCommand());
        this.getCommand("spawn").setExecutor(new Spawn());
        this.getCommand("discord").setExecutor(new Discord());
        this.getCommand("warp").setExecutor(new Warp());
        this.getCommand("setwarp").setExecutor(new Setwarp());
        this.getCommand("exbal").setExecutor(new Balance());

        TeleportCompleter tabCompleter = new TeleportCompleter();
        this.getCommand("tpa").setTabCompleter(tabCompleter);
        this.getCommand("tpaccept").setTabCompleter(tabCompleter);
        this.getCommand("tpahere").setTabCompleter(tabCompleter);
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
                config.set("r." + key + ".0", value.getMtoggle());
                config.set("r." + key + ".1", value.getTptoggle());
                config.set("r." + key + ".2", value.getMoney());
                config.set("r." + key + ".3", value.getDeaths());
                config.set("r." + key + ".4", value.getKills());
            }
        }
        try {
            config.save(dataFile);
        } catch (IOException ignored) {
        }
        alreadySavingData = false;
    }

    public void registerPacketListeners() {
        PacketEvents.getAPI().getEventManager().registerListener(new AnimationEvent());
        PacketEvents.getAPI().getEventManager().registerListener(new InteractionEvent());
        PacketEvents.getAPI().getEventManager().registerListener(new LastPacketEvent());
        PacketEvents.getAPI().getEventManager().registerListener(new AntiAutoTotem());
        PacketEvents.getAPI().init();
    }

    @Override
    public void onEnable() {
        dataFolder = getDataFolder();
        dataFile = new File(dataFolder, "data.yml");
        config = YamlConfiguration.loadConfiguration(dataFile);
        chat = getServer().getServicesManager().getRegistration(Chat.class).getProvider();
        lp = Bukkit.getServicesManager().getRegistration(LuckPerms.class).getProvider();
        economy = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class).getProvider();
        p = this;
        Bukkit.getScheduler().runTaskLater(this, () -> {
            Bukkit.getLogger().warning("Started population of RTPs...");
            World d0 = Bukkit.getWorld("world_nether");
            World d1 = Bukkit.getWorld("world_the_end");
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
                    netherRTP.add(getRandomLoc(d0));
                    endRTP.add(getRandomLoc(d1));
                }
            }.runTaskTimer(this, 0L, 20L);
            d = Bukkit.getWorld("world");
            Constants.spawn = new Location(Economy.d,
                    -0.5, 141.06250, 0.5);

            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
                for (Entity ent : d.getEntities()) {
                    if (ent instanceof EnderCrystal)
                        ent.remove();
                }

                if (arena++ == 3)
                    arena = 1;
                Arena.arenas.get(arena == 1 ? "ffa1" : "ffa2").reset(100000);
                for (Player a : Bukkit.getOnlinePlayers()) {
                    if (a.isGliding())
                        continue;

                    Location c = a.getLocation();
                    c.setY(200);
                    if (Economy.d.getBlockAt(c).getType() != Material.BARRIER)
                        continue;
                    c.setY(135);
                    a.teleportAsync(c);
                }
            }, 0L, 24000L);
        }, 100L);
        registerCommands();
        Arrays.stream(new File(dataFolder, "arenas").listFiles()).forEach(result -> {
            try {
                Arena arena = ArenaIO.loadArena(result);
                if (arena != null)
                    Arena.arenas.put(arena.getName(), arena);
            } catch (Exception ignored) {
            }
        });
        Gui.init();
        registerPacketListeners();
        Constants.init();
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
        saveData();
    }
}