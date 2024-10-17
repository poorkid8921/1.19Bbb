package main;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import main.commands.FastCrystals;
import main.commands.economy.Kit;
import main.commands.essentials.List;
import main.commands.essentials.*;
import main.commands.tpa.*;
import main.commands.warps.RTP;
import main.commands.warps.SetWarp;
import main.commands.warps.Spawn;
import main.commands.warps.Warp;
import main.managers.*;
import main.utils.AutoTotem;
import main.managers.GUIManager;
import main.utils.Initializer;
import main.utils.ProtectionEvents;
import main.utils.TeleportCompleter;
import main.managers.instances.PlayerDataHolder;
import main.utils.modules.npcs.InteractAtNPC;
import main.utils.modules.optimizer.InteractionListeners;
import main.utils.modules.optimizer.LastPacketEvent;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.io.File;
import java.util.Map;

import static main.utils.Initializer.*;

public class Economy extends JavaPlugin {
    public static final Point spawnDistance = new Point(0, 0);
    private static final Point point = new Point(0, 0);
    public static Economy INSTANCE;
    public static File dataFolder;

    public static World overworld;
    public static World nether;
    public static World end;

    public static DatabaseManager databaseManager;
    public static ArenaManager arenaManager;
    public static ScheduleManager scheduleManager;
    public static FileManager fileManager;
    public static GUIManager guiManager;
    public static TeamManager teamManager;
    public static EffectManager effectManager;
    public static MessageManager messageManager;

    private static boolean flatSwitch = false;
    private static boolean layerSwitch = false;

    private Location getRandomLoc(World world, int radius, Location[] RTP) {
        Location location = null;
        int boundX, boundZ;
        Block block;
        while (location == null) {
            boundX = Initializer.RANDOM.nextInt(-radius, radius);
            boundZ = Initializer.RANDOM.nextInt(-radius, radius);
            for (final Location k : RTP) {
                if (k == null) break;
                point.setLocation(k.getBlockX(), k.getBlockZ());
                if (point.distance(boundX, boundZ) < 999) break;
            }
            block = world.getHighestBlockAt(boundX, boundZ);
            if (block.isSolid()) location = new Location(world, boundX, block.getY() + 1, boundZ);
        }
        return location;
    }

    private Location getNetherRandomLoc() {
        Location location = null;
        int boundX, boundZ;
        while (location == null) {
            boundX = Initializer.RANDOM.nextInt(-1500, 1500);
            boundZ = Initializer.RANDOM.nextInt(-1500, 1500);
            for (int y = 30; y < 128; y++) {
                if (nether.getBlockAt(boundX, y, boundZ).getType() == Material.AIR && nether.getBlockAt(boundX, y - 1, boundZ).isSolid()) {
                    location = new Location(nether, boundX, y, boundZ);
                    break;
                }
            }
        }
        return location;
    }

    private void registerCommands() {
        CommandManager.registerCommand("msg", new Msg());
        CommandManager.registerCommand("reply", new Reply());
        CommandManager.registerCommand("tpa", new Tpa());
        CommandManager.registerCommand("tpaall", new TpaAll());
        CommandManager.registerCommand("tpaccept", new Tpaccept());
        CommandManager.registerCommand("tpahere", new Tpahere());
        CommandManager.registerCommand("tpdeny", new TpDeny());
        CommandManager.registerCommand("report", new Report());
        CommandManager.registerCommand("msglock", new MsgLock());
        CommandManager.registerCommand("tpalock", new TpaLock());
        CommandManager.registerCommand("spawn", new Spawn());
        CommandManager.registerCommand("discord", new Discord());
        CommandManager.registerCommand("warp", new Warp());
        CommandManager.registerCommand("setwarp", new SetWarp());
        CommandManager.registerCommand("playtime", new PlayTime());
        CommandManager.registerCommand("kit", new Kit());
        CommandManager.registerCommand("ec", new EnderChest());
        CommandManager.registerCommand("setrank", new SetRank());
        CommandManager.registerCommand("grindstone", new GrindStone());
        CommandManager.registerCommand("list", new List());
        CommandManager.registerCommand("broadcast", new Broadcast());
        CommandManager.registerCommand("banip", new Ban());
        CommandManager.registerCommand("suicide", new Suicide());
        CommandManager.registerCommand("fastcrystals", new FastCrystals());
        CommandManager.registerCommand("irename", new ItemRename());

        TeleportCompleter tabCompleter = new TeleportCompleter();
        CommandManager.tabCompleter("tpa", tabCompleter);
        CommandManager.tabCompleter("tpaccept", tabCompleter);
        CommandManager.tabCompleter("tpahere", tabCompleter);
        CommandManager.tabCompleter("tpdeny", tabCompleter);
    }

    private void registerPacketListeners() {
        PacketEvents.getAPI().getEventManager().registerListeners(
                new InteractionListeners(),
                new LastPacketEvent(),
                new AutoTotem(),
                new InteractAtNPC()
        );
        PacketEvents.getAPI().init();
    }

