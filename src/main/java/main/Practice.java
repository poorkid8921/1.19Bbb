package main;

import main.commands.*;
import main.commands.chat.Msg;
import main.commands.chat.MsgLock;
import main.commands.chat.Reply;
import main.commands.duel.Duel;
import main.commands.duel.DuelAccept;
import main.commands.duel.DuelDeny;
import main.commands.duel.Event;
import main.commands.ess.*;
import main.commands.tpa.*;
import main.expansions.arenas.Arena;
import main.expansions.arenas.ArenaIO;
import main.expansions.arenas.CreateCommand;
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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
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

        // less job on the GC
        ticked = 0;
        chat = null;
        cf = null;
        config = null;
        df = null;

        RANDOM = null;
        inDuel.clear();
        back.clear();
        duel.clear();
        chatdelay.clear();
        inFFA.clear();
        inMatchmaking.clear();
        lastReceived.clear();
        msg.clear();
        tpa.clear();
        teams.clear();
        valid.clear();
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    @Override
    public void onEnable() {
        if (!setupChat())
            return;

        df = getDataFolder();

        cf = new File(df, "data.yml");
        config = YamlConfiguration.loadConfiguration(cf);

        p = this;
        saveConfig();

        if (!cf.exists()) this.saveCustomConfig();

        World d = Bukkit.getWorld("world");
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            if (Bukkit.getOnlinePlayers().size() > 0) {
                d.getEntities().stream()
                        .filter(r -> !(r instanceof Player))
                        .forEach(Entity::remove);

                Arena.arenas.get("flat").reset(30000);
                if (ticked++ == 3) {
                    if (flatstr++ == 6)
                        flatstr = 1;

                    Arena.arenas.get("p_f" + flatstr).reset(30000);
                }

                if (ticked == 4) {
                    ticked = 0;
                    Arena.arenas.get("ffa").reset(30000);
                    Arena.arenas.get("ffaup").reset(65000);
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
                }
            }
        }, 0L, 2400L);

        this.getCommand("vanish").setExecutor(new Vanish());

        this.getCommand("report").setExecutor(new Report());
        this.getCommand("killeffect").setExecutor(new Killeffect());
        this.getCommand("discord").setExecutor(new Discord());

        this.getCommand("back").setExecutor(new Back());

        this.getCommand("help").setExecutor(new Help());
        this.getCommand("tpa").setExecutor(new TpaCommand());
        this.getCommand("tpaccept").setExecutor(new TpacceptCommand());
        this.getCommand("tpahere").setExecutor(new TpahereCommand());
        this.getCommand("tpdeny").setExecutor(new TpdenyCommand());

        this.getCommand("msg").setExecutor(new Msg());
        this.getCommand("reply").setExecutor(new Reply());

        this.getCommand("msg").setTabCompleter(new TabMSG());
        this.getCommand("tpa").setTabCompleter(new TabTPA());
        this.getCommand("tpaccept").setTabCompleter(new TabTPA());
        this.getCommand("tpahere").setTabCompleter(new TabTPA());

        this.getCommand("duel").setExecutor(new Duel());
        this.getCommand("duelaccept").setExecutor(new DuelAccept());
        this.getCommand("dueldeny").setExecutor(new DuelDeny());
        this.getCommand("event").setExecutor(new Event());

        this.getCommand("spawn").setExecutor(new Spawn());
        this.getCommand("ffa").setExecutor(new Ffa());
        this.getCommand("flat").setExecutor(new Flat());

        this.getCommand("msglock").setExecutor(new MsgLock());
        this.getCommand("tpalock").setExecutor(new TpaLock());

        this.getCommand("acreate").setExecutor(new CreateCommand());
        this.getCommand("rtp").setExecutor(new RTP());
        this.getCommand("gmc").setExecutor(new GMc());
        this.getCommand("gms").setExecutor(new GMs());
        this.getCommand("kickall").setExecutor(new Kickall());
        this.getCommand("list").setExecutor(new List());
        this.getCommand("unban").setExecutor(new Unban());
        this.getCommand("ban").setExecutor(new Ban());

        File folder = new File(getDataFolder(), "Arenas");
        if (!folder.exists()) folder.mkdirs();

        Arena.arenas.clear();
        Arrays.stream(folder.listFiles()).parallel().forEach(r -> {
            try {
                Arena arena = ArenaIO.loadArena(r);
                if (arena == null)
                    return;
                Arena.arenas.put(arena.getName(), arena);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        Languages.init();
        main.expansions.guis.Utils.init();
        Bukkit.getPluginManager().registerEvents(new Events(), this);
    }
}