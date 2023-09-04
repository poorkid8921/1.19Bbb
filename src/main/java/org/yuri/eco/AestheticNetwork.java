package org.yuri.eco;

import common.commands.*;
import common.commands.tpa.TpaCommand;
import common.commands.tpa.TpacceptCommand;
import common.commands.tpa.TpahereCommand;
import common.commands.tpa.TpdenyCommand;
import io.papermc.lib.PaperLib;
import net.luckperms.api.LuckPerms;
import net.md_5.bungee.api.ChatColor;
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
import org.yuri.eco.commands.baltop;
import org.yuri.eco.commands.tpa.TpaAllCommand;
import org.yuri.eco.utils.Initializer;
import org.yuri.eco.utils.Utils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;

import static org.yuri.eco.utils.Utils.translateo;

@SuppressWarnings("deprecation")
public final class AestheticNetwork extends JavaPlugin implements CommandExecutor, TabExecutor {
    FileConfiguration config = getConfig();
    private File customConfigFile = new File(getDataFolder(), "data.yml");
    private FileConfiguration customConfigConfig = YamlConfiguration.loadConfiguration(customConfigFile);

    private static double[] linear(double from, double to, int max) {
        final double[] res = new double[max];
        for (int i = 0; i < max; i++) {
            res[i] = from + i * ((to - from) / (max - 1));
        }
        return res;
    }

    public static String rgbGradient(String str, Color from, Color to) {
        final double[] red = linear(from.getRed(), to.getRed(), str.length());
        final double[] green = linear(from.getGreen(), to.getGreen(), str.length());
        final double[] blue = linear(from.getBlue(), to.getBlue(), str.length());

        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < str.length(); i++) {
            builder.append(ChatColor.of(new Color((int) Math.round(red[i]), (int) Math.round(green[i]), (int) Math.round(blue[i])))).append(str.charAt(i));
        }