    @Override
    public void onLoad() {
        INSTANCE = this;

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings().checkForUpdates(false).reEncodeByDefault(false);
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        dataFolder = getDataFolder();

        overworld = Bukkit.getWorld("world");
        nether = Bukkit.getWorld("world_nether");
        end = Bukkit.getWorld("world_the_end");

        spawn = new Location(overworld, -0.5D, 140.0D, 0.5D, 90F, 0F);

        databaseManager = new DatabaseManager();
        arenaManager = new ArenaManager();
        scheduleManager = new ScheduleManager();
        fileManager = new FileManager();
        guiManager = new GUIManager();
        teamManager = new TeamManager();
        effectManager = new EffectManager();
        messageManager = new MessageManager();

        WorldBorder netherWorldBorder = nether.getWorldBorder();
        netherWorldBorder.setCenter(0D, 0D);
        netherWorldBorder.setSize(12500D);

        WorldBorder endWorldBorder = end.getWorldBorder();
        endWorldBorder.setCenter(0D, 0D);
        endWorldBorder.setSize(100000D);

        scheduleManager.later(() -> {
            for (short i = 0; i < 101; i++) {
                if (i == 100) {
                    getCommand("rtp").setExecutor(new RTP());
                    Bukkit.getScheduler().scheduleSyncRepeatingTask(INSTANCE, () -> {
                        for (final Entity entity : overworld.getEntities()) {
                            if (entity instanceof EnderCrystal || entity instanceof Arrow) entity.remove();
                        }
                        // flat
                        arenaManager.setCuboid(new short[][]{
                                        new short[]{44, 136, 45, -46, 134, 24},
                                        new short[]{23, 136, -45, 44, 134, 23},
                                        new short[]{-46, 136, -24, 22, 134, -45},
                                        new short[]{-46, 136, -231, -25, 134, 23}
                                },
                                Blocks.AIR,
                                Blocks.AIR.defaultBlockState());
                        if (!flatSwitch) {
                            String key;
                            for (final Map.Entry<String, PlayerDataHolder> D0 : playerData.entrySet()) {
                                key = D0.getKey();
                                if (Bukkit.getPlayer(key) != null) playerData.remove(key);
                            }

                            // arena air
                            arenaManager.setCuboid(new short[][]{
                                            new short[]{-47, 136, -111, -112, 134, 111},
                                            new short[]{-46, 136, -111, 110, 134, -46},
                                            new short[]{45, 136, 111, 110, 134, -45},
                                            new short[]{-46, 136, 111, 44, 134, 46}
                                    },
                                    Blocks.AIR,
                                    Blocks.AIR.defaultBlockState());
                            if (!layerSwitch) {
                                // grass layer
                                arenaManager.setArea(133, new short[][]{
                                                new short[]{-47, -111, -112, 111},
                                                new short[]{-46, -111, 110, -46},
                                                new short[]{45, 111, 110, -45},
                                                new short[]{-46, 111, 44, 46}
                                        },
                                        Blocks.GRASS_BLOCK.defaultBlockState());
                                // dirt layer
                                arenaManager.setCuboid(new short[][]{
                                                new short[]{-47, 132, -111, -112, 129, 111},
                                                new short[]{-46, 132, -111, 110, 129, -46},
                                                new short[]{45, 132, 111, 110, 129, -45},
                                                new short[]{-46, 132, 111, 44, 129, 46}
                                        },
                                        Blocks.DIRT,
                                        Blocks.DIRT.defaultBlockState());
                            } else {
                                // sand layer
                                arenaManager.setCuboid(new short[][]{
                                                new short[]{-47, 133, -111, -112, 129, 111},
                                                new short[]{-46, 133, -111, 110, 129, -46},
                                                new short[]{45, 133, 111, 110, 129, -45},
                                                new short[]{-46, 133, 111, 44, 129, 46}
                                        },
                                        Blocks.SAND,
                                        Blocks.SAND.defaultBlockState());
                            }
                            layerSwitch = !layerSwitch;
                            // stone layer
                            arenaManager.setCuboid(new short[][]{
                                            new short[]{-47, 128, -111, -112, 3, 111},
                                            new short[]{-46, 128, -111, 110, 3, -46},
                                            new short[]{45, 128, 111, 110, 3, -45},
                                            new short[]{-46, 128, 111, 44, 3, 46}
                                    },
                                    Blocks.STONE,
                                    Blocks.STONE.defaultBlockState());

                            Location location;
                            for (final Player k : Bukkit.getOnlinePlayers()) {
                                if (k.isGliding()) continue;
                                location = k.getLocation();
                                location.setY(200);
                                if (overworld.getBlockAt(location).getType() != Material.BARRIER) continue;
                                location.setY(135);
                                k.teleport(location);
                            }
                        }
                        flatSwitch = !flatSwitch;
                    }, 0L, 12000L);
                    Bukkit.getLogger().warning("Finished RTP population.");
                    return;
                }
                short finalI = i;
                Bukkit.getScheduler().runTaskLater(this, () -> {
                    overworldRTP[finalI] = getRandomLoc(overworld, 5000, overworldRTP);
                    netherRTP[finalI] = getNetherRandomLoc();
                    endRTP[finalI] = getRandomLoc(end, 1500, endRTP);
                }, finalI);
            }
        }, 100L);

        registerCommands();
        registerPacketListeners();

        Bukkit.getPluginManager().registerEvents(new Events(), Economy.INSTANCE);
        Bukkit.getPluginManager().registerEvents(new ProtectionEvents(), Economy.INSTANCE);

        Initializer.init();
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
    }
}