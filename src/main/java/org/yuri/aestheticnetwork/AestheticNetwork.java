package org.yuri.aestheticnetwork;

import io.papermc.lib.PaperLib;
import me.clip.placeholderapi.PlaceholderAPI;
import net.luckperms.api.LuckPerms;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.*;
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
import org.yuri.aestheticnetwork.commands.*;
import org.yuri.aestheticnetwork.commands.tpa.*;
import org.yuri.aestheticnetwork.utils.Initializer;
import org.yuri.aestheticnetwork.utils.Utils;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

import static org.yuri.aestheticnetwork.utils.Initializer.*;
import static org.yuri.aestheticnetwork.utils.Utils.translate;

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
            builder.append(ChatColor.of(new Color(
                            (int) Math.round(red[i]),
                            (int) Math.round(green[i]),
                            (int) Math.round(blue[i]))))
                    .append(str.charAt(i));
        }

        return builder.toString();
    }

    public void reloadCustomConfig() {
        if (customConfigFile == null)
            customConfigFile = new File(getDataFolder(), "data.yml");

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

    public static PlaceholderAPI papi;
    private static Permission perms;
    private static Chat chat;

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null)
            return false;
        perms = rsp.getProvider();
        return true;
    }

    @Override
    public void onEnable() {
        Initializer.p = this;

        config.addDefault("placeholders.discordlink", "https://discord.gg/aestheticnetwork");
        config.addDefault("discordmessage", "&7You can join our discord server using &7%discordlink%&7!");
        config.addDefault("ShouldTeleportPlayerToSpawn", true);
        config.addDefault("ShouldAllowEndPortal", true);
        config.addDefault("Spawn.X", 0);
        config.addDefault("Spawn.Y", 300);
        config.addDefault("Spawn.Z", 0);
        config.addDefault("Spawn.World", "world");
        config.addDefault("Arena", false);
        config.addDefault("ArenaResetTimeInTicks", 26000);
        config.addDefault("ArenaName", "pvpline");
        config.options().copyDefaults(true);
        saveConfig();

        if (!customConfigFile.exists())
            this.saveCustomConfig();

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null && setupPermissions()) {
            lp = provider.getProvider();
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

        if (config.getBoolean("Arena")) {
            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "killall endercrystals world");

                Bukkit.getServer().getOnlinePlayers().stream().filter(s -> !s.isInsideVehicle() &&
                        !s.isGliding() &&
                        s.getWorld().getBlockAt(new Location(s.getWorld(), s.getLocation().getX(), 319, s.getLocation().getZ())).getType() == Material.BARRIER).forEach(player -> {
                    Location loctp = new Location(player.getWorld(), player.getLocation().getX(), 135, player.getLocation().getZ());
                    loctp.setPitch(player.getLocation().getPitch());
                    loctp.setYaw(player.getLocation().getYaw());
                    PaperLib.teleportAsync(player, loctp);
                });
                Bukkit.broadcastMessage(rgbGradient("ᴛʜᴇ ᴀʀᴇɴᴀ ʜᴀꜱ ʙᴇᴇɴ ʀᴇꜱᴇᴛ", new Color(229, 45, 39), new Color(179, 18, 23)));
            }, 0L, 2405L);
            //Bukkit.broadcastMessage(rgbGradient("ᴛʜᴇ ᴀʀᴇɴᴀ ʜᴀꜱ ʙᴇᴇɴ ʀᴇꜱᴇᴛ", new Color(229, 45, 39), new Color(179, 18, 23)));
            //Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, events::spawnLootdrop, 0L, this.getConfig().getInt("ArenaResetTimeInTicks")/2);
        }
        getServer().getMessenger().registerOutgoingPluginChannel(this, "hcscr:haram");
    }

    public static AestheticNetwork getInstance() {
        return p;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player))
            return true;

        if (cmd.getName().equals("msg")) {
            if (args.length == 0) {
                player.sendMessage(translate("&7You must specify who you want to message."));
                return true;
            } else if (args.length == 1) {
                player.sendMessage(translate("&7You must specify a message to send to the player"));
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(translate("&7You can't send messages to offline players!"));
                return true;
            }

            if (Utils.manager().get(
                    "r." + target.getUniqueId() + ".m") != null &&
            !sender.hasPermission("has.staff"))
            {
                player.sendMessage(translate("&7You can't send messages to this player since they locked his messages."));
                return true;
            }

            StringBuilder msgargs = new StringBuilder();

            for (int i = 1; i < args.length; i++)
                msgargs.append(args[i]).append(" ");

            player.sendMessage(translate("&6[&cme &6-> &c" + target.getDisplayName() + "&6] &r" + msgargs));
            target.sendMessage(translate("&6[&c" + player.getDisplayName() + " &6-> &cme&6] &r" + msgargs));
            lastReceived.put(player.getUniqueId(), target.getUniqueId());
            lastReceived.put(target.getUniqueId(), player.getUniqueId());
            return true;
        } else if (cmd.getName().equals("reply")) {
            if (args.length == 0) {
                player.sendMessage(translate("&7You must specify a message to send to the player"));
                return true;
            }

            if (!lastReceived.containsKey(player.getUniqueId()) || lastReceived.get(player.getUniqueId()) == null)
            {
                player.sendMessage(translate("&7You have no one to reply to!"));
                return true;
            }

            Player target = Bukkit.getPlayer(lastReceived.get(player.getUniqueId()));
            if (target == null) {
                player.sendMessage(translate("&7You have no one to reply to!"));
                return true;
            }

            StringBuilder msgargs = new StringBuilder();
            for (String arg : args) msgargs.append(arg).append(" ");

            player.sendMessage(translate("&6[&cme &6-> &c" + target.getDisplayName() + "&6] &r" + msgargs));
            target.sendMessage(translate("&6[&c" + player.getDisplayName() + " &6-> &cme&6] &r" + msgargs));
            lastReceived.put(target.getUniqueId(), player.getUniqueId());
            return true;
        }

        return false;
    }

    public static double getTPSofLastSecond() {
        return Bukkit.getServer().getTPS()[0];
    }
}