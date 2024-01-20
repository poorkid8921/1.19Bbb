package main;

import com.github.retrooper.packetevents.PacketEvents;
import expansions.AntiCheat;
import expansions.arenas.Arena;
import expansions.arenas.ArenaIO;
import expansions.arenas.Section;
import expansions.arenas.commands.CreateCommand;
import expansions.economy.Balance;
import expansions.optimizer.AnimationEvent;
import expansions.optimizer.InteractionEvent;
import expansions.optimizer.LastPacketEvent;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import main.commands.*;
import main.utils.Constants;
import main.utils.TeleportCompleter;
import main.utils.instances.CustomPlayerDataHolder;
import net.luckperms.api.LuckPerms;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static main.utils.Constants.*;
import static main.utils.Utils.space;

public class Economy extends JavaPlugin {
    public static File dataFolder;
    public static World d;
    public static int vote_yes;
    public static int vote_no;
    public static FileConfiguration config;
    private static boolean alreadySavingData;
    private File dataFile;

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
        this.getCommand("rtp").setExecutor(new RTP());
        this.getCommand("exbal").setExecutor(new Balance());

        TeleportCompleter tabCompleter = new TeleportCompleter();
        this.getCommand("tpa").setTabCompleter(tabCompleter);
        this.getCommand("tpaccept").setTabCompleter(tabCompleter);
        this.getCommand("tpahere").setTabCompleter(tabCompleter);
    }

    public void setupWarps() {
        File arenasFolder = new File(dataFolder, "arenas");
        if (!arenasFolder.exists()) arenasFolder.mkdirs();
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
        PacketEvents.getAPI().getEventManager().registerListener(new AntiCheat());
        PacketEvents.getAPI().init();
    }

    @Override
    public void onEnable() {
        dataFolder = getDataFolder();
        dataFile = new File(dataFolder, "data.yml");
        config = YamlConfiguration.loadConfiguration(dataFile);
        chat = getServer().getServicesManager().getRegistration(Chat.class).getProvider();
        Constants.lp = Bukkit.getServicesManager().getRegistration(LuckPerms.class).getProvider();
        economy = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class).getProvider();
        Constants.p = this;

        Bukkit.getScheduler().runTaskLater(this, () -> {
            Bukkit.getLogger().warning("Started population of RTPs...");
            World d0 = Bukkit.getWorld("world_nether");
            World d1 = Bukkit.getWorld("world_the_end");
            new BukkitRunnable() {
                int i = 0;

                public void run() {
                    if (i++ == 101) {
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
                    -0.5, 142.06250, 0.5);

            Arena ffaup1 = Arena.arenas.get("ffa1");
            Arena.ResetLoopinData ffaup_data1 = new Arena.ResetLoopinData();
            ffaup_data1.speed = 2000000;
            for (Section s : ffaup1.getSections()) {
                int sectionAmount = (int) ((double) 2000000 / (double) (ffaup1.getc2().getBlockX() - ffaup1.getc1().getBlockX() + 1) * (ffaup1.getc2().getBlockY() - ffaup1.getc1().getBlockY() + 1) * (ffaup1.getc2().getBlockZ() - ffaup1.getc1().getBlockZ() + 1) * (double) s.getTotalBlocks());
                if (sectionAmount <= 0) sectionAmount = 1;
                ffaup_data1.sections.put(s.getID(), sectionAmount);
                ffaup_data1.sectionIDs.add(s.getID());
            }

            Arena ffaup2 = Arena.arenas.get("ffa2");
            Arena.ResetLoopinData ffaup_data2 = new Arena.ResetLoopinData();
            ffaup_data2.speed = 2000000;
            for (Section s : ffaup2.getSections()) {
                int sectionAmount = (int) ((double) 2000000 / (double) (ffaup2.getc2().getBlockX() - ffaup2.getc1().getBlockX() + 1) * (ffaup2.getc2().getBlockY() - ffaup2.getc1().getBlockY() + 1) * (ffaup2.getc2().getBlockZ() - ffaup2.getc1().getBlockZ() + 1) * (double) s.getTotalBlocks());
                if (sectionAmount <= 0) sectionAmount = 1;
                ffaup_data2.sections.put(s.getID(), sectionAmount);
                ffaup_data2.sectionIDs.add(s.getID());
            }

            ObjectArrayList<Player> players = new ObjectArrayList<>();
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
                Economy.d.getEntities().stream()
                        .filter(r -> r instanceof EnderCrystal)
                        .forEach(Entity::remove);

                for (Player a : Bukkit.getOnlinePlayers()) {
                    if (a.isGliding())
                        continue;

                    Location c = a.getLocation();
                    c.setY(318);
                    if (Economy.d.getBlockAt(c).getType() != Material.BARRIER)
                        continue;

                    players.add(a);
                    a.playSound(a.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);
                    a.sendMessage(new TextComponent("§7Vote: Should the arena be resetted? "), VOTE_YES, space, VOTE_NO);
                }
                Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
                    Event.verified.clear();
                    if (vote_yes > vote_no || (vote_no == 0)) {
                        boolean ffaupresetted;
                        boolean i = Constants.RANDOM.nextInt() == 1;
                        Arena.ResetLoopinData ffaup_data = i ? ffaup_data1 : ffaup_data2;
                        Arena ffaup = Arena.arenas.get(i ? "ffa1" : "ffa2");
                        do {
                            ffaupresetted = true;
                            for (Player a : players) {
                                a.sendMessage("§7The arena has been reset.");
                                Location c = a.getLocation();
                                c.setY(135);
                                a.teleportAsync(c);
                            }
                        } while (!ffaup.loopyReset(ffaup_data) && !ffaupresetted);
                    }
                    vote_no = 0;
                    vote_yes = 0;
                    saveData();
                }, 600L);
            }, 0L, 6000L);
        }, 100L);
        registerCommands();
        setupWarps();
        expansions.guis.Utils.init();
        registerPacketListeners();
        Constants.init();
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
        saveData();
    }
}