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
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static main.utils.Initializer.economy;

@SuppressWarnings("deprecation")
public class Economy extends JavaPlugin implements CommandExecutor, TabExecutor {
    public static FileConfiguration cc;
    int ffa = 1;
    private File cf;

    public void saveCustomConfig() {
        try {
            cc.save(cf);
        } catch (IOException ignored) {
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null)
            return false;

        RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> rsp = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (rsp == null) return false;

        economy = rsp.getProvider();
        return true;
    }

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            setEnabled(false);
            return;
        }

        cf = new File(getDataFolder(), "data.yml");
        cc = YamlConfiguration.loadConfiguration(cf);
        Initializer.p = this;
        saveConfig();

        if (!cf.exists()) this.saveCustomConfig();

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) Initializer.lp = provider.getProvider();
        Bukkit.getPluginManager().registerEvents(new Events(), this);

        World d = Bukkit.getWorld("world");
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            d.getEntities().stream()
                    .filter(r -> r instanceof EnderCrystal)
                    .forEach(Entity::remove);
            if (ffa++ == 3)
                ffa = 1;

            Arena.arenas.get("ffa").reset(200000, "ffa" + ffa, 200000, d);
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

        File[] rc = new File(Bukkit.getWorld("world")
                .getWorldFolder()
                .getAbsolutePath() + "/logs/").listFiles();

        try {
            Pattern p = Pattern.compile("\\$[0-9]+(.[0-9]+)?\\w", Pattern.CASE_INSENSITIVE);
            String toLookup = "???? » PRITHVI_XD bought";
            String toLookup2 = "???? » PRITHVI_XD sold";
            int sold = 0;
            int bought = 0;
            int fails = 0;
            for (File r : Arrays.stream(rc).sorted().toList()) {
                try {
                    Scanner myReader = new Scanner(r);
                    while (myReader.hasNextLine()) {
                        String data = myReader.nextLine();
                        if (data.contains(toLookup2)) {
                            Matcher dc = p.matcher(data.replace(",", ""));
                            if (!dc.find()) {
                                fails++;
                                continue;
                            }

                            sold += Integer.parseInt(dc.group(1).replace("$", ""));
                            Bukkit.getLogger().warning(data);
                        } else if (data.contains(toLookup)) {
                            Matcher dc = p.matcher(data.replace(",", ""));
                            if (!dc.find()) {
                                fails++;
                                continue;
                            }

                            bought += Integer.parseInt(dc.group(1).replace("$", ""));
                            Bukkit.getLogger().warning(data);
                        } else
                            fails++;
                    }
                    myReader.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            Bukkit.getLogger().warning("sum: " + (sold - bought) + " | fails: " + fails);
        } catch (RuntimeException ignored) {

        }
        Languages.init();
    }
}