        return builder.toString();
    }

    public static double getTPSofLastSecond() {
        return Bukkit.getServer().getTPS()[0];
    }

    public void reloadCustomConfig() {
        if (customConfigFile == null) customConfigFile = new File(getDataFolder(), "data.yml");

        customConfigConfig = YamlConfiguration.loadConfiguration(customConfigFile);
    }

    public FileConfiguration getCustomConfig() {
        if (customConfigConfig == null) {
            this.reloadCustomConfig();
        }
        return customConfigConfig;
    }

    public void saveCustomConfig() {
        if (customConfigConfig == null || customConfigFile == null) {
            return;
        }
        try {
            getCustomConfig().save(customConfigFile);
        } catch (IOException ex) {
            this.getLogger().log(Level.SEVERE, "Could not save config to " + customConfigFile, ex);
        }
    }

    @Override
    public void onEnable() {
        Initializer.p = this;

        config.addDefault("Spawn.X", 0);
        config.addDefault("Spawn.Y", 300);
        config.addDefault("Spawn.Z", 0);
        config.options().copyDefaults(true);
        saveConfig();

        if (!customConfigFile.exists()) this.saveCustomConfig();

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            Initializer.lp = provider.getProvider();
            Bukkit.getPluginManager().registerEvents(new events(), this);
        }
        Bukkit.getPluginManager().registerEvents(new AnvilListener(), this);
        Objects.requireNonNull(this.getCommand("chatlock")).setExecutor(new ChatLock());

        //if (setupEconomy())
        //    Objects.requireNonNull(this.getCommand("pay")).setExecutor(new PayCommand(econ));

        Objects.requireNonNull(this.getCommand("tpa")).setExecutor(new TpaCommand());
        Objects.requireNonNull(this.getCommand("tpaall")).setExecutor(new TpaAllCommand());
        Objects.requireNonNull(this.getCommand("tpaccept")).setExecutor(new TpacceptCommand());
        Objects.requireNonNull(this.getCommand("tpahere")).setExecutor(new TpahereCommand());
        Objects.requireNonNull(this.getCommand("tpdeny")).setExecutor(new TpdenyCommand());

        Objects.requireNonNull(this.getCommand("tpa")).setTabCompleter(new TabTPA());
        Objects.requireNonNull(this.getCommand("tpahere")).setTabCompleter(new TabTPA());
        Objects.requireNonNull(this.getCommand("msg")).setTabCompleter(new TabMSG());

        Objects.requireNonNull(this.getCommand("spawn")).setExecutor(new spawn());
        Objects.requireNonNull(this.getCommand("discord")).setExecutor(new discord());
        Objects.requireNonNull(this.getCommand("report")).setExecutor(new Report(this));

        Objects.requireNonNull(this.getCommand("msglock")).setExecutor(new MsgLock());
        Objects.requireNonNull(this.getCommand("tpalock")).setExecutor(new TpaLock());
        Objects.requireNonNull(this.getCommand("ebaltop")).setExecutor(new baltop());

        Objects.requireNonNull(this.getCommand("stats")).setExecutor(new stats());

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "killall endercrystals world");
            Bukkit.getServer().getOnlinePlayers().stream().filter(s -> !s.isInsideVehicle() && !s.isGliding() && s.getWorld().getBlockAt(new Location(s.getWorld(), s.getLocation().getX(), 319, s.getLocation().getZ())).getType() == Material.BARRIER).forEach(player -> {
                Location loctp = new Location(player.getWorld(), player.getLocation().getX(), 135, player.getLocation().getZ());
                loctp.setPitch(player.getLocation().getPitch());
                loctp.setYaw(player.getLocation().getYaw());
                PaperLib.teleportAsync(player, loctp);
            });
            //Bukkit.broadcastMessage(rgbGradient("ᴛʜᴇ ᴀʀᴇɴᴀ ʜᴀꜱ ʙᴇᴇɴ ʀᴇꜱᴇᴛ", new Color(229, 45, 39), new Color(179, 18, 23)));
        }, 0L, 12005L);
        //Bukkit.broadcastMessage(rgbGradient("ᴛʜᴇ ᴀʀᴇɴᴀ ʜᴀꜱ ʙᴇᴇɴ ʀᴇꜱᴇᴛ", new Color(229, 45, 39), new Color(179, 18, 23)));
        //Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, events::spawnLootdrop, 0L, this.getConfig().getInt("ArenaResetTimeInTicks")/2);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "hcscr:haram");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (cmd.getName().equals("msg")) {
            if (args.length == 0) {
                player.sendMessage(translateo("&7You must specify who you want to message"));
                return true;
            } else if (args.length == 1) {
                player.sendMessage(translateo("&7You must specify a message to send to the player"));
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(translateo("&7You can't send messages to offline players"));
                return true;
            }

            if (Utils.manager().get("r." + target.getUniqueId() + ".m") != null && !sender.hasPermission("has.staff")) {
                player.sendMessage(translateo("&7You can't send messages to this player since they've locked their messages"));
                return true;
            }

            StringBuilder msgargs = new StringBuilder();

            for (int i = 1; i < args.length; i++)
                msgargs.append(args[i]).append(" ");

            player.sendMessage(Utils.translate("&6[&cme &6-> &c" + target.getDisplayName() + "&6] &r" + msgargs));
            target.sendMessage(Utils.translate("&6[&c" + player.getDisplayName() + " &6-> &cme&6] &r" + msgargs));
            Initializer.lastReceived.put(player.getUniqueId(), target.getUniqueId());
            Initializer.lastReceived.put(target.getUniqueId(), player.getUniqueId());
            return true;
        } else if (cmd.getName().equals("reply")) {
            if (args.length == 0) {
                player.sendMessage(translateo("&7You must specify a message to send to the player"));
                return true;
            }

            if (!Initializer.lastReceived.containsKey(player.getUniqueId()) || Initializer.lastReceived.get(player.getUniqueId()) == null) {
                player.sendMessage(translateo("&7You have no one to reply to"));
                return true;
            }

            Player target = Bukkit.getPlayer(Initializer.lastReceived.get(player.getUniqueId()));
            if (target == null) {
                player.sendMessage(translateo("&7You have no one to reply to"));
                return true;
            }

            StringBuilder msgargs = new StringBuilder();
            for (String arg : args) msgargs.append(arg).append(" ");

            player.sendMessage(Utils.translate("&6[&cme &6-> &c" + target.getDisplayName() + "&6] &r" + msgargs));
            target.sendMessage(Utils.translate("&6[&c" + player.getDisplayName() + " &6-> &cme&6] &r" + msgargs));
            Initializer.lastReceived.put(target.getUniqueId(), player.getUniqueId());
            return true;
        }

        return false;
    }
}