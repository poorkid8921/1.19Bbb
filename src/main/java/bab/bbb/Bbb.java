package bab.bbb;

import bab.bbb.Commands.DelHomeCommand;
import bab.bbb.Commands.Discord;
import bab.bbb.Commands.HomeCommand;
import bab.bbb.Commands.SetHomeCommand;
import bab.bbb.Events.Dupes.FrameDupe;
import bab.bbb.Events.Dupes.ikeaDupe;
import bab.bbb.Events.misc.BetterChat;
import bab.bbb.Events.misc.MiscEvents;
import bab.bbb.Events.misc.MoveEvents;
import bab.bbb.Events.misc.patches.AntiBurrow;
import bab.bbb.Events.misc.patches.AntiPacketElytraFly;
import bab.bbb.Events.misc.patches.ChestLimit;
import bab.bbb.tpa.TpaCommand;
import bab.bbb.tpa.TpacceptCommand;
import bab.bbb.tpa.TpahereCommand;
import bab.bbb.tpa.TpdenyCommand;
import bab.bbb.utils.Utils;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

import static bab.bbb.utils.Utils.translate;

@SuppressWarnings("deprecation")
public final class Bbb extends JavaPlugin implements CommandExecutor, TabExecutor {
    public static HashMap<UUID, UUID> lastReceived = new HashMap<>();
    public static HashMap<UUID, Long> kills = new HashMap<>();
    private static Bbb instance;
    public FileConfiguration config = this.getConfig();
    public File customConfigFile = new File(getDataFolder(), "data.yml");
    public FileConfiguration customConfigConfig = YamlConfiguration.loadConfiguration(customConfigFile);

    public static Bbb getInstance() {
        return instance;
    }

    public static double getTPSofLastSecond() {
        return Bukkit.getServer().getTPS()[0];
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(new MiscEvents(), this);
        Bukkit.getPluginManager().registerEvents(new MoveEvents(), this);
        //Bukkit.getPluginManager().registerEvents(new AnvilListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChestLimit(), this);

        Bukkit.getPluginManager().registerEvents(new BetterChat(), this);
        Bukkit.getPluginManager().registerEvents(new FrameDupe(), this);
        Bukkit.getPluginManager().registerEvents(new AntiPacketElytraFly(), this);
        //Bukkit.getPluginManager().registerEvents(new AntiBurrow(), this);

        Bukkit.getPluginManager().registerEvents(new ikeaDupe(), this);

        Objects.requireNonNull(this.getCommand("d")).setExecutor(new Discord());
        Objects.requireNonNull(this.getCommand("tpa")).setExecutor(new TpaCommand());
        Objects.requireNonNull(this.getCommand("tpaccept")).setExecutor(new TpacceptCommand());
        Objects.requireNonNull(this.getCommand("tpahere")).setExecutor(new TpahereCommand());
        Objects.requireNonNull(this.getCommand("tpdeny")).setExecutor(new TpdenyCommand());

        Objects.requireNonNull(this.getCommand("delhome")).setExecutor(new DelHomeCommand());
        Objects.requireNonNull(this.getCommand("home")).setExecutor(new HomeCommand());
        Objects.requireNonNull(this.getCommand("sethome")).setExecutor(new SetHomeCommand());

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

            if (msgargs.toString().equals(" ")) {
                Utils.errormsgs(player, 1, "");
                return true;
            }

            List<String> ignoreclient = customConfigConfig.getStringList("data." + player.getUniqueId() + ".ignorelist");
            if (ignoreclient != null && ignoreclient.contains(target.getName())) {
                Utils.errormsgs(player, 5, "");
                return true;
            }

            List<String> ignoretarget = customConfigConfig.getStringList("data." + target.getUniqueId() + ".ignorelist");
            if (ignoretarget != null && ignoretarget.contains(player.getName())) {
                Utils.errormsgs(player, 4, "");
                return true;
            }

            player.sendMessage(translate("&dYou whisper to " + target.getName() + ": " + msgargs));
            target.sendMessage(translate("&d" + player.getName() + " whispers: " + msgargs));
            lastReceived.put(player.getUniqueId(), target.getUniqueId());
            lastReceived.put(target.getUniqueId(), player.getUniqueId());
            return true;
        } else if (cmd.getName().equals("reply")) {
            if (args.length == 0) {
                Utils.errormsgs(player, 1, "");
                return true;
            }

            if (!lastReceived.containsKey(player.getUniqueId()) || lastReceived.get(player.getUniqueId()) == null) {
                Utils.errormsgs(player, 6, "");
                return true;
            }

            Player target = Bukkit.getPlayer(lastReceived.get(player.getUniqueId()));
            if (target == null) {
                Utils.errormsgs(player, 6, "");
                return true;
            }
            StringBuilder msgargs = new StringBuilder();
            for (String arg : args) msgargs.append(arg).append(" ");

            if (msgargs.toString().equals(" ")) {
                Utils.errormsgs(player, 1, "");
                return true;
            }

            List<String> ignoreclient = customConfigConfig.getStringList("data." + player.getUniqueId() + ".ignorelist");
            if (ignoreclient != null && ignoreclient.contains(target.getName())) {
                Utils.errormsgs(player, 5, "");
                return true;
            }

            List<String> ignoretarget = customConfigConfig.getStringList("data." + target.getUniqueId() + ".ignorelist");
            if (ignoretarget != null && ignoretarget.contains(player.getName())) {
                Utils.errormsgs(player, 4, "");
                return true;
            }

            player.sendMessage(translate("&dYou whisper to " + target.getName() + ": " + msgargs));
            target.sendMessage(translate("&d" + player.getName() + " whispers: " + msgargs));
            lastReceived.put(target.getUniqueId(), player.getUniqueId());
            return true;
        } else if (cmd.getName().equals("ignore")) {
            List<String> ignoreclient = customConfigConfig.getStringList("data." + player.getUniqueId() + ".ignorelist");
            if (args.length < 1) {
                if (ignoreclient != null) {
                    ignoreclient.replaceAll(s -> "&c" + s + "&7");
                    String str = ignoreclient.toString().replace("[", "").replace("]", "");
                    player.sendMessage(translate("&7Your ignored players are: &c" + str));
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

            if (ignoreclient != null && ignoreclient.contains(target.getName())) {
                ignoreclient.remove(target.getName());
                customConfigConfig.set("data." + player.getUniqueId() + ".ignorelist", ignoreclient);
                Utils.saveData();
                player.sendMessage(translate("&7Successfully un ignored &c" + target.getName()));
                return true;
            }

            ignoreclient.add(target.getName());
            customConfigConfig.set("data." + player.getUniqueId() + ".ignorelist", ignoreclient);
            Utils.saveData();
            player.sendMessage(translate("&7Successfully ignored &c" + target.getName()));
            return true;
        } else if (cmd.getName().equals("kill")) {
            UUID playerUniqueId = Objects.requireNonNull(((Player) sender).getPlayer()).getUniqueId();
            if (
                    kills.containsKey(playerUniqueId)
                            && kills.get(playerUniqueId) > System.currentTimeMillis()
            ) {
                sender.sendMessage(translate("&7You can't use this command right now. Try again later"));
                return true;
            } else
                kills.put(playerUniqueId, System.currentTimeMillis() + 2000);

            EntityDamageEvent event = new EntityDamageEvent(player, EntityDamageEvent.DamageCause.SUICIDE, player.getHealth());
            player.setLastDamageCause(event);
            Bukkit.getServer().getPluginManager().callEvent(event);
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

    @Override
    public void onDisable() {
        saveCustomConfig();
        reloadConfig();
    }
}