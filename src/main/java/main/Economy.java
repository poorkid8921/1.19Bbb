package main;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import main.commands.*;
import main.commands.economy.Pay;
import main.commands.essentials.*;
import main.commands.tpa.*;
import main.commands.warps.DelHome;
import main.commands.warps.Home;
import main.commands.warps.SetHome;
import main.commands.warps.SetWarp;
import main.commands.warps.Warp;
import main.utils.*;
import main.utils.arenas.Arena;
import main.utils.arenas.ArenaIO;
import main.utils.arenas.CreateCommand;
import main.commands.economy.Balance;
import main.utils.instances.CommandHolder;
import main.utils.instances.CustomPlayerDataHolder;
import main.utils.instances.HomeHolder;
import main.utils.optimizer.AnimationEvent;
import main.utils.optimizer.InteractionEvent;
import main.utils.optimizer.LastPacketEvent;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static main.utils.Initializer.*;

public class Economy extends JavaPlugin {
    public static File dataFolder;
    public static World d;
    public static FileConfiguration config;
    private static boolean alreadySavingData;
    private File dataFile;
    private int arena = 1;

    Location getRandomLoc(World w) {
        Location loc = null;
        while (loc == null) {
            int boundX = Initializer.RANDOM.nextInt(-5000, 5000);
            int boundZ = Initializer.RANDOM.nextInt(-5000, 5000);
            Block b = w.getHighestBlockAt(boundX, boundZ);
            if (b.isSolid())
                loc = new Location(w, boundX, b.getY() + 1, boundZ);
        }
        return loc;
    }

    Location getNetherRandomLoc(World w) {
        Location loc = null;
        while (loc == null) {
            int boundX = Initializer.RANDOM.nextInt(-5000, 5000);
            int boundZ = Initializer.RANDOM.nextInt(-5000, 5000);

            for (int y = 30; y < 128; y++) {
                if (w.getBlockAt(boundX, y, boundZ).getType() == Material.AIR &&
                        w.getBlockAt(boundX, y - 1, boundZ).isSolid()) {
                    loc = new Location(w, boundX, y, boundZ);
                    break;
                }
            }
        }
        return loc;
    }

    void registerCommands() {
        for (CommandHolder command : ObjectArrayList.of(
                new CommandHolder("msg", new Msg()),
                new CommandHolder("reply", new Reply()),
                new CommandHolder("tpa", new Tpa()),
                new CommandHolder("tpaall", new TpaAll()),
                new CommandHolder("tpaccept", new Tpaccept()),
                new CommandHolder("tpahere", new Tpahere()),
                new CommandHolder("tpdeny", new TpDeny()),
                new CommandHolder("report", new Report()),
                new CommandHolder("msglock", new MsgLock()),
                new CommandHolder("tpalock", new TpaLock()),
                new CommandHolder("stats", new Stats()),
                new CommandHolder("spawn", new Spawn()),
                new CommandHolder("discord", new Discord()),
                new CommandHolder("warp", new Warp()),
                new CommandHolder("setwarp", new SetWarp()),
                new CommandHolder("playtime", new PlayTime()),
                new CommandHolder("kit", new Kit()),
                new CommandHolder("rbalance", new Balance()),
                new CommandHolder("rsethome", new SetHome()),
                new CommandHolder("rhome", new Home()),
                new CommandHolder("rdelhome", new DelHome()),
                new CommandHolder("acreate", new CreateCommand()),
                new CommandHolder("enderchest", new EnderChest()),
                new CommandHolder("setrank", new SetRank()),
                new CommandHolder("rgc", new CreateRegion()),
                new CommandHolder("rpay", new Pay())
        ))
            getCommand(command.getName()).setExecutor(command.getClazz());
        TeleportCompleter tabCompleter = new TeleportCompleter();
        this.getCommand("tpa").setTabCompleter(tabCompleter);
        this.getCommand("tpaccept").setTabCompleter(tabCompleter);
        this.getCommand("tpahere").setTabCompleter(tabCompleter);

        HomeCompleter homeCompleter = new HomeCompleter();
        this.getCommand("rhome").setTabCompleter(homeCompleter);
        this.getCommand("rdelhome").setTabCompleter(homeCompleter);
    }

    private void setupArenas() {
        for (File file : new File(dataFolder, "arenas").listFiles()) {
            try {
                Arena arena = ArenaIO.loadArena(file);
                if (arena != null) Arena.getArenas().put(arena.getName(), arena);
            } catch (Exception ignored) {
            }
        }
    }
    
    private void registerPacketListeners() {
        PacketEvents.getAPI().getEventManager().registerListeners(new AnimationEvent(),
                new InteractionEvent(),
                new LastPacketEvent(),
                new AntiAutoTotem());
        PacketEvents.getAPI().init();
    }

    @Override
    public void onLoad() {
        p = this;
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings()
                .checkForUpdates(false)
                .reEncodeByDefault(false);
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        dataFolder = getDataFolder();
        dataFile = new File(dataFolder, "data.yml");
        config = YamlConfiguration.loadConfiguration(dataFile);
        chat = getServer().getServicesManager().getRegistration(Chat.class).getProvider();
        lp = Bukkit.getServicesManager().getRegistration(LuckPerms.class).getProvider();
        economy = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class).getProvider();

        Bukkit.getScheduler().runTaskLater(this, () -> {
            World d0 = Bukkit.getWorld("world_nether");
            World d1 = Bukkit.getWorld("world_the_end");
            new BukkitRunnable() {
                int i = 0;

                public void run() {
                    if (i++ == 101) {
                        getCommand("rtp").setExecutor(new RTP());
                        Bukkit.getScheduler().scheduleSyncRepeatingTask(p, () -> {
                            for (Entity ent : d.getEntities()) {
                                if (ent instanceof EnderCrystal)
                                    ent.remove();
                            }

                            if (arena++ == 3)
                                arena = 1;
                            Arena.getArenas().get(arena == 1 ? "ffa1" : "ffa2").reset(100000);
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                if (p.isGliding())
                                    continue;
                                Location loc = p.getLocation();
                                loc.setY(200);
                                if (Economy.d.getBlockAt(loc).getType() != Material.BARRIER)
                                    continue;
                                loc.setY(135);
                                p.teleportAsync(loc);
                            }
                        }, 0L, 24000L);
                        Bukkit.getLogger().warning("Finished RTP population.");
                        this.cancel();
                        return;
                    }
                    overworldRTP.add(getRandomLoc(d));
                    netherRTP.add(getNetherRandomLoc(d0));
                    endRTP.add(getRandomLoc(d1));
                }
            }.runTaskTimer(this, 0L, 0L);
            d = Bukkit.getWorld("world");
            spawn = new Location(Economy.d, -0.5, 140.0, 0.5, 90F, 0F);
        }, 100L);
        registerCommands();
        setupArenas();
        Gui.init();
        registerPacketListeners();
        Initializer.init();
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
        if (alreadySavingData)
            return;

        alreadySavingData = true;
        config.set("r", null);
        if (!playerData.isEmpty()) {
            for (Map.Entry<String, CustomPlayerDataHolder> entry : playerData.entrySet()) {
                CustomPlayerDataHolder value = entry.getValue();
                String key = entry.getKey();
                config.set("r." + key + ".0", value.getMtoggle());
                config.set("r." + key + ".1", value.getTptoggle());
                config.set("r." + key + ".2", value.getMoney());
                config.set("r." + key + ".3", value.getDeaths());
                config.set("r." + key + ".4", value.getKills());
                if (value.getHomes().size() > 0) {
                    StringBuilder finalstr = new StringBuilder();
                    for (HomeHolder k : value.getHomes()) {
                        Location loc = k.getLocation();
                        finalstr.append(k.getName()).append(":").append(loc.getWorld().getName()).append("m").append(loc.getX()).append(":").append(loc.getY()).append(":").append(loc.getZ()).append(":").append(loc.getYaw()).append(":").append(loc.getPitch()).append(":;");
                    }
                    config.set("r." + key + ".5", finalstr.toString());
                } else
                    config.set("r." + key + ".5", "null");
                config.set("r." + key + ".6", value.getRank());
            }
        }
        try {
            config.save(dataFile);
        } catch (IOException ignored) {
        }
        alreadySavingData = false;
    }
}