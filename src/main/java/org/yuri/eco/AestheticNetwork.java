package org.yuri.eco;

import com.booksaw.betterTeams.BooksawCommand;
import com.booksaw.betterTeams.ConfigManager;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.HelpCommand;
import com.booksaw.betterTeams.commands.PermissionParentCommand;
import com.booksaw.betterTeams.commands.team.*;
import com.booksaw.betterTeams.cooldown.CooldownManager;
import com.booksaw.betterTeams.cost.CostManager;
import com.booksaw.betterTeams.events.ChatManagement;
import com.booksaw.betterTeams.events.DamageManagement;
import com.booksaw.betterTeams.events.InventoryManagement;
import com.booksaw.betterTeams.events.MCTeamManagement;
import com.booksaw.betterTeams.integrations.placeholder.TeamPlaceholders;
import com.booksaw.betterTeams.message.MessageManager;
import com.booksaw.betterTeams.score.ScoreManagement;
import com.booksaw.betterTeams.team.storage.StorageType;
import com.booksaw.betterTeams.team.storage.storageManager.YamlStorageManager;
import common.commands.*;
import common.commands.tpa.TpaCommand;
import common.commands.tpa.TpacceptCommand;
import common.commands.tpa.TpahereCommand;
import common.commands.tpa.TpdenyCommand;
import io.papermc.lib.PaperLib;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;
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
import org.yuri.eco.utils.Languages;
import org.yuri.eco.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

import static org.yuri.eco.utils.Initializer.economy;
import static org.yuri.eco.utils.Utils.translateo;

@SuppressWarnings("deprecation")
public final class AestheticNetwork extends JavaPlugin implements CommandExecutor, TabExecutor {
    public MCTeamManagement teamManagement;
    public ChatManagement chatManagement;
    //FileConfiguration config = getConfig();
    private File cf = new File(getDataFolder(), "data.yml");
    private FileConfiguration cc = YamlConfiguration.loadConfiguration(cf);
    private DamageManagement damageManagement;

    private ConfigManager configManager;

