package org.yuri.aestheticnetwork;

import io.papermc.lib.PaperLib;
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
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.yuri.aestheticnetwork.commands.*;
import org.yuri.aestheticnetwork.expansions.ExpansionManager;
import org.yuri.aestheticnetwork.commands.duel.*;
import org.yuri.aestheticnetwork.commands.essentialsx.back;
import org.yuri.aestheticnetwork.commands.ffa;
import org.yuri.aestheticnetwork.commands.flat;
import org.yuri.aestheticnetwork.commands.essentialsx.help;
import org.yuri.aestheticnetwork.commands.tpa.TpaCommand;
import org.yuri.aestheticnetwork.commands.tpa.TpacceptCommand;
import org.yuri.aestheticnetwork.commands.tpa.TpahereCommand;
import org.yuri.aestheticnetwork.commands.tpa.TpdenyCommand;
import org.yuri.aestheticnetwork.utils.Messages.Languages;
import org.yuri.aestheticnetwork.utils.TabTPA;
import org.yuri.aestheticnetwork.utils.Utils;
import org.yuri.aestheticnetwork.utils.placeholders;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.yuri.aestheticnetwork.utils.Messages.Initializer.*;
import static org.yuri.aestheticnetwork.utils.Utils.translate;
import static org.yuri.aestheticnetwork.utils.Utils.translateo;

@SuppressWarnings("deprecation")
public final class AestheticNetwork extends JavaPlugin implements TabExecutor {
    public FileConfiguration config = getConfig();
    public File df = getDataFolder();
    private final File cf = new File(df, "data.yml");
    private final File cf1 = new File(df, "other.yml");

    public static Logger log = Bukkit.getLogger();

    private FileConfiguration cc = YamlConfiguration.loadConfiguration(cf);
    private FileConfiguration cc1 = YamlConfiguration.loadConfiguration(cf1);

    public static AestheticNetwork getInstance() {
        return p;
    }

    public void reloadCustomConfig() {
        cc = YamlConfiguration.loadConfiguration(cf);
    }

    public FileConfiguration getCustomConfig() {
        return cc;
    }

    public void saveCustomConfig() {
        try {
            getCustomConfig().save(cf);
        } catch (IOException ex) {
            this.getLogger().log(Level.SEVERE, "Could not save config to " + cf, ex);
        }
    }

    public void reloadCustomConfig1() {
        cc1 = YamlConfiguration.loadConfiguration(cf1);
    }

    public FileConfiguration getCustomConfig1() {
        return cc1;
    }

    public void saveCustomConfig1() {
        try {
            getCustomConfig1().save(cf1);
        } catch (IOException ex) {
            this.getLogger().log(Level.SEVERE, "Could not save config to " + cf1, ex);
        }
    }

    @Override
    public void onEnable() {
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
        new placeholders(this).register();

        // EXPANSIONS
        initExpansions(a);
    }

    private void initExpansions(long a) {
        ExpansionManager.initKits();
        log("Initialization Done! It took: " + (System.currentTimeMillis() - a) + " in order to finish.");
    }

    private void initMisc() {
        Objects.requireNonNull(this.getCommand("msg")).setTabCompleter(new TabMSG());

        Objects.requireNonNull(this.getCommand("report")).setExecutor(new Report(this));
        Objects.requireNonNull(this.getCommand("shop")).setExecutor(new Shop());
        Objects.requireNonNull(this.getCommand("discord")).setExecutor(new discord());

        Objects.requireNonNull(this.getCommand("event")).setExecutor(new Event());
        Objects.requireNonNull(this.getCommand("back")).setExecutor(new back());

        Objects.requireNonNull(this.getCommand("help")).setExecutor(new help());
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
        Objects.requireNonNull(this.getCommand("msg")).setTabCompleter(new TabMSG());

        Objects.requireNonNull(this.getCommand("duel")).setExecutor(new Duel());
        Objects.requireNonNull(this.getCommand("duelaccept")).setExecutor(new DuelAccept());
        Objects.requireNonNull(this.getCommand("dueldeny")).setExecutor(new DuelDeny());

        Objects.requireNonNull(this.getCommand("spawn")).setExecutor(new Spawn());
        Objects.requireNonNull(this.getCommand("ffa")).setExecutor(new ffa());
        Objects.requireNonNull(this.getCommand("flat")).setExecutor(new flat());

        Objects.requireNonNull(this.getCommand("msglock")).setExecutor(new MsgLock());
        Objects.requireNonNull(this.getCommand("tpalock")).setExecutor(new TpaLock());
        log("Initialized Main commands.");
    }

    private void setupConfig() {
        config.options().copyDefaults(true);
        saveConfig();

        if (cc == null) this.reloadCustomConfig();
        if (cc1 == null) this.reloadCustomConfig1();

        if (!cf.exists()) this.saveCustomConfig();
        if (!cf1.exists()) this.saveCustomConfig1();
    }

    public static void log(String a) {
        log.log(Level.FINE, a);
    }

    private void initReset() {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
                    if (Bukkit.getServer().getOnlinePlayers().size() > 0) {
                        Languages.MOTD.forEach(Bukkit::broadcastMessage);
                    }
            if (ffaconst.isEmpty()) return;

            ffaconst.stream().filter(s -> !s.isInsideVehicle() && !s.isGliding()).forEach(player -> {
                Location location = new Location(player.getWorld(), player.getLocation().getX(), 198, player.getLocation().getZ());
                Block b = player.getWorld().getBlockAt(location);
                Block b2 = player.getWorld().getBlockAt(location.add(new Vector(0, 1, 0)));

                b2.setType(Material.AIR, false);
                b.setType(Material.AIR, false);
                PaperLib.teleportAsync(player, new Location(location.getWorld(), location.getX(), player.getWorld().getHighestBlockYAt(location) + 1, location.getZ(), player.getLocation().getYaw(), player.getLocation().getPitch())).thenAccept(reason -> {
                    b.setType(Material.BARRIER, false);
                    b2.setType(Material.BARRIER, false);
                });
            });
        }, 0L, 6005L);
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
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
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
            if (Utils.manager1().get("r." + tn + ".m") != null && !sender.hasPermission("has.staff")) {
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