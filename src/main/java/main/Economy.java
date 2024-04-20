package main;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import main.commands.BombRTP;
import main.commands.FastCrystals;
import main.commands.economy.Kit;
import main.commands.essentials.List;
import main.commands.essentials.*;
import main.commands.tpa.*;
import main.commands.warps.*;
import main.utils.*;
import main.utils.instances.CommandHolder;
import main.utils.instances.CustomPlayerDataHolder;
import main.utils.npcs.InteractAtNPC;
import main.utils.optimizer.InteractionListeners;
import main.utils.optimizer.LastPacketEvent;
import net.minecraft.world.level.block.Blocks;
import org.apache.commons.lang3.ArrayUtils;
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
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static main.utils.Initializer.*;
import static main.utils.Utils.chunkSource;
import static main.utils.Utils.nmsOverworld;

public class Economy extends JavaPlugin {
    public static final Point spawnDistance = new Point(0, 0);
    private static final Point point = new Point(0, 0);
    public static File dataFolder;
    public static World d;
    public static World d0;
    public static World d1;
    private static boolean flatSwitch = false;

    private Location getRandomLoc(World w, int x, Location[] RTP) {
        Location loc = null;
        while (loc == null) {
            int boundX = Initializer.RANDOM.nextInt(-x, x);
            int boundZ = Initializer.RANDOM.nextInt(-x, x);
            if (spawnDistance.distance(boundX, boundZ) < 128) continue;
            for (Location loc1 : RTP) {
                if (loc1 == null) break;
                point.setLocation(loc1.getBlockX(), loc1.getBlockZ());
                if (point.distance(boundX, boundZ) < 50) break;
            }
            Block b = w.getHighestBlockAt(boundX, boundZ);
            if (b.isSolid()) loc = new Location(w, boundX, b.getY() + 1, boundZ);
        }
        return loc;
    }

    private Location getNetherRandomLoc() {
        Location loc = null;
        while (loc == null) {
            int boundX = Initializer.RANDOM.nextInt(-1500, 1500);
            int boundZ = Initializer.RANDOM.nextInt(-1500, 1500);
            for (int y = 30; y < 128; y++) {
                if (d0.getBlockAt(boundX, y, boundZ).getType() == Material.AIR && d0.getBlockAt(boundX, y - 1, boundZ).isSolid()) {
                    loc = new Location(d0, boundX, y, boundZ);
                    break;
                }
            }
        }
        return loc;
    }

