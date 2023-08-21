package org.yuri.aestheticnetwork;

import io.papermc.lib.PaperLib;
import net.luckperms.api.LuckPerms;
import net.md_5.bungee.api.ChatColor;
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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.yuri.aestheticnetwork.commands.*;
import org.yuri.aestheticnetwork.commands.duel.Event;
import org.yuri.aestheticnetwork.commands.ffa;
import org.yuri.aestheticnetwork.commands.flat;
import org.yuri.aestheticnetwork.commands.duel.*;
import org.yuri.aestheticnetwork.commands.tpa.TpaCommand;
import org.yuri.aestheticnetwork.commands.tpa.TpacceptCommand;
import org.yuri.aestheticnetwork.commands.tpa.TpahereCommand;
import org.yuri.aestheticnetwork.commands.tpa.TpdenyCommand;
import org.yuri.aestheticnetwork.utils.Utils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

import static org.yuri.aestheticnetwork.utils.Initializer.*;
import static org.yuri.aestheticnetwork.utils.Utils.translate;
import static org.yuri.aestheticnetwork.utils.Utils.translateo;

public final class AestheticNetwork extends JavaPlugin implements TabExecutor {
    public FileConfiguration config = getConfig();
    private File customConfigFile = new File(getDataFolder(), "data.yml");
    private File customConfigFile1 = new File(getDataFolder(), "other.yml");
    private FileConfiguration customConfigConfig = YamlConfiguration.loadConfiguration(customConfigFile);
    private FileConfiguration customConfigConfig1 = YamlConfiguration.loadConfiguration(customConfigFile1);

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

    public static AestheticNetwork getInstance() {
        return p;
    }

    public void reloadCustomConfig() {
        if (customConfigFile == null)
            customConfigFile = new File(getDataFolder(), "data.yml");

        customConfigConfig = YamlConfiguration.loadConfiguration(customConfigFile);
    }

    public FileConfiguration getCustomConfig() {
        if (customConfigConfig == null)
            this.reloadCustomConfig();

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

    public void reloadCustomConfig1() {
        if (customConfigFile1 == null)
            customConfigFile1 = new File(getDataFolder(), "other.yml");

        customConfigConfig1 = YamlConfiguration.loadConfiguration(customConfigFile1);
    }

    public FileConfiguration getCustomConfig1() {
        if (customConfigConfig1 == null)
            this.reloadCustomConfig1();

        return customConfigConfig1;
    }

    public void saveCustomConfig1() {
        if (customConfigConfig1 == null || customConfigFile1 == null) {
            return;
        }
        try {
            getCustomConfig1().save(customConfigFile1);
        } catch (IOException ex) {
            this.getLogger().log(Level.SEVERE, "Could not save config to " + customConfigFile1, ex);
        }
    }

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            this.setEnabled(false);
        }

        p = this;

        config.options().copyDefaults(true);
        saveConfig();

        if (!customConfigFile.exists())
            this.saveCustomConfig();

        if (!customConfigFile1.exists())
            this.saveCustomConfig1();

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            lp = provider.getProvider();
            Bukkit.getPluginManager().registerEvents(new events(), this);
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
            new placeholders(this).register();

        Objects.requireNonNull(this.getCommand("tpa")).setExecutor(new TpaCommand());
        Objects.requireNonNull(this.getCommand("tpaccept")).setExecutor(new TpacceptCommand());
        Objects.requireNonNull(this.getCommand("tpahere")).setExecutor(new TpahereCommand());
        Objects.requireNonNull(this.getCommand("tpdeny")).setExecutor(new TpdenyCommand());

        Objects.requireNonNull(this.getCommand("tpa")).setTabCompleter(new TabTPA());
        Objects.requireNonNull(this.getCommand("tpahere")).setTabCompleter(new TabTPA());
        Objects.requireNonNull(this.getCommand("msg")).setTabCompleter(new TabMSG());

        Objects.requireNonNull(this.getCommand("report")).setExecutor(new Report(this));
        Objects.requireNonNull(this.getCommand("shop")).setExecutor(new Shop());
        Objects.requireNonNull(this.getCommand("spawn")).setExecutor(new Spawn());
        Objects.requireNonNull(this.getCommand("discord")).setExecutor(new discord());
        Objects.requireNonNull(this.getCommand("ffa")).setExecutor(new ffa());
        Objects.requireNonNull(this.getCommand("flat")).setExecutor(new flat());
        Objects.requireNonNull(this.getCommand("flatlegacy")).setExecutor(new flatlegacy());
        Objects.requireNonNull(this.getCommand("nethpot")).setExecutor(new Nethpot());

        Objects.requireNonNull(this.getCommand("msglock")).setExecutor(new MsgLock());
        Objects.requireNonNull(this.getCommand("tpalock")).setExecutor(new TpaLock());

        Objects.requireNonNull(this.getCommand("duel")).setExecutor(new Duel());
        Objects.requireNonNull(this.getCommand("duelaccept")).setExecutor(new DuelAccept());
        Objects.requireNonNull(this.getCommand("dueldeny")).setExecutor(new DuelDeny());

        Objects.requireNonNull(this.getCommand("event")).setExecutor(new Event());
        getServer().getMessenger().registerOutgoingPluginChannel(this, "hcscr:haram");

