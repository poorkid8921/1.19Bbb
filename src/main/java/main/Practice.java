package main;

import com.google.common.collect.ImmutableList;
import main.commands.*;
import main.commands.chat.Msg;
import main.commands.chat.MsgLock;
import main.commands.chat.Reply;
import main.commands.ess.*;
import main.commands.tpa.*;
import main.expansions.arenas.Arena;
import main.expansions.arenas.ArenaIO;
import main.expansions.arenas.Section;
import main.expansions.arenas.commands.CreateCommand;
import main.expansions.duels.commands.Duel;
import main.expansions.duels.commands.DuelAccept;
import main.expansions.duels.commands.DuelDeny;
import main.expansions.duels.commands.Event;
import main.utils.Initializer;
import main.utils.Instances.CustomPlayerDataHolder;
import main.utils.Instances.LocationHolder;
import main.utils.Languages;
import main.utils.TabTPA;
import main.utils.Utils;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import static main.utils.Initializer.*;
import static main.utils.Languages.MAIN_COLOR;

@SuppressWarnings("deprecation")
public class Practice extends JavaPlugin implements TabExecutor {
    public static File df;
    public static FileConfiguration config;
    public static World d;
    public static World d0;
    private static File cf;
    int flatstr = 1;
    int ticked = 0;
    ImmutableList<Material> blacklist = ImmutableList.of(Material.AIR, Material.LAVA, Material.WATER);

    @Override
    public void onDisable() {
        long d = new Date().getTime();
        int x = 0;
        int y = 0;
        for (File p : new File(Bukkit.getWorld("world")
                .getWorldFolder()
                .getAbsolutePath() + "/stats/").listFiles()) {
            if (d - p.lastModified() > 6.048e+8) {
                x++;
                p.delete();
            }
        }

        for (File p : new File(Bukkit.getWorld("world")
                .getWorldFolder()
                .getAbsolutePath() + "/region/").listFiles()) {
            if (d - p.lastModified() > 6.048e+8) {
                y++;
                p.delete();
            }
        }

        for (File p : new File(Bukkit.getWorld("world_the_end")
                .getWorldFolder()
                .getAbsolutePath() + "/DIM1/").listFiles()) {
            p.delete();
        }

        for (File p : new File(Bukkit.getWorld("world")
                .getWorldFolder()
                .getAbsolutePath() + "/entities/").listFiles()) {
            p.delete();
        }

        Bukkit.getLogger().warning("Successfully purged " + x + " accounts & " + y + " regions.");

        config.set("r", null);
        if (!playerData.isEmpty()) {
            for (Map.Entry<String, CustomPlayerDataHolder> entry : playerData.entrySet()) {
                String key = entry.getKey();
                CustomPlayerDataHolder value = entry.getValue();
                config.set("r." + key + ".w", value.getWins());
                config.set("r." + key + ".l", value.getLosses());
                config.set("r." + key + ".c", value.getC());
                config.set("r." + key + ".m", value.getM());
                config.set("r." + key + ".t", value.getT());
            }
        }

        try {
            config.save(cf);
        } catch (IOException ignored) {
        }
    }

    LocationHolder getRandomLoc(World w) {
        LocationHolder loc = null;
        while (loc == null) {
            int boundX = Initializer.RANDOM.nextInt(10000);
            int boundZ = Initializer.RANDOM.nextInt(10000);

            if (boundX > 5000)
                boundX = -boundX;
            if (boundZ > 5000)
                boundZ = -boundZ;

            Block b = w.getHighestBlockAt(boundX, boundZ);
            if (b.isSolid() && !blacklist.contains(b.getType()))
                loc = new LocationHolder(new int[]{boundX, b.getY() + 1, boundZ});
        }

        return loc;
    }

    void setupHolos() {
        // Test
        Location loc = new Location(d, 0, 300, 0);
        for (int i = 0; i < 3; i++) {
            loc.setY(loc.getY() - 0.3);
            Utils.createHologram(i,
                    MAIN_COLOR + "Test",
                    loc);
        }
    }