    private void registerCommands() {
        for (CommandHolder command : new CommandHolder[]{new CommandHolder("msg", new Msg()), new CommandHolder("reply", new Reply()), new CommandHolder("tpa", new Tpa()), new CommandHolder("tpaall", new TpaAll()), new CommandHolder("tpaccept", new Tpaccept()), new CommandHolder("tpahere", new Tpahere()), new CommandHolder("tpdeny", new TpDeny()), new CommandHolder("report", new Report()), new CommandHolder("msglock", new MsgLock()), new CommandHolder("tpalock", new TpaLock()), new CommandHolder("spawn", new Spawn()), new CommandHolder("discord", new Discord()), new CommandHolder("warp", new Warp()), new CommandHolder("setwarp", new SetWarp()), new CommandHolder("playtime", new PlayTime()), new CommandHolder("kit", new Kit()), new CommandHolder("ec", new EnderChest()), new CommandHolder("setrank", new SetRank()), new CommandHolder("rgc", new CreateRegion()), new CommandHolder("grindstone", new GrindStone()), new CommandHolder("list", new List()), new CommandHolder("broadcast", new Broadcast()), new CommandHolder("bombrtp", new BombRTP()), new CommandHolder("banip", new Ban()), new CommandHolder("suicide", new Suicide()), new CommandHolder("fastcrystals", new FastCrystals()), new CommandHolder("irename", new ItemRename())})
            getCommand(command.getName()).setExecutor(command.getClazz());
        TeleportCompleter tabCompleter = new TeleportCompleter();
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
        economyHandler = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class).getProvider();
        Bukkit.getScheduler().runTaskLater(this, () -> {
            d = Bukkit.getWorld("world");
            d0 = Bukkit.getWorld("world_nether");
            d1 = Bukkit.getWorld("world_the_end");
            nmsOverworld = ((CraftWorld) d).getHandle();
            chunkSource = nmsOverworld.getChunkSource();
            Utils.lightEngine = chunkSource.getLightEngine();
            WorldBorder wb = d0.getWorldBorder();
            wb.setCenter(0D, 0D);
            wb.setSize(12500D);
            wb = d1.getWorldBorder();
            wb.setCenter(0D, 0D);
            wb.setSize(100000D);
            spawn = new Location(d, -0.5D, 140.0D, 0.5D, 90F, 0F);
            for (short i = 0; i < 101; i++) {
                if (i == 100) {
                    getCommand("rtp").setExecutor(new RTP());
                    Bukkit.getScheduler().scheduleSyncRepeatingTask(p, () -> {
                        for (Entity ent : d.getEntities()) {
                            if (ent instanceof EnderCrystal || ent instanceof Arrow) ent.remove();
                        }
                        // flat
                        Utils.setCuboid(new short[][] {
                                new short[] { 44, 136, 45, -46, 134, 24 },
                                new short[] { 23, 136, -45, 44, 134, 23 },
                                new short[] { -46, 136, -24, 22, 134, -45 },
                                new short[] { -46, 136, -231, -25, 134, 23 }
                                },
                                Blocks.AIR,
                                Blocks.AIR.defaultBlockState());
                        if (!flatSwitch) {
                            String key;
                            for (Map.Entry<String, CustomPlayerDataHolder> D0 : playerData.entrySet()) {
                                key = D0.getKey();
                                if (Bukkit.getPlayer(key) != null) playerData.remove(key);
                            }

                            // arena air
                            Utils.setCuboid(new short[][] {
                                            new short[] { -47, 136, -111, -112, 134, 111 },
                                            new short[] { -46, 136, -111, 110, 134, -46 },
                                            new short[] { 45, 136, 111, 110, 134, -45 },
                                            new short[] { -46, 136, 111, 44, 134, 46 }
                                    },
                                    Blocks.AIR,
                                    Blocks.AIR.defaultBlockState());
                            // grass layer
                            Utils.setArea(133, new short[][] {
                                            new short[] { -47, -111, -112, 111 },
                                            new short[] { -46, -111, 110, -46 },
                                            new short[] { 45, 111, 110, -45 },
                                            new short[] { -46, 111, 44, 46 }
                                    },
                                    Blocks.GRASS_BLOCK.defaultBlockState());
                            // dirt layer
                            Utils.setCuboid(new short[][] {
                                            new short[] { -47, 132, -111, -112, 129, 111 },
                                            new short[] { -46, 132, -111, 110, 129, -46 },
                                            new short[] { 45, 132, 111, 110, 129, -45 },
                                            new short[] { -46, 132, 111, 44, 129, 46 }
                                    },
                                    Blocks.DIRT,
                                    Blocks.DIRT.defaultBlockState());
                            // stone layer
                            Utils.setCuboid(new short[][] {
                                            new short[] { -47, 128, -111, -112, 3, 111 },
                                            new short[] { -46, 128, -111, 110, 3, -46 },
                                            new short[] { 45, 128, 111, 110, 3, -45 },
                                            new short[] { -46, 128, 111, 44, 3, 46 }
                                    },
                                    Blocks.STONE,
                                    Blocks.STONE.defaultBlockState());

                            Location loc;
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                if (p.isGliding()) continue;
                                loc = p.getLocation();
                                loc.setY(200);
                                if (Economy.d.getBlockAt(loc).getType() != Material.BARRIER) continue;
                                loc.setY(135);
                                p.teleport(loc);
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
                }, i);
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