        // populating tinylist's
        color.addAll(List.of(org.bukkit.Color.LIME,
                org.bukkit.Color.ORANGE,
                org.bukkit.Color.RED,
                org.bukkit.Color.BLUE,
                org.bukkit.Color.OLIVE,
                org.bukkit.Color.PURPLE,
                org.bukkit.Color.WHITE,
                org.bukkit.Color.AQUA,
                org.bukkit.Color.BLACK,
                org.bukkit.Color.FUCHSIA,
                org.bukkit.Color.GRAY,
                org.bukkit.Color.GREEN,
                org.bukkit.Color.MAROON,
                org.bukkit.Color.NAVY,
                org.bukkit.Color.SILVER,
                org.bukkit.Color.TEAL,
                org.bukkit.Color.YELLOW));
        entities.addAll(List.of(EntityType.THROWN_EXP_BOTTLE,
                EntityType.PLAYER,
                EntityType.SPLASH_POTION,
                EntityType.LIGHTNING,
                EntityType.ARROW,
                EntityType.DROPPED_ITEM,
                EntityType.ENDER_CRYSTAL,
                EntityType.FALLING_BLOCK,
                EntityType.EXPERIENCE_ORB,
                EntityType.ARMOR_STAND,
                EntityType.ENDER_PEARL,
                EntityType.FIREWORK,
                EntityType.FISHING_HOOK));

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            Bukkit.getServer().getLogger().info("Current players in ffa: " + ffaconst.size());

            if (ffastr == 3)
                ffastr = 1;

            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "arena reset ffa" + ffastr + " veryfast -silent");

            if (!hasReset) {
                if (flatstr == 14)
                    flatstr = 1;
                if (nethstr == 5)
                    nethstr = 1;

                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "arena reset flat" + flatstr + " veryfast -silent");
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "arena reset nethpot_" + nethstr + " veryfast -silent");
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "arena reset flatl" + flatstr + " veryfast -silent");
                flatstr++;
                nethstr++;
            }

            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "arena reset flat veryfast -silent");
            //Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "arena reset underwater veryfast -silent");
            hasReset = !hasReset;

            /*for (int i = 1; i <= 4; i++) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                        "arena reset ffa_reg" + i + " extreme -silent");
            }*/
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                    "arena reset ffa_arena veryfast -silent");

            ffastr++;

            if (ffaconst.isEmpty())
                return;

            Bukkit.getScheduler().scheduleSyncDelayedTask(this, () ->
                    ffaconst.stream().filter(s -> !s.isInsideVehicle() &&
                            !s.isGliding()).forEach(player -> {
                        Location location = new Location(
                                player.getWorld(),
                                player.getLocation().getX(),
                                198,
                                player.getLocation().getZ());
                        Block b = player.getWorld().getBlockAt(location);
                        Block b2 = player.getWorld().getBlockAt(location.add(new Vector(0, 1, 0)));

                        b2.setType(Material.AIR, false);
                        b.setType(Material.AIR, false);
                        PaperLib.teleportAsync(player, new Location(
                                location.getWorld(),
                                location.getX(),
                                player.getWorld().getHighestBlockYAt(location) + 1,
                                location.getZ(),
                                player.getLocation().getYaw(),
                                player.getLocation().getPitch())).thenAccept(reason -> {
                            b.setType(Material.BARRIER, false);
                            b2.setType(Material.BARRIER, false);
                        });
                    }), 5L);
        }, 0L, 5000L);

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            ffa = new Location(Bukkit.getWorld("world"),
                    getConfig().getDouble("ffa.X"),
                    getConfig().getDouble("ffa.Y"),
                    getConfig().getDouble("ffa.Z"));
            flat = new Location(Bukkit.getWorld("world"),
                    getConfig().getDouble("flat.X"),
                    getConfig().getDouble("flat.Y"),
                    getConfig().getDouble("flat.Z"));
            lflat = new Location(Bukkit.getWorld("world"),
                    getConfig().getDouble("legacyflat.X"),
                    getConfig().getDouble("legacyflat.Y"),
                    getConfig().getDouble("legacyflat.Z"));
            nethpot = new Location(Bukkit.getWorld("world"),
                    getConfig().getDouble("nethpot.X"),
                    getConfig().getDouble("nethpot.Y"),
                    getConfig().getDouble("nethpot.Z"));
            spawn = new Location(Bukkit.getWorld("world"),
                    getConfig().getDouble("Spawn.X"),
                    getConfig().getDouble("Spawn.Y"),
                    getConfig().getDouble("Spawn.Z"));
            spawn.setYaw(getConfig().getLong("Spawn.yaw"));
        }, 100);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null)
            return false;

        econ = rsp.getProvider();
        return true;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player))
            return true;

        if (cmd.getName().equals("msg")) {
            if (args.length == 0) {
                player.sendMessage(translateo("&7You must specify who you want to message."));
                return true;
            } else if (args.length == 1) {
                player.sendMessage(translateo("&7You must specify a message to send to the player"));
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(translateo("&7You can't send messages to offline players!"));
                return true;
            }

            if (Utils.manager1().get(
                    "r." + target.getUniqueId() + ".m") != null &&
                    !sender.hasPermission("has.staff")) {
                player.sendMessage(translateo("&7You can't send messages to this player since he locked his messages."));
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
                player.sendMessage(translateo("&7You must specify a message to send to the player"));
                return true;
            }

            if (!lastReceived.containsKey(player.getUniqueId()) || lastReceived.get(player.getUniqueId()) == null) {
                player.sendMessage(translateo("&7You have no one to reply to!"));
                return true;
            }

            Player target = Bukkit.getPlayer(lastReceived.get(player.getUniqueId()));
            if (target == null) {
                player.sendMessage(translateo("&7You have no one to reply to!"));
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
}