    @Override
    public void onDisable() {
        for (Map.Entry<Player, Team> temp : InventoryManagement.adminViewers.entrySet()) {
            temp.getKey().closeInventory();
            temp.getValue().saveEchest();
        }

        if (teamManagement != null) {
            teamManagement.removeAll();
        }

        Team.disable();
        MessageManager.dumpMessages();
    }

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

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
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
            Bukkit.getServer().getOnlinePlayers().stream().filter(s -> !s.isInsideVehicle() && !s.isGliding() && s.getWorld().getBlockAt(new Location(s.getWorld(), s.getLocation().getX(), 319, s.getLocation().getZ())).getType() == Material.BARRIER).forEach(player -> {
                Location l = player.getLocation();
                PaperLib.teleportAsync(player, new Location(player.getWorld(), l.getX(), 135, l.getZ(), l.getYaw(), l.getPitch()));
            });
        }, 0L, 24005L);

        Bukkit.getServer().getScheduler().runTaskLater(this, Languages::init, 100L);
        Bukkit.getPluginManager().registerEvents(new events(), this);

        configManager = new ConfigManager("config", true);
        String language = getConfig().getString("language");
        MessageManager.setLanguage(language);
        if (Objects.requireNonNull(language).equals("en") || language.equals("")) {
            MessageManager.setLanguage("messages");
        }

        loadCustomConfigs();
        setupStorage();
        ChatManagement.enable();
        new TeamPlaceholders(this).register();

        setupCommands();
        setupListeners();
    }

    public void loadCustomConfigs() {
        File f = MessageManager.getFile();
        String language = MessageManager.getLanguage();
        try {
            if (!f.exists()) saveResource(language + ".yml", false);
        } catch (Exception e) {
            Bukkit.getLogger().warning("Could not load selected language: " + language + " go to https://github.com/booksaw/BetterTeams/wiki/Language to view a list of supported languages");
            Bukkit.getLogger().warning("Reverting to english so the plugin can still function");
            MessageManager.setLanguage("messages");
            loadCustomConfigs();
            return;
        }

        ConfigManager messagesConfigManager = new ConfigManager(language, true);
        MessageManager.addMessages(messagesConfigManager.config);

        if (!language.equals("messages")) {
            messagesConfigManager = new ConfigManager("messages", true);
            MessageManager.addBackupMessages(messagesConfigManager.config);
        }

        if (getConfig().getBoolean("disableCombat")) {
            if (damageManagement == null) {
                damageManagement = new DamageManagement();
                getServer().getPluginManager().registerEvents(damageManagement, this);
            }

        } else {
            if (damageManagement != null) {
                Bukkit.getLogger().log(Level.WARNING, "Restart server for damage changes to apply");
            }
        }

        // loading the fully custom help message option
        HelpCommand.setupHelp();
    }

    public void setupCommands() {
        PermissionParentCommand teamCommand = new PermissionParentCommand(new CostManager("team"), new CooldownManager("team"), "team");
        teamCommand.addSubCommands(new CreateCommand(), new LeaveCommand(), new DisbandCommand(), new DescriptionCommand(), new InviteCommand(), new JoinCommand(), new NameCommand(), new OpenCommand(), new InfoCommand(), new KickCommand(), new PromoteCommand(), new DemoteCommand(), new HomeCommand(), new SethomeCommand(), new BanCommand(), new UnbanCommand(), new ChatCommand(), new ColorCommand(), new TitleCommand(), new TopCommand(), new BaltopCommand(), new RankCommand(), new DelHome(), new AllyCommand(), new NeutralCommand(), new AllyChatCommand(), new ListCommand(), new WarpCommand(), new SetWarpCommand(), new DelwarpCommand(), new WarpsCommand(), new EchestCommand(), new RankupCommand(), new TagCommand());

        if (getConfig().getBoolean("disableCombat")) {
            teamCommand.addSubCommand(new PvpCommand());
        }

        if (getConfig().getBoolean("singleOwner")) {
            teamCommand.addSubCommand(new SetOwnerCommand());
        }

        new BooksawCommand("team", teamCommand, "All commands for teams", getConfig().getStringList("command.team"));
        teamCommand.addSubCommands(new DepositCommand(), new BalCommand(), new WithdrawCommand());
    }

    public void setupListeners() {
        Bukkit.getLogger().info("Display team name config value: " + getConfig().getString("displayTeamName"));
        MCTeamManagement.BelowNameType type = MCTeamManagement.BelowNameType.getType(Objects.requireNonNull(getConfig().getString("displayTeamName")));
        Bukkit.getLogger().info("Loading below name. Type: " + type);
        if (getConfig().getBoolean("useTeams")) {
            if (teamManagement == null) {
                teamManagement = new MCTeamManagement(type);

                Bukkit.getScheduler().runTaskAsynchronously(this, () -> teamManagement.displayBelowNameForAll());
                getServer().getPluginManager().registerEvents(teamManagement, this);
                Bukkit.getLogger().info("teamManagement declared: " + teamManagement);
            }
        } else {
            Bukkit.getLogger().info("Not loading management");
            if (teamManagement != null) {
                Bukkit.getLogger().log(Level.WARNING, "Restart server for minecraft team changes to apply");
            }
        }

        getServer().getPluginManager().registerEvents((chatManagement = new ChatManagement()), this);
        getServer().getPluginManager().registerEvents(new ScoreManagement(), this);

        getServer().getPluginManager().registerEvents(new InventoryManagement(), this);
    }

    @Override
    public FileConfiguration getConfig() {
        return configManager.config;
    }

    public void setupStorage() {
        File f = new File("plugins/BetterTeams/" + YamlStorageManager.TEAMLISTSTORAGELOC + ".yml");

        if (!f.exists()) {
            Initializer.p.saveResource("teams.yml", false);
        }

        Team.setupTeamManager(StorageType.YAML);
        Team.getTeamManager().loadTeams();
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
            if (Utils.manager().get("r." + tn + ".m") != null && !sender.hasPermission("has.staff")) {
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