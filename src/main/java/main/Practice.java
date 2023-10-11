package main;

import main.commands.*;
import main.commands.duel.Duel;
import main.commands.duel.DuelAccept;
import main.commands.duel.DuelDeny;
import main.commands.duel.Event;
import main.commands.tpa.TpaCommand;
import main.commands.tpa.TpacceptCommand;
import main.commands.tpa.TpahereCommand;
import main.commands.tpa.TpdenyCommand;
import main.expansions.arenas.Arena;
import main.expansions.arenas.ArenaIO;
import main.expansions.arenas.CreateCommand;
import main.utils.Languages;
import main.utils.TabTPA;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Level;

import static main.utils.Initializer.*;
import static main.utils.Utils.translate;
import static main.utils.Utils.translateo;

@SuppressWarnings("deprecation")
public class Practice extends JavaPlugin implements TabExecutor {
    public static File df;
    public static FileConfiguration cc;
    public static FileConfiguration cc1;
    private static File cf;
    private static File cf1;
    int ffastr = 1;
    int flatstr = 1;
    boolean hasReset;

    public static void log(String a) {
        Bukkit.getLogger().log(Level.FINE, a);
    }

    public void saveCustomConfig() {
        try {
            cc.save(cf);
        } catch (IOException ignored) {
        }
    }

    public void saveCustomConfig1() {
        try {
            cc1.save(cf1);
        } catch (IOException ignored) {
        }
    }

    @Override
    public void onDisable() {
        long d = new Date().getTime();
        int x = 0;
        int n = 0;
        int poi = 0;
        for (File p : new File(Bukkit.getWorld("world")
                .getWorldFolder()
                .getAbsolutePath() + "/stats/").listFiles()) {
            if (d - p.lastModified() > 1296000000) {
                x++;
                p.delete();
            }
        }

        for (File p : new File(Bukkit.getWorld("world")
                .getWorldFolder()
                .getAbsolutePath() + "/poi/").listFiles()) {
            if (d - p.lastModified() > 1296000000) {
                poi++;
                p.delete();
            }
        }

        for (File p : new File(Bukkit.getWorld("world")
                .getWorldFolder()
                .getAbsolutePath() + "/region/").listFiles()) {
            if (d - p.lastModified() > 1296000000) {
                poi++;
                p.delete();
            }
        }

        for (File p : new File(Bukkit.getWorld("world")
                .getWorldFolder()
                .getAbsolutePath() + "/entities/").listFiles()) {
            p.delete();
        }

        Bukkit.getLogger().warning("Successfully purged " + x + " accounts.");
        Bukkit.getLogger().warning("Successfully purged " + n + " regions.");
        Bukkit.getLogger().warning("Successfully purged " + poi + " poi regions.");
    }

    @Override
    public void onEnable() {
        df = getDataFolder();

        cf = new File(df, "data.yml");
        cc = YamlConfiguration.loadConfiguration(cf);
        cf1 = new File(df, "other.yml");
        cc1 = YamlConfiguration.loadConfiguration(cf1);

        if (!setupEconomy()) {
            setEnabled(false);
            return;
        }

        p = this;

        setupConfig();
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            lp = provider.getProvider();
            Bukkit.getPluginManager().registerEvents(new Events(), this);
        }

        long a = System.currentTimeMillis();
        initReset();
        initMessages();
        initMisc();
        initMain();

