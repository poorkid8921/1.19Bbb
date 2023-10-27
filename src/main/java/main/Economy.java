package main;

import commands.*;
import commands.chat.Msg;
import commands.chat.MsgLock;
import commands.chat.Reply;
import commands.chat.TpaLock;
import commands.tpa.*;
import main.expansions.arenas.Arena;
import main.expansions.arenas.ArenaIO;
import main.expansions.arenas.CreateCommand;
import main.utils.Initializer;
import main.utils.Languages;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static main.utils.Initializer.economy;

@SuppressWarnings("deprecation")
public class Economy extends JavaPlugin implements CommandExecutor, TabExecutor {
    public static FileConfiguration cc;
    public static File cf;
    public static File df;
    int ffa = 1;

    public void saveCustomConfig() {
        try {
            cc.save(cf);
        } catch (IOException ignored) {
        }
    }

    @Override
    public void onEnable() {
        df = getDataFolder();
        cf = new File(df, "data.yml");
        cc = YamlConfiguration.loadConfiguration(cf);
        Initializer.p = this;
        saveConfig();

        if (!cf.exists()) this.saveCustomConfig();

        Initializer.lp = Bukkit.getServicesManager().getRegistration(LuckPerms.class).getProvider();
        economy = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class).getProvider();
        Bukkit.getPluginManager().registerEvents(new Events(), this);

        World d = Bukkit.getWorld("world");
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            d.getEntities().stream()
                    .filter(r -> r instanceof EnderCrystal)
                    .forEach(Entity::remove);
            if (ffa++ == 3)
                ffa = 1;

            Arena.arenas.get("ffa").reset(200000);
            Arena.arenas.get("ffa" + ffa).reset(20000);
            Bukkit.getOnlinePlayers().stream().filter(s -> !s.isGliding() &&
                    d.getBlockAt(new Location(d, s.getLocation().getX(), 319, s.getLocation().getZ())).getType() == Material.BARRIER).forEach(player ->
            {
                Location l = player.getLocation();
                player.teleportAsync(new Location(d,
                        l.getX(),
                        135,
                        l.getZ(),
                        l.getYaw(),
                        l.getPitch()));
            });
        }, 0L, 24000L);
        // CHAT
        this.getCommand("msg").setExecutor(new Msg());
        this.getCommand("reply").setExecutor(new Reply());

        // TPA
        this.getCommand("tpa").setExecutor(new Tpa());
        this.getCommand("tpaall").setExecutor(new TpaAll());
        this.getCommand("tpaccept").setExecutor(new Tpaccept());
        this.getCommand("tpahere").setExecutor(new Tpahere());
        this.getCommand("tpdeny").setExecutor(new TpDeny());

        this.getCommand("tpa").setTabCompleter(new TabTPA());
        this.getCommand("tpaccept").setTabCompleter(new TabTPA());
        this.getCommand("tpahere").setTabCompleter(new TabTPA());

        // OTHER
        this.getCommand("report").setExecutor(new Report());

        this.getCommand("msglock").setExecutor(new MsgLock());
        this.getCommand("tpalock").setExecutor(new TpaLock());

        this.getCommand("stats").setExecutor(new Stats());
        this.getCommand("acreate").setExecutor(new CreateCommand());

        this.getCommand("spawn").setExecutor(new Spawn());
        this.getCommand("discord").setExecutor(new Discord());
        this.getCommand("purge").setExecutor(new Purge());

        File folder = new File(Initializer.p.getDataFolder(), "Arenas");
        if (!folder.exists()) folder.mkdirs();

        Arena.arenas.clear();
        Arrays.stream(folder.listFiles()).parallel().forEach(r -> {
            try {
                Arena arena = ArenaIO.loadArena(r);
                if (arena == null) return;
                Arena.arenas.put(arena.getName(), arena);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Languages.init();
        Initializer.requests.remove(null);
    }
}