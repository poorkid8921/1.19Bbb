package bab.bbb;

import bab.bbb.Commands.DelHomeCommand;
import bab.bbb.Commands.Discord;
import bab.bbb.Commands.HomeCommand;
import bab.bbb.Commands.SetHomeCommand;
import bab.bbb.Events.DupeEvent;
import bab.bbb.Events.Dupes.FrameDupe;
import bab.bbb.Events.misc.*;
import bab.bbb.Events.misc.patches.AntiBurrow;
import bab.bbb.Events.misc.patches.AntiPacketElytraFly;
import bab.bbb.Events.misc.patches.ChestLimit;
import bab.bbb.tpa.TpaCommand;
import bab.bbb.tpa.TpacceptCommand;
import bab.bbb.tpa.TpahereCommand;
import bab.bbb.tpa.TpdenyCommand;
import bab.bbb.utils.Tablist;
import bab.bbb.utils.Utils;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

import static bab.bbb.utils.Utils.translate;

@SuppressWarnings("deprecation")
public final class Bbb extends JavaPlugin implements CommandExecutor, TabExecutor {
    public FileConfiguration config = this.getConfig();
    private File customConfigFile = new File(getDataFolder(), "data.yml");
    private FileConfiguration customConfigConfig = YamlConfiguration.loadConfiguration(customConfigFile);
    public static HashMap<UUID, UUID> lastReceived = new HashMap<>();
    private static Bbb instance;

    public void register() {
        Bukkit.getPluginManager().registerEvents(new MiscEvents(), this);
        Bukkit.getPluginManager().registerEvents(new DupeEvent(), this);
        Bukkit.getPluginManager().registerEvents(new MoveEvents(), this);
        Bukkit.getPluginManager().registerEvents(new AnvilListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChestLimit(), this);

        Bukkit.getPluginManager().registerEvents(new BetterChat(), this);
        Bukkit.getPluginManager().registerEvents(new FrameDupe(), this);
        Bukkit.getPluginManager().registerEvents(new AntiPacketElytraFly(), this);
        Bukkit.getPluginManager().registerEvents(new AntiBurrow(), this);

        Objects.requireNonNull(this.getCommand("discord")).setExecutor(new Discord());

        Objects.requireNonNull(this.getCommand("tpa")).setExecutor(new TpaCommand());
        Objects.requireNonNull(this.getCommand("tpaccept")).setExecutor(new TpacceptCommand());
        Objects.requireNonNull(this.getCommand("tpahere")).setExecutor(new TpahereCommand());
        Objects.requireNonNull(this.getCommand("tpdeny")).setExecutor(new TpdenyCommand());

        Objects.requireNonNull(this.getCommand("delhome")).setExecutor(new DelHomeCommand());
        Objects.requireNonNull(this.getCommand("home")).setExecutor(new HomeCommand());
        Objects.requireNonNull(this.getCommand("sethome")).setExecutor(new SetHomeCommand());

        Bukkit.getScheduler().runTaskTimer(this, new Tablist(), 0, 100);

        PaperLib.suggestPaper(Bbb.getInstance());
    }

    @Override
    public void onEnable() {
        instance = this;

        File homesFolder = new File(getDataFolder(), "homedata");
        if (!homesFolder.exists())
            homesFolder.mkdir();

        register();
    }

