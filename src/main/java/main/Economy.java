package main;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import main.commands.BombRTP;
import main.commands.FastCrystals;
import main.commands.economy.Kit;
import main.commands.essentials.List;
import main.commands.essentials.*;
import main.commands.tpa.*;
import main.commands.warps.RTP;
import main.commands.warps.SetWarp;
import main.commands.warps.Spawn;
import main.commands.warps.Warp;
import main.utils.*;
import main.utils.instances.CommandHolder;
import main.utils.instances.CustomPlayerDataHolder;
import main.utils.modules.npcs.InteractAtNPC;
import main.utils.modules.optimizer.InteractionListeners;
import main.utils.modules.optimizer.LastPacketEvent;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.io.File;
import java.util.Map;

import static main.utils.Initializer.*;
import static main.utils.modules.arenas.Utils.chunkSource;
import static main.utils.modules.arenas.Utils.nmsOverworld;
import static main.utils.modules.arenas.Utils.setArea;
import static main.utils.modules.arenas.Utils.setCuboid;

public class Economy extends JavaPlugin {
    public static final Point spawnDistance = new Point(0, 0);
    private static final Point point = new Point(0, 0);
    public static File dataFolder;
    public static World d;
    public static World d0;
    public static World d1;
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
                if (d0.getBlockAt(boundX, y, boundZ).getType() == Material.AIR && d0.getBlockAt(boundX, y - 1, boundZ).isSolid()) {
                    location = new Location(d0, boundX, y, boundZ);
                    break;
                }
            }
        }
        return location;
    }

    private void registerCommands() {
        final CommandHolder[] ignored = {
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
                new CommandHolder("spawn", new Spawn()),
                new CommandHolder("discord", new Discord()),
                new CommandHolder("warp", new Warp()),
                new CommandHolder("setwarp", new SetWarp()),
                new CommandHolder("playtime", new PlayTime()),
                new CommandHolder("kit", new Kit()),
                new CommandHolder("ec", new EnderChest()),
                new CommandHolder("setrank", new SetRank()),
                new CommandHolder("rgc", new CreateRegion()),
                new CommandHolder("grindstone", new GrindStone()),
                new CommandHolder("list", new List()),
                new CommandHolder("broadcast", new Broadcast()),
                new CommandHolder("bombrtp", new BombRTP()),
                new CommandHolder("banip", new Ban()),
                new CommandHolder("suicide", new Suicide()),
                new CommandHolder("fastcrystals", new FastCrystals()),
                new CommandHolder("irename", new ItemRename())
        };
        final TeleportCompleter tabCompleter = new TeleportCompleter();
        this.getCommand("tpa").setTabCompleter(tabCompleter);
        this.getCommand("tpaccept").setTabCompleter(tabCompleter);
        this.getCommand("tpahere").setTabCompleter(tabCompleter);
        this.getCommand("tpdeny").setTabCompleter(tabCompleter);
    }

    private void registerPacketListeners() {
        PacketEvents.getAPI().getEventManager().registerListeners(new InteractionListeners(), new LastPacketEvent(), new AutoTotem(), new InteractAtNPC());
        PacketEvents.getAPI().init();
    }

    @Override
    public void onLoad() {
        p = this;
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings().checkForUpdates(false).reEncodeByDefault(false);
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        dataFolder = getDataFolder();
        d = Bukkit.getWorld("world");
        d0 = Bukkit.getWorld("world_nether");
        d1 = Bukkit.getWorld("world_the_end");
        nmsOverworld = ((CraftWorld) d).getHandle();
        chunkSource = nmsOverworld.getChunkSource();
        main.utils.modules.arenas.Utils.lightEngine = chunkSource.getLightEngine();
        WorldBorder wb = d0.getWorldBorder();
        wb.setCenter(0D, 0D);
        wb.setSize(12500D);
        wb = d1.getWorldBorder();
        wb.setCenter(0D, 0D);
        wb.setSize(100000D);
        spawn = new Location(d, -0.5D, 140.0D, 0.5D, 90F, 0F);
        Bukkit.getScheduler().runTaskLater(this, () -> {
            for (short i = 0; i < 101; i++) {
                if (i == 100) {
                    getCommand("rtp").setExecutor(new RTP());
                    Bukkit.getScheduler().scheduleSyncRepeatingTask(p, () -> {
                        for (final Entity entity : d.getEntities()) {
                            if (entity instanceof EnderCrystal || entity instanceof Arrow) entity.remove();
                        }
                        // flat
                        setCuboid(new short[][]{
                                        new short[]{44, 136, 45, -46, 134, 24},
                                        new short[]{23, 136, -45, 44, 134, 23},
                                        new short[]{-46, 136, -24, 22, 134, -45},
                                        new short[]{-46, 136, -231, -25, 134, 23}
                                },
                                Blocks.AIR,
                                Blocks.AIR.defaultBlockState());
                        if (!flatSwitch) {
                            String key;
                            for (final Map.Entry<String, CustomPlayerDataHolder> D0 : playerData.entrySet()) {
                                key = D0.getKey();
                                if (Bukkit.getPlayer(key) != null) playerData.remove(key);
                            }

                            // arena air
                            setCuboid(new short[][]{
                                            new short[]{-47, 136, -111, -112, 134, 111},
                                            new short[]{-46, 136, -111, 110, 134, -46},
                                            new short[]{45, 136, 111, 110, 134, -45},
                                            new short[]{-46, 136, 111, 44, 134, 46}
                                    },
                                    Blocks.AIR,
                                    Blocks.AIR.defaultBlockState());
                            if (!layerSwitch) {
                                // grass layer
                                setArea(133, new short[][]{
                                                new short[]{-47, -111, -112, 111},
                                                new short[]{-46, -111, 110, -46},
                                                new short[]{45, 111, 110, -45},
                                                new short[]{-46, 111, 44, 46}
                                        },
                                        Blocks.GRASS_BLOCK.defaultBlockState());
                                // dirt layer
                                setCuboid(new short[][]{
                                                new short[]{-47, 132, -111, -112, 129, 111},
                                                new short[]{-46, 132, -111, 110, 129, -46},
                                                new short[]{45, 132, 111, 110, 129, -45},
                                                new short[]{-46, 132, 111, 44, 129, 46}
                                        },
                                        Blocks.DIRT,
                                        Blocks.DIRT.defaultBlockState());
                            } else {
                                // sand layer
                                setCuboid(new short[][]{
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
                            setCuboid(new short[][]{
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
                                if (Economy.d.getBlockAt(location).getType() != Material.BARRIER) continue;
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
                    overworldRTP[finalI] = getRandomLoc(d, 5000, overworldRTP);
                    netherRTP[finalI] = getNetherRandomLoc();
                    endRTP[finalI] = getRandomLoc(d1, 1500, endRTP);
                }, finalI);
            }
        }, 100L);
        registerCommands();
        Gui.init();
        registerPacketListeners();
        Initializer.init();
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
    }
}