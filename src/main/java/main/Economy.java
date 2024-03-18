package main;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import main.commands.BombRTP;
import main.commands.FastCrystals;
import main.commands.economy.*;
import main.commands.essentials.List;
import main.commands.essentials.*;
import main.commands.tpa.*;
import main.commands.warps.*;
import main.utils.*;
import main.utils.arenas.Utils;
import main.utils.arenas.*;
import main.utils.instances.CommandHolder;
import main.utils.optimizer.InteractionListeners;
import main.utils.optimizer.LastPacketEvent;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import static main.utils.Initializer.*;

public class Economy extends JavaPlugin {
    public static File dataFolder;
    public static World d;
    public static World d0;
    public static World d1;
    private final Point spawnDistance = new Point(0, 0);
    private int arena = 1;

    private Location getRandomLoc(World w, int x) {
        Location loc = null;
        while (loc == null) {
            int boundX = Initializer.RANDOM.nextInt(-x, x);
            int boundZ = Initializer.RANDOM.nextInt(-x, x);
            if (spawnDistance.distance(boundX, boundZ) < 128)
                continue;
            Block b = w.getHighestBlockAt(boundX, boundZ);
            if (b.isSolid())
                loc = new Location(w, boundX, b.getY() + 1, boundZ);
        }
        return loc;
    }

    private Location getNetherRandomLoc() {
        Location loc = null;
        while (loc == null) {
            int boundX = Initializer.RANDOM.nextInt(-1500, 1500);
            int boundZ = Initializer.RANDOM.nextInt(-1500, 1500);
            for (int y = 30; y < 128; y++) {
                if (d0.getBlockAt(boundX, y, boundZ).getType() == Material.AIR &&
                        d0.getBlockAt(boundX, y - 1, boundZ).isSolid()) {
                    loc = new Location(d0, boundX, y, boundZ);
                    break;
                }
            }
        }
        return loc;
    }

    private void registerCommands() {
        for (CommandHolder command : new CommandHolder[]{
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
                new CommandHolder("acreate", new main.utils.arenasc.CreateCommand()),
                new CommandHolder("enderchest", new EnderChest()),
                new CommandHolder("setrank", new SetRank()),
                new CommandHolder("rgc", new CreateRegion()),
                new CommandHolder("rpay", new Pay()),
                new CommandHolder("rbaltop", new Baltop()),
                new CommandHolder("anvil", new Anvil()),
                new CommandHolder("grindstone", new GrindStone()),
                new CommandHolder("list", new List()),
                new CommandHolder("broadcast", new Broadcast()),
                new CommandHolder("bombrtp", new BombRTP()),
                new CommandHolder("banip", new Ban()),
                new CommandHolder("areset", new ResetCommand()),
                new CommandHolder("suicide", new Suicide()),
                new CommandHolder("fastcrystals", new FastCrystals()),
                new CommandHolder("bounty", new Bounty())
        })
            getCommand(command.getName()).setExecutor(command.getClazz());
        TeleportCompleter tabCompleter = new TeleportCompleter();
        this.getCommand("tpa").setTabCompleter(tabCompleter);
        this.getCommand("tpaccept").setTabCompleter(tabCompleter);
        this.getCommand("tpahere").setTabCompleter(tabCompleter);
        this.getCommand("tpdeny").setTabCompleter(tabCompleter);

        HomeCompleter homeCompleter = new HomeCompleter();
        this.getCommand("rhome").setTabCompleter(homeCompleter);
        this.getCommand("rdelhome").setTabCompleter(homeCompleter);
    }