    public static Bbb getInstance() {
        return instance;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player))
            return true;

        if (cmd.getName().equals("msg")) {
            if (args.length == 0) {
                Utils.errormsgs(player, 1, "");
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                Utils.errormsgs(player, 2, args[0]);
                return true;
            }

            StringBuilder msgargs = new StringBuilder();

            for (int i = 1; i < args.length; i++)
                msgargs.append(args[i]).append(" ");

            if (msgargs.toString().equals("")) {
                Utils.errormsgs(player, 1, "");
                return true;
            }

            String b = Utils.getString("otherdata." + target.getUniqueId() + ".ignorelist");
            if (b != null && b.contains(player.getName())) {
                Utils.errormsgs(player, 4, "");
                return true;
            }

            String be = Utils.getString("otherdata." + player.getUniqueId() + ".ignorelist");
            if (b != null && be.contains(target.getName())) {
                Utils.errormsgs(player, 5, "");
                return true;
            }

            player.sendMessage(translate(player, "#d6a7eb[#bc5ae8You #d6a7eb-> #bc5ae8" + target.getName() + "#d6a7eb] %msg%" + msgargs));
            target.sendMessage(translate(player, "#d6a7eb[#bc5ae8" + player.getName() + " #d6a7eb-> #bc5ae8You#d6a7eb] %msg%" + msgargs));            lastReceived.put(player.getUniqueId(), target.getUniqueId());
            lastReceived.put(target.getUniqueId(), player.getUniqueId());
            return true;
        } else if (cmd.getName().equals("reply")) {
            if (args.length == 0) {
                Utils.errormsgs(player, 1, "");
                return true;
            }
            Player target = Bukkit.getPlayer(lastReceived.get(player.getUniqueId()));
            if (target == null || !lastReceived.containsKey(player.getUniqueId()) || lastReceived.get(player.getUniqueId()) == null) {
                Utils.errormsgs(player, 6, "");
                return true;
            }

            StringBuilder msgargs = new StringBuilder();

            for (String arg : args) msgargs.append(arg).append(" ");

            if (msgargs.toString().equals("")) {
                Utils.errormsgs(player, 1, "");
                return true;
            }

            String ignoreclient = Utils.getString("otherdata." + player.getUniqueId() + ".ignorelist");
            if (ignoreclient != null && ignoreclient.contains(target.getName())) {
                Utils.errormsgs(player, 5, "");
                return true;
            }

            String ignoretarget = Utils.getString("otherdata." + target.getUniqueId() + ".ignorelist");
            if (ignoretarget != null && ignoretarget.contains(player.getName())) {
                Utils.errormsgs(player, 4, "");
                return true;
            }

            player.sendMessage(translate(player, "#d6a7eb[#bc5ae8You #d6a7eb-> #bc5ae8" + target.getName() + "#d6a7eb] %msg%" + msgargs));
            target.sendMessage(translate(player, "#d6a7eb[#bc5ae8" + player.getName() + " #d6a7eb-> #bc5ae8You#d6a7eb] %msg%" + msgargs));            lastReceived.put(player.getUniqueId(), target.getUniqueId());
            lastReceived.put(player.getUniqueId(), target.getUniqueId());
            lastReceived.put(target.getUniqueId(), player.getUniqueId());

            return true;
        } else if (cmd.getName().equals("ignore")) {
            String ignoreclient = Utils.getString("otherdata." + player.getUniqueId() + ".ignorelist");
            if (args.length < 1) {
                if (ignoreclient != null) {
                    player.sendMessage(translate(player, "#bc5ae8Your ignored players are: " + ignoreclient.replace(", ", "#bc5ae8, #d6a7eb")));
                    return true;
                }
                Utils.errormsgs(player, 1, "");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                Utils.errormsgs(player, 2, "");
                return true;
            }

            if (target.getName().equals(player.getName())) {
                Utils.errormsgs(player, 8, "");
                return true;
            }

            String breplace = target.getName() + ", ";

            if (ignoreclient != null && ignoreclient.contains(target.getName())) {
                Utils.setData("otherdata." + player.getUniqueId() + ".ignorelist", ignoreclient.replace(target.getName() + ", ", ""));
                Utils.saveData();
                player.sendMessage(translate(player, "#bc5ae8Successfully un ignored &d6a7eb" + target.getName()));
                return true;
            }

            breplace += ignoreclient;
            Utils.setData("otherdata." + player.getUniqueId() + ".ignorelist", breplace);
            Utils.saveData();
            player.sendMessage(translate(player, "#bc5ae8Successfully ignored #d6a7eb" + target.getName()));
            return true;
        } else if (cmd.getName().equals("kill")) {
            player.setHealth(0);
            return true;
        }

        return false;
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

    public static double getTPSofLastSecond() {
        return Bukkit.getServer().getTPS()[0];
    }

    public static int countMinecartInChunk(Chunk chunk) {
        int count = 0;

        for (Entity entity : chunk.getEntities()) {
            if (entity instanceof Minecart) {
                count++;
            }
        }
        return count;
    }

    public static void removeMinecartInChunk(Chunk chunk) {
        for (Entity entity : chunk.getEntities()) {
            if (entity instanceof Minecart) {
                entity.remove();
            }
        }
    }

    @Override
    public void onDisable() {
        saveCustomConfig();
        reloadConfig();
    }
}