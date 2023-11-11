package main;

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
import main.utils.Languages;
import main.utils.TabTPA;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import static main.utils.Initializer.*;

@SuppressWarnings("deprecation")
public class Practice extends JavaPlugin implements TabExecutor {
    public static File df;
    public static FileConfiguration config;
    private static File cf;
    int flatstr = 1;
    int ticked = 0;

    public void saveCustomConfig() {
        try {
            config.save(cf);
        } catch (IOException ignored) {
        }
    }

    @Override
    public void onDisable() {
        long d = new Date().getTime();
        int x = 0;
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
                .getAbsolutePath() + "/entities/").listFiles()) {
            p.delete();
        }

        Bukkit.getLogger().warning("Successfully purged " + x + " accounts.");
    }

    public static World d;

    @Override
    public void onEnable() {
        df = getDataFolder();
        cf = new File(df, "data.yml");
        config = YamlConfiguration.loadConfiguration(cf);
        p = this;

        chat = getServer().getServicesManager().getRegistration(Chat.class).getProvider();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            if (Bukkit.getOnlinePlayers().size() > 0) {
                d.getEntities().stream()
                        .filter(r -> !(r instanceof Player) && !(r instanceof EnderPearl))
                        .forEach(Entity::remove);
                
                if (ticked++ == 3) {
                    if (flatstr++ == 6)
                        flatstr = 1;

                    Arena.arenas.get("p_f" + flatstr).reset(10000);
                }

                if (ticked == 6) {
                    ticked = 0;

                    Arena ffa = Arena.arenas.get("flat");
                    Arena.ResetLoopinData data = new Arena.ResetLoopinData();
                    data.speed = 10000;
                    for (Section s : ffa.getSections()) {
                        int sectionAmount = (int) ((double) 10000 / (double) (ffa.getc2().getBlockX() - ffa.getc1().getBlockX() + 1) * (ffa.getc2().getBlockY() - ffa.getc1().getBlockY() + 1) * (ffa.getc2().getBlockZ() - ffa.getc1().getBlockZ() + 1) * (double) s.getTotalBlocks());
                        if (sectionAmount <= 0) sectionAmount = 1;
                        data.sections.put(s.getID(), sectionAmount);
                        data.sectionIDs.add(s.getID());
                    }
                    
                    boolean flatresetted;
                    boolean ffaresetted;
                    boolean ffaupresetted;
                    do {
                        flatresetted = true;

                        ffa = Arena.arenas.get("ffa");
                        data = new Arena.ResetLoopinData();
                        data.speed = 1000000;
                        for (Section s : ffa.getSections()) {
                            int sectionAmount = (int) ((double) 1000000 / (double) (ffa.getc2().getBlockX() - ffa.getc1().getBlockX() + 1) * (ffa.getc2().getBlockY() - ffa.getc1().getBlockY() + 1) * (ffa.getc2().getBlockZ() - ffa.getc1().getBlockZ() + 1) * (double) s.getTotalBlocks());
                            if (sectionAmount <= 0) sectionAmount = 1;
                            data.sections.put(s.getID(), sectionAmount);
                            data.sectionIDs.add(s.getID());
                        }

                        do {
                            ffaresetted = true;

                            ffa = Arena.arenas.get("ffaup");
                            data = new Arena.ResetLoopinData();
                            data.speed = 2000;
                            for (Section s : ffa.getSections()) {
                                int sectionAmount = (int) ((double) 2000 / (double) (ffa.getc2().getBlockX() - ffa.getc1().getBlockX() + 1) * (ffa.getc2().getBlockY() - ffa.getc1().getBlockY() + 1) * (ffa.getc2().getBlockZ() - ffa.getc1().getBlockZ() + 1) * (double) s.getTotalBlocks());
                                if (sectionAmount <= 0) sectionAmount = 1;
                                data.sections.put(s.getID(), sectionAmount);
                                data.sectionIDs.add(s.getID());
                            }

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
                                bannedFromflat.clear();
                            } while (!ffa.loopyReset(data) && !ffaupresetted);
                        } while (!ffa.loopyReset(data) && !ffaresetted);
                    } while (!ffa.loopyReset(data) && !flatresetted);
                }
                else
                    Arena.arenas.get("flat").reset(10000);
            }
        }, 0L, 2400L);

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

        this.getCommand("spawn").setExecutor(new Spawn());
        this.getCommand("ffa").setExecutor(new Ffa());
        this.getCommand("flat").setExecutor(new Flat());

        this.getCommand("warp").setExecutor(new Warp());
        this.getCommand("setwarp").setExecutor(new Setwarp());

        this.getCommand("acreate").setExecutor(new CreateCommand());
        this.getCommand("gmc").setExecutor(new GMc());
        this.getCommand("gms").setExecutor(new GMs());
        this.getCommand("list").setExecutor(new List());
        this.getCommand("unban").setExecutor(new Unban());
        this.getCommand("ban").setExecutor(new Ban());
        this.getCommand("tp").setExecutor(new Teleport());
        this.getCommand("tphere").setExecutor(new TeleportHere());
        this.getCommand("ss").setExecutor(new Screenshare());

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
    }
}