    @Override
    public void onEnable() {
        df = getDataFolder();
        cf = new File(df, "data.yml");
        config = YamlConfiguration.loadConfiguration(cf);
        chat = getServer().getServicesManager().getRegistration(Chat.class).getProvider();
        p = this;

        Bukkit.getScheduler().runTaskLater(this, () -> {
            Bukkit.getLogger().warning("Started population thread of Overworld's RTP");
            for (int i = 0; i < 100; i++) {
                Bukkit.getScheduler().runTaskLater(this,
                        () -> overworldRTP.add(getRandomLoc(d)),
                        5L);
            }
            Bukkit.getLogger().warning("Started population thread of End's RTP");
            for (int i = 0; i < 100; i++) {
                Bukkit.getScheduler().runTaskLater(this,
                        () -> endRTP.add(getRandomLoc(d)),
                        5L);
            }

            Arena flat = Arena.arenas.get("flat");
            Arena.ResetLoopinData flat_data = new Arena.ResetLoopinData();
            flat_data.speed = 10000;
            for (Section s : flat.getSections()) {
                int sectionAmount = (int) ((double) 10000 / (double) (flat.getc2().getBlockX() - flat.getc1().getBlockX() + 1) * (flat.getc2().getBlockY() - flat.getc1().getBlockY() + 1) * (flat.getc2().getBlockZ() - flat.getc1().getBlockZ() + 1) * (double) s.getTotalBlocks());
                if (sectionAmount <= 0) sectionAmount = 1;
                flat_data.sections.put(s.getID(), sectionAmount);
                flat_data.sectionIDs.add(s.getID());
            }

            Arena ffa = Arena.arenas.get("ffa");
            Arena.ResetLoopinData ffa_data = new Arena.ResetLoopinData();
            ffa_data.speed = 1000000;
            for (Section s : ffa.getSections()) {
                int sectionAmount = (int) ((double) 1000000 / (double) (ffa.getc2().getBlockX() - ffa.getc1().getBlockX() + 1) * (ffa.getc2().getBlockY() - ffa.getc1().getBlockY() + 1) * (ffa.getc2().getBlockZ() - ffa.getc1().getBlockZ() + 1) * (double) s.getTotalBlocks());
                if (sectionAmount <= 0) sectionAmount = 1;
                ffa_data.sections.put(s.getID(), sectionAmount);
                ffa_data.sectionIDs.add(s.getID());
            }

            Arena ffaup = Arena.arenas.get("ffaup");
            Arena.ResetLoopinData ffaup_data = new Arena.ResetLoopinData();
            ffaup_data.speed = 200000;
            for (Section s : ffaup.getSections()) {
                int sectionAmount = (int) ((double) 200000 / (double) (ffaup.getc2().getBlockX() - ffaup.getc1().getBlockX() + 1) * (ffaup.getc2().getBlockY() - ffaup.getc1().getBlockY() + 1) * (ffaup.getc2().getBlockZ() - ffaup.getc1().getBlockZ() + 1) * (double) s.getTotalBlocks());
                if (sectionAmount <= 0) sectionAmount = 1;
                ffaup_data.sections.put(s.getID(), sectionAmount);
                ffaup_data.sectionIDs.add(s.getID());
            }

            // Entity method <<< -\\\Packets///-
            //setupHolos();
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
                //if (Bukkit.getOnlinePlayers().size() > 0) {
                d.getEntities().stream()
                        .filter(r -> r instanceof EnderCrystal)
                        .forEach(Entity::remove);

                if (ticked++ == 3) {
                    if (flatstr++ == 6)
                        flatstr = 1;

                    Arena.arenas.get("p_f" + flatstr).reset(10000);
                    bannedFromflat.clear();
                    if (ticked == 6) {
                        ticked = 0;

                        boolean flatresetted;
                        boolean ffaresetted;
                        boolean ffaupresetted;
                        do {
                            flatresetted = true;

                            do {
                                ffaresetted = true;

                                do {
                                    ffaupresetted = true;
                                    inFFA.stream().filter(s -> !s.isGliding()).forEach(player -> {
                                        Location location = player.getLocation();
                                        location.setY(200);
                                        Block b = d.getBlockAt(location);
                                        Block b2 = d.getBlockAt(location.add(0, 1, 0));

                                        b2.setType(Material.AIR, false);
                                        b.setType(Material.AIR, false);
                                        location.setY(d.getHighestBlockYAt(location) + 1);
                                        player.teleportAsync(location).thenAccept(reason -> {
                                            b.setType(Material.BARRIER, false);
                                            b2.setType(Material.BARRIER, false);
                                        });
                                    });
                                } while (!ffaup.loopyReset(ffaup_data) && !ffaupresetted);
                            } while (!ffa.loopyReset(ffa_data) && !ffaresetted);
                        } while (!flat.loopyReset(flat_data) && !flatresetted);
                    } else
                        Arena.arenas.get("flat").reset(10000);
                } else
                    Arena.arenas.get("flat").reset(10000);
                //}
            }, 0L, 2400L);
        }, 240L);

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

        this.getCommand("spawn").setExecutor(new Spawn());
        this.getCommand("ffa").setExecutor(new Ffa());
        this.getCommand("flat").setExecutor(new Flat());

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

        File arenaF = new File(df, "arenas");
        if (!arenaF.exists()) arenaF.mkdirs();

        File wF = new File(df, "warps");
        if (!wF.exists()) wF.mkdirs();

        Arena.arenas.clear();
        Arrays.stream(arenaF.listFiles()).parallel().forEach(r -> {
            try {
                Arena arena = ArenaIO.loadArena(r);
                if (arena == null)
                    return;
                Arena.arenas.put(arena.getName(), arena);
            } catch (Exception ignored) {
            }
        });
        Languages.init();
        main.expansions.guis.Utils.init();
        Bukkit.getPluginManager().registerEvents(new Events(), this);

        d = Bukkit.getWorld("world");
        d0 = Bukkit.getWorld("world_the_end");
        Initializer.ffa = new Location(d,
                -243.5,
                156,
                -580.5);
        Initializer.flat = new Location(d,
                -2.5,
                131.0625,
                363.5);
        Initializer.spawn = new Location(d,
                0.5,
                86,
                0.5);
        Initializer.spawn.setYaw(
                90F
        );
        Initializer.flat.setYaw(
                90F
        );

        if (config.contains("r")) {
            int dataLoaded = 0;
            for (String key : config.getConfigurationSection("r").getKeys(false)) {
                for (String key2 : config.getConfigurationSection("r." + key).getKeys(false)) {
                    int wins = 0;
                    int losses = 0;
                    int c = -1;
                    int m = 0;
                    int t = 0;
                    switch (key2) {
                        case "w" -> wins = config.getInt("r." + key + "." + key2);
                        case "l" -> losses = config.getInt("r." + key + "." + key2);
                        case "c" -> c = config.getInt("r." + key + "." + key2);
                        case "m" -> m = config.getInt("r." + key + "." + key2);
                        case "t" -> t = config.getInt("r." + key + "." + key2);
                    }

                    playerData.put(key, new CustomPlayerDataHolder(wins, losses, c, m, t));
                }
            }
            Bukkit.getLogger().warning("Successfully loaded " + dataLoaded + " accounts!");
        }
    }
}