package main;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import main.commands.economy.Balance;
import main.commands.economy.Baltop;
import main.commands.economy.Kit;
import main.commands.economy.Pay;
import main.commands.essentials.List;
import main.commands.essentials.*;
import main.commands.tpa.*;
import main.commands.warps.*;
import main.utils.*;
import main.utils.arenas.Utils;
import main.utils.arenas.*;
import main.utils.instances.CommandHolder;
import main.utils.instances.CustomPlayerDataHolder;
import main.utils.instances.HomeHolder;
import main.utils.optimizer.InteractionListeners;
import main.utils.optimizer.LastPacketEvent;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import static main.utils.Initializer.*;

public class Economy extends JavaPlugin {
    public static File dataFolder;
    public static World d;
    public static World d0;
    public static World d1;
    public static FileConfiguration config;
    private static boolean alreadySavingData;
    private final Point spawnDistance = new Point(0, 0);
    private File dataFile;
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

    private Location getNetherRandomLoc(World w) {
        Location loc = null;
        while (loc == null) {
            int boundX = Initializer.RANDOM.nextInt(-1500, 1500);
            int boundZ = Initializer.RANDOM.nextInt(-1500, 1500);

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

    private void registerCommands() {
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
                new CommandHolder("rpay", new Pay()),
                new CommandHolder("rbaltop", new Baltop()),
                new CommandHolder("anvil", new Anvil()),
                new CommandHolder("grindstone", new GrindStone()),
                new CommandHolder("list", new List()),
                new CommandHolder("broadcast", new Broadcast()),
                new CommandHolder("bombrtp", new BombRTP()),
                new CommandHolder("banip", new Ban()),
                new CommandHolder("areset", new ResetCommand()),
                new CommandHolder("suicide", new Suicide())
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
            Arena arena;
            try {
                byte[] readBytes = null;
                byte[] bytes = Files.readAllBytes(file.toPath());
                Inflater decompresser = new Inflater();
                decompresser.setInput(bytes);

                try {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(bytes.length);
                    decompresser.setInput(bytes);
                    byte[] buffer = new byte[1024];
                    while (!decompresser.finished()) {
                        int count = decompresser.inflate(buffer);
                        outputStream.write(buffer, 0, count);
                    }
                    readBytes = outputStream.toByteArray();
                } catch (DataFormatException ignored) {
                }

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
        dataFile = new File(dataFolder, "data.yml");
        config = YamlConfiguration.loadConfiguration(dataFile);
        Bukkit.getScheduler().runTaskLater(this, () -> {
            d = Bukkit.getWorld("world");
            d0 = Bukkit.getWorld("world_nether");
            d1 = Bukkit.getWorld("world_the_end");
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
                        Arena.arenas.get(arena == 1 ? "ffa1" : "ffa2").reset(100000);
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
                Bukkit.getScheduler().runTaskLater(this, () -> {
                    overworldRTP.add(getRandomLoc(d, 5000));
                    netherRTP.add(getNetherRandomLoc(d0));
                    endRTP.add(getRandomLoc(d1, 1500));
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
                if (!value.getHomes().isEmpty()) {
                    StringBuilder finalstr = new StringBuilder();
                    for (HomeHolder k : value.getHomes()) {
                        Location loc = k.getLocation();
                        finalstr.append(k.getName()).append(":").append(loc.getWorld().getName()).append("m").append(loc.getX()).append(":").append(loc.getY()).append(":").append(loc.getZ()).append(":").append(loc.getYaw()).append(":").append(loc.getPitch()).append(":;");
                    }
                    config.set("r." + key + ".5", finalstr.toString());
                } else
                    config.set("r." + key + ".5", "null");
            }
        }
        try {
            config.save(dataFile);
        } catch (IOException ignored) {
        }
        alreadySavingData = false;
    }
}