        // EXPANSIONS
        initExpansions(a);
    }

    private void initExpansions(long a) {
        File folder = new File(getDataFolder(), "Arenas");
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
        Practice.log("Initialized the Arenas expansion.");
        main.expansions.guis.Utils.init();
        log("Initialization Done! It took: " + (System.currentTimeMillis() - a) + " in order to finish.");
    }

    private void initMisc() {
        Objects.requireNonNull(this.getCommand("msg")).setTabCompleter(new TabMSG());

        Objects.requireNonNull(this.getCommand("report")).setExecutor(new Report());
        Objects.requireNonNull(this.getCommand("shop")).setExecutor(new Shop());
        Objects.requireNonNull(this.getCommand("discord")).setExecutor(new Discord());

        Objects.requireNonNull(this.getCommand("back")).setExecutor(new Back());

        Objects.requireNonNull(this.getCommand("help")).setExecutor(new Help());
        log("Initialized Background commands.");
    }

    private void initMain() {
        Objects.requireNonNull(this.getCommand("tpa")).setExecutor(new TpaCommand());
        Objects.requireNonNull(this.getCommand("tpaccept")).setExecutor(new TpacceptCommand());
        Objects.requireNonNull(this.getCommand("tpahere")).setExecutor(new TpahereCommand());
        Objects.requireNonNull(this.getCommand("tpdeny")).setExecutor(new TpdenyCommand());

        Objects.requireNonNull(this.getCommand("tpa")).setTabCompleter(new TabTPA());
        Objects.requireNonNull(this.getCommand("tpaccept")).setTabCompleter(new TabTPA());
        Objects.requireNonNull(this.getCommand("tpahere")).setTabCompleter(new TabTPA());

        Objects.requireNonNull(this.getCommand("duel")).setExecutor(new Duel());
        Objects.requireNonNull(this.getCommand("duelaccept")).setExecutor(new DuelAccept());
        Objects.requireNonNull(this.getCommand("dueldeny")).setExecutor(new DuelDeny());
        Objects.requireNonNull(this.getCommand("event")).setExecutor(new Event());

        Objects.requireNonNull(this.getCommand("msg")).setTabCompleter(new TabMSG());

        Objects.requireNonNull(this.getCommand("spawn")).setExecutor(new Spawn());
        Objects.requireNonNull(this.getCommand("ffa")).setExecutor(new Ffa());
        Objects.requireNonNull(this.getCommand("flat")).setExecutor(new Flat());

        Objects.requireNonNull(this.getCommand("msglock")).setExecutor(new MsgLock());
        Objects.requireNonNull(this.getCommand("tpalock")).setExecutor(new TpaLock());

        Objects.requireNonNull(this.getCommand("acreate")).setExecutor(new CreateCommand());
        log("Initialized Main commands.");
    }

    private void setupConfig() {
        saveConfig();

        if (cc == null) cc = YamlConfiguration.loadConfiguration(cf);
        if (cc1 == null) cc1 = YamlConfiguration.loadConfiguration(cf1);

        if (!cf.exists()) this.saveCustomConfig();
        if (!cf1.exists()) this.saveCustomConfig1();
    }

    private void initReset() {
        //Initializer.EXECUTOR.scheduleAtFixedRate(() -> Bukkit.getScheduler().runTask(this, () -> {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            if (Bukkit.getOnlinePlayers().size() > 0) {
                Bukkit.getWorld("world").getEntities().stream()
                        .filter(r -> r instanceof EnderCrystal)
                        .forEach(Entity::remove);

                if (ffastr++ == 3)
                    ffastr = 1;
                Arena.arenas.get("p_h" + ffastr).reset(65000);

                if (!hasReset) {
                    if (flatstr == 5)
                        flatstr = 1;

                    Arena.arenas.get("p_f" + flatstr).reset(940);
                    flatstr++;
                }

                hasReset = !hasReset;
                Arena.arenas.get("ffa").reset(2000000);
                Arena.arenas.get("flat").reset(940);

                inFFA.stream().filter(s -> !s.isInsideVehicle() && !s.isGliding()).forEach(player -> {
                    Location location = player.getLocation();
                    location.setY(198);
                    Block b = player.getWorld().getBlockAt(location);
                    Block b2 = player.getWorld().getBlockAt(location.add(0, 1, 0));

                    b2.setType(Material.AIR, false);
                    b.setType(Material.AIR, false);
                    location.setY(player.getWorld().getHighestBlockYAt(location) + 1);
                    player.teleportAsync(location).thenAccept(reason -> {
                        b.setType(Material.BARRIER, false);
                        b2.setType(Material.BARRIER, false);
                    });
                });
            }
        }, 0L, 2400L);
        // }), 0L, 5, TimeUnit.MINUTES);
        log("Initialized Auto arena reset.");
    }

    private void initMessages() {
        Bukkit.getScheduler().runTaskLater(this, Languages::init, 100);
        log("Initialized the Language.");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;

        econ = rsp.getProvider();
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (cmd.getName().equals("msg")) {
            if (args.length == 0) {
                player.sendMessage(translateo("&7You must specify who you want to message."));
                return true;
            } else if (args.length == 1) {
                player.sendMessage(translateo("&7You must specify a message to send to the player."));
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(translateo("&7You can't send messages to offline players."));
                return true;
            }

            String tn = target.getName();
            if (Practice.cc1.get("r." + tn + ".m") != null && !sender.hasPermission("has.staff")) {
                player.sendMessage(translateo("&7You can't send messages to this player since they've locked their messages."));
                return true;
            }

            StringBuilder msgargs = new StringBuilder();

            for (int i = 1; i < args.length; i++)
                msgargs.append(args[i]).append(" ");

            player.sendMessage(translate("&6[&cme &6-> &c" + target.getDisplayName() + "&6] &r" + msgargs));
            target.sendMessage(translate("&6[&c" + player.getDisplayName() + " &6-> &cme&6] &r" + msgargs));

            String pn = player.getName();
            lastReceived.put(player.getName(), tn);
            lastReceived.put(target.getName(), pn);
            return true;
        } else if (cmd.getName().equals("reply")) {
            if (args.length == 0) {
                player.sendMessage(translateo("&7You must specify a message to send to the player."));
                return true;
            }

            String pn = player.getName();
            if (!lastReceived.containsKey(pn)) {
                player.sendMessage(translateo("&7You have no one to reply to."));
                return true;
            }

            Player target = Bukkit.getPlayer(lastReceived.get(pn));
            if (target == null) {
                lastReceived.remove(pn);
                player.sendMessage(translateo("&7You have no one to reply to."));
                return true;
            }

            StringBuilder msgargs = new StringBuilder();
            for (String arg : args) msgargs.append(arg).append(" ");

            player.sendMessage(translate("&6[&cme &6-> &c" + target.getDisplayName() + "&6] &r" + msgargs));
            target.sendMessage(translate("&6[&c" + player.getDisplayName() + " &6-> &cme&6] &r" + msgargs));
            lastReceived.put(target.getName(), pn);
            return true;
        }

        return false;
    }
}