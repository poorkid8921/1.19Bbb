package main;

import commands.*;
import io.papermc.lib.PaperLib;
import main.utils.Initializer;
import main.utils.Languages;
import main.utils.Utils;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static main.utils.Initializer.economy;

@SuppressWarnings("deprecation")
public class Economy extends JavaPlugin implements CommandExecutor, TabExecutor {
    private final File cf = new File(getDataFolder(), "data.yml");
    private FileConfiguration cc = YamlConfiguration.loadConfiguration(cf);

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

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
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

        Initializer.p = this;
        saveConfig();

        if (cc == null) this.reloadCustomConfig();
        if (!cf.exists()) this.saveCustomConfig();

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) Initializer.lp = provider.getProvider();
        Objects.requireNonNull(this.getCommand("chatlock")).setExecutor(new ChatLock());

        Objects.requireNonNull(this.getCommand("tpa")).setExecutor(new Tpa());
        Objects.requireNonNull(this.getCommand("tpaall")).setExecutor(new TpaAll());
        Objects.requireNonNull(this.getCommand("tpaccept")).setExecutor(new Tpaccept());
        Objects.requireNonNull(this.getCommand("tpahere")).setExecutor(new Tpahere());
        Objects.requireNonNull(this.getCommand("tpdeny")).setExecutor(new TpDeny());

        Objects.requireNonNull(this.getCommand("tpa")).setTabCompleter(new TabTPA());
        Objects.requireNonNull(this.getCommand("tpaccept")).setTabCompleter(new TabTPA());
        Objects.requireNonNull(this.getCommand("tpahere")).setTabCompleter(new TabTPA());
        Objects.requireNonNull(this.getCommand("msg")).setTabCompleter(new TabMSG());

        Objects.requireNonNull(this.getCommand("spawn")).setExecutor(new Spawn());
        Objects.requireNonNull(this.getCommand("discord")).setExecutor(new Discord());
        Objects.requireNonNull(this.getCommand("report")).setExecutor(new Report(this));

        Objects.requireNonNull(this.getCommand("msglock")).setExecutor(new MsgLock());
        Objects.requireNonNull(this.getCommand("tpalock")).setExecutor(new TpaLock());

        Objects.requireNonNull(this.getCommand("stats")).setExecutor(new Stats());
        Objects.requireNonNull(this.getCommand("purge")).setExecutor(new Purge());

        Initializer.EXECUTOR.scheduleAtFixedRate(() ->
                Bukkit.getScheduler().runTask(this, () ->
                        Bukkit.getServer().getOnlinePlayers().stream().filter(s -> !s.isInsideVehicle() && !s.isGliding() && s.getWorld().getBlockAt(new Location(s.getWorld(), s.getLocation().getX(), 319, s.getLocation().getZ())).getType() == Material.BARRIER).forEach(player -> {
                            Location l = player.getLocation();
                            PaperLib.teleportAsync(player, new Location(player.getWorld(), l.getX(), 135, l.getZ(), l.getYaw(), l.getPitch()));
                        })), 0, 20, TimeUnit.MINUTES);

        Bukkit.getServer().getScheduler().runTaskLater(this, Languages::init, 100L);
        Bukkit.getPluginManager().registerEvents(new Events(), this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (cmd.getName().equals("msg")) {
            if (args.length == 0) {
                player.sendMessage(Utils.translateo("&7You must specify who you want to message."));
                return true;
            } else if (args.length == 1) {
                player.sendMessage(Utils.translateo("&7You must specify a message to send to the player."));
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(Utils.translateo("&7You can't send messages to offline players."));
                return true;
            }

            String tn = target.getName();
            if (Utils.manager().get("r." + tn + ".m") != null && !sender.hasPermission("has.staff")) {
                player.sendMessage(Utils.translateo("&7You can't send messages to this player since they've locked their messages."));
                return true;
            }

            StringBuilder msgargs = new StringBuilder();

            for (int i = 1; i < args.length; i++)
                msgargs.append(args[i]).append(" ");

            player.sendMessage(Utils.translate("&6[&cme &6-> &c" + target.getDisplayName() + "&6] &r" + msgargs));
            target.sendMessage(Utils.translate("&6[&c" + player.getDisplayName() + " &6-> &cme&6] &r" + msgargs));
            String pn = player.getName();
            Initializer.lastReceived.put(pn, tn);
            Initializer.lastReceived.put(tn, pn);
            return true;
        } else if (cmd.getName().equals("reply")) {
            if (args.length == 0) {
                player.sendMessage(Utils.translateo("&7You must specify a message to send to the player."));
                return true;
            }

            String pn = player.getName();
            if (!Initializer.lastReceived.containsKey(pn)) {
                player.sendMessage(Utils.translateo("&7You have no one to reply to."));
                return true;
            }

            Player target = Bukkit.getPlayer(Initializer.lastReceived.get(pn));
            if (target == null) {
                Initializer.lastReceived.remove(pn);
                player.sendMessage(Utils.translateo("&7You have no one to reply to."));
                return true;
            }

            StringBuilder msgargs = new StringBuilder();
            for (String arg : args) msgargs.append(arg).append(" ");

            player.sendMessage(Utils.translate("&6[&cme &6-> &c" + target.getDisplayName() + "&6] &r" + msgargs));
            target.sendMessage(Utils.translate("&6[&c" + player.getDisplayName() + " &6-> &cme&6] &r" + msgargs));
            String tn = target.getName();
            Initializer.lastReceived.put(tn, pn);
            return true;
        }

        return false;
    }
}