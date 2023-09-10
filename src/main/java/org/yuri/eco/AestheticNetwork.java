package org.yuri.eco;

import common.commands.*;
import common.commands.tpa.TpaCommand;
import common.commands.tpa.TpacceptCommand;
import common.commands.tpa.TpahereCommand;
import common.commands.tpa.TpdenyCommand;
import io.papermc.lib.PaperLib;
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
import org.jetbrains.annotations.NotNull;
import org.yuri.eco.commands.ChatLock;
import org.yuri.eco.commands.tpa.TpaAllCommand;
import org.yuri.eco.utils.Initializer;
import org.yuri.eco.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;

import static org.yuri.eco.utils.Utils.translateo;

@SuppressWarnings("deprecation")
public final class AestheticNetwork extends JavaPlugin implements CommandExecutor, TabExecutor {
    FileConfiguration config = getConfig();
    private File cf = new File(getDataFolder(), "data.yml");
    private FileConfiguration cc = YamlConfiguration.loadConfiguration(cf);

    public void reloadCustomConfig() {
        if (cf == null) cf = new File(getDataFolder(), "data.yml");

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

    @Override
    public void onEnable() {
        Initializer.p = this;
        saveConfig();

        if (cc == null) this.reloadCustomConfig();
        if (!cf.exists()) this.saveCustomConfig();

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            Initializer.lp = provider.getProvider();
            Bukkit.getPluginManager().registerEvents(new events(), this);
        }
        Bukkit.getPluginManager().registerEvents(new AnvilListener(), this);
        Objects.requireNonNull(this.getCommand("chatlock")).setExecutor(new ChatLock());

        Objects.requireNonNull(this.getCommand("tpa")).setExecutor(new TpaCommand());
        Objects.requireNonNull(this.getCommand("tpaall")).setExecutor(new TpaAllCommand());
        Objects.requireNonNull(this.getCommand("tpaccept")).setExecutor(new TpacceptCommand());
        Objects.requireNonNull(this.getCommand("tpahere")).setExecutor(new TpahereCommand());
        Objects.requireNonNull(this.getCommand("tpdeny")).setExecutor(new TpdenyCommand());

        Objects.requireNonNull(this.getCommand("tpa")).setTabCompleter(new TabTPA());
        Objects.requireNonNull(this.getCommand("tpaccept")).setTabCompleter(new TabTPA());
        Objects.requireNonNull(this.getCommand("tpahere")).setTabCompleter(new TabTPA());
        Objects.requireNonNull(this.getCommand("msg")).setTabCompleter(new TabMSG());

        Objects.requireNonNull(this.getCommand("spawn")).setExecutor(new spawn());
        Objects.requireNonNull(this.getCommand("discord")).setExecutor(new discord());
        Objects.requireNonNull(this.getCommand("report")).setExecutor(new Report(this));

        Objects.requireNonNull(this.getCommand("msglock")).setExecutor(new MsgLock());
        Objects.requireNonNull(this.getCommand("tpalock")).setExecutor(new TpaLock());

        Objects.requireNonNull(this.getCommand("stats")).setExecutor(new stats());

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "killall endercrystals world");
            Bukkit.getServer().getOnlinePlayers().stream().filter(s -> !s.isInsideVehicle() && !s.isGliding() && s.getWorld().getBlockAt(new Location(s.getWorld(), s.getLocation().getX(), 319, s.getLocation().getZ())).getType() == Material.BARRIER).forEach(player -> {
                Location loctp = new Location(player.getWorld(), player.getLocation().getX(), 135, player.getLocation().getZ());
                loctp.setPitch(player.getLocation().getPitch());
                loctp.setYaw(player.getLocation().getYaw());
                PaperLib.teleportAsync(player, loctp);
            });
        }, 0L, 24005L);
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
            if (Utils.manager().get("r." + tn + ".m") != null &&
                    !sender.hasPermission("has.staff")) {
                player.sendMessage(translateo("&7You can't send messages to this player since they've locked their messages."));
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
                player.sendMessage(translateo("&7You must specify a message to send to the player."));
                return true;
            }

            String pn = player.getName();
            if (!Initializer.lastReceived.containsKey(pn)) {
                player.sendMessage(translateo("&7You have no one to reply to."));
                return true;
            }

            Player target = Bukkit.getPlayer(Initializer.lastReceived.get(pn));
            if (target == null) {
                Initializer.lastReceived.remove(pn);
                player.sendMessage(translateo("&7You have no one to reply to."));
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