    private void setupArenas() {
        for (File file : new File(dataFolder, "arenas").listFiles()) {
            Arena arena;
            try {
                byte[] readBytes = Files.readAllBytes(file.toPath());
                int firstSectionSplit = ArrayUtils.indexOf(readBytes, (byte) '\u0002');
                byte[] header = Arrays.copyOfRange(readBytes, 0, firstSectionSplit);
                String headerString = new String(header, StandardCharsets.US_ASCII);
                String name = headerString.split(",")[0];
                int xx1 = Integer.parseInt(headerString.split(",")[1]);
                int yy1 = Integer.parseInt(headerString.split(",")[2]);
                int zz1 = Integer.parseInt(headerString.split(",")[3]);

                World w = Bukkit.getWorld("world");
                Location corner1 = new Location(w, xx1, yy1, zz1);

                int xx2 = Integer.parseInt(headerString.split(",")[4]);
                int yy2 = Integer.parseInt(headerString.split(",")[5]);
                int zz2 = Integer.parseInt(headerString.split(",")[6]);

                Location corner2 = new Location(w, xx2, yy2, zz2);
                int keySectionSplit = ArrayUtils.indexOf(readBytes, (byte) '\u0002', firstSectionSplit + 1);
                byte[] keyBytes = Arrays.copyOfRange(readBytes, firstSectionSplit + 1, keySectionSplit);
                java.util.List<Material> blockDataSet = new ArrayList<>();

                for (byte[] key : Utils.split(new byte[]{'\u0003'}, keyBytes)) {
                    String blockData = new String(key, StandardCharsets.US_ASCII);
                    try {
                        blockDataSet.add(Material.valueOf(blockData));
                    } catch (IllegalArgumentException e) {
                        try {
                            blockDataSet.add(Material.valueOf(blockData.split("\\[")[0]));
                        } catch (IllegalArgumentException ignored) {
                            return;
                        }
                    }
                }

                arena = new Arena(name, corner1, corner2);
                arena.setKeys(blockDataSet);
                byte[] blockBytes = Arrays.copyOfRange(readBytes, keySectionSplit + 1, readBytes.length);

                ByteBuffer bb = ByteBuffer.allocate(2);
                bb.put(blockBytes[0]);
                bb.put(blockBytes[1]);
                short sectionCount = bb.getShort(0);
                short currentSection = 0;
                blockBytes = Arrays.copyOfRange(blockBytes, 2, blockBytes.length);

                ByteBuffer buffer = ByteBuffer.allocate(blockBytes.length);
                buffer.put(blockBytes);
                buffer.position(0);

                while (currentSection < sectionCount) {
                    int x1 = buffer.getInt();
                    int y1 = buffer.getInt();
                    int z1 = buffer.getInt();
                    int x2 = buffer.getInt();
                    int y2 = buffer.getInt();
                    int z2 = buffer.getInt();
                    Location start = new Location(corner1.getWorld(), x1, y1, z1);
                    Location end = new Location(corner1.getWorld(), x2, y2, z2);
                    int left = buffer.getInt();
                    int numLeft = left / 2;

                    short[] amounts = new short[numLeft];
                    short[] types = new short[numLeft];

                    for (int i = 0; i < numLeft; i++) {
                        amounts[i] = buffer.getShort();
                        types[i] = buffer.getShort();
                    }

                    arena.getSections().add(new Section(arena, currentSection, start, end, types, amounts));
                    currentSection++;
                }
            } catch (Exception ignored) {
                arena = null;
            }
            if (arena != null) Arena.arenas.put(arena.getName(), arena);
        }
    }

    private void registerPacketListeners() {
        PacketEvents.getAPI().getEventManager().registerListeners(
                new InteractionListeners(),
                new LastPacketEvent(),
                new AntiAutoTotem()
        );
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
        economyHandler = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class).getProvider();
        Bukkit.getScheduler().runTaskLater(this, () -> {
            d = Bukkit.getWorld("world");
            d0 = Bukkit.getWorld("world_nether");
            d1 = Bukkit.getWorld("world_the_end");
            WorldBorder wb = d0.getWorldBorder();
            wb.setCenter(0D, 0D);
            wb.setSize(12500D);
            wb = d1.getWorldBorder();
            wb.setCenter(0D, 0D);
            wb.setSize(100000D);
            spawn = new Location(d, -0.5D, 140.0D, 0.5D, 90F, 0F);
            for (int i = 0; i < 101; i++) {
                if (i == 100) {
                    getCommand("rtp").setExecutor(new RTP());
                    Bukkit.getScheduler().scheduleSyncRepeatingTask(p, () -> {
                        for (Entity ent : d.getEntities()) {
                            if (ent instanceof EnderCrystal)
                                ent.remove();
                        }

                        if (arena++ == 3)
                            arena = 1;
                        Arena.arenas.get(arena == 1 ? "ffa1" : "ffa2").reset(1000000);
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
                    return;
                }
                int finalI = i;
                Bukkit.getScheduler().runTaskLater(this, () -> {
                    overworldRTP[finalI] = getRandomLoc(d, 5000);
                    netherRTP[finalI] = getNetherRandomLoc();
                    endRTP[finalI] = getRandomLoc(d1, 1500);
                }, i);
            }
